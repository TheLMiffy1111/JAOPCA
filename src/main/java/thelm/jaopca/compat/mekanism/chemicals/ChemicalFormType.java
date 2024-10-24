package thelm.jaopca.compat.mekanism.chemicals;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import com.google.common.collect.Tables;
import com.google.common.collect.TreeBasedTable;
import com.mojang.serialization.Codec;

import mekanism.api.MekanismAPI;
import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.mekanism.MekanismDataInjector;
import thelm.jaopca.compat.mekanism.MekanismHelper;
import thelm.jaopca.compat.mekanism.api.chemicals.IChemicalFormSettings;
import thelm.jaopca.compat.mekanism.api.chemicals.IChemicalFormType;
import thelm.jaopca.compat.mekanism.api.chemicals.IChemicalInfo;
import thelm.jaopca.compat.mekanism.api.chemicals.IMaterialFormChemical;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class ChemicalFormType implements IChemicalFormType {

	private ChemicalFormType() {};

	public static final ChemicalFormType INSTANCE = new ChemicalFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, Supplier<IMaterialFormChemical>> CHEMICALS = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IChemicalInfo> CHEMICAL_INFOS = TreeBasedTable.create();
	private static boolean registered = false;

	public static void init() {
		FormTypeHandler.registerFormType(INSTANCE);
	}

	@Override
	public String getName() {
		return "chemical";
	}

	@Override
	public void addForm(IForm form) {
		FORMS.add(form);
	}

	@Override
	public Set<IForm> getForms() {
		return Collections.unmodifiableNavigableSet(FORMS);
	}

	@Override
	public boolean shouldRegister(IForm form, IMaterial material) {
		ResourceLocation tagLocation = MiscHelper.INSTANCE.getTagLocation(form.getSecondaryName(), material.getName());
		return !MekanismHelper.INSTANCE.getChemicalTags().contains(tagLocation);
	}

	@Override
	public IChemicalInfo getMaterialFormInfo(IForm form, IMaterial material) {
		IChemicalInfo info = CHEMICAL_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new ChemicalInfo(CHEMICALS.get(form, material).get());
			CHEMICAL_INFOS.put(form, material, info);
		}
		return info;
	}

	@Override
	public IChemicalFormSettings getNewSettings() {
		return new ChemicalFormSettings();
	}

	@Override
	public Codec<IFormSettings> formSettingsCodec() {
		return ChemicalCustomCodecs.CHEMICAL_FORM_SETTINGS;
	}

	@Override
	public void registerMaterialForms() {
		if(registered) {
			return;
		}
		registered = true;
		JAOPCAApi api = ApiImpl.INSTANCE;
		IMiscHelper helper = MiscHelper.INSTANCE;
		for(IForm form : FORMS) {
			IChemicalFormSettings settings = (IChemicalFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			for(IMaterial material : form.getMaterials()) {
				String name = form.getName()+'.'+material.getName();
				ResourceLocation registryName = ResourceLocation.fromNamespaceAndPath("jaopca", name);

				Supplier<IMaterialFormChemical> materialFormChemical = MemoizingSuppliers.of(()->settings.getChemicalCreator().create(form, material, settings));
				CHEMICALS.put(form, material, materialFormChemical);
				api.registerRegistryEntry(MekanismAPI.CHEMICAL_REGISTRY_NAME, name, ()->materialFormChemical.get().toChemical());

				MekanismDataInjector.registerChemicalTag(helper.createResourceLocation(secondaryName), registryName);
				MekanismDataInjector.registerChemicalTag(helper.getTagLocation(secondaryName, material.getName()), registryName);
				for(String alternativeName : material.getAlternativeNames()) {
					MekanismDataInjector.registerChemicalTag(helper.getTagLocation(secondaryName, alternativeName), registryName);
				}
			}
		}
	}

	public static Collection<IMaterialFormChemical> getSlurries() {
		return Tables.transformValues(CHEMICALS, Supplier::get).values();
	}
}
