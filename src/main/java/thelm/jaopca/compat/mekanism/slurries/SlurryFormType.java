package thelm.jaopca.compat.mekanism.slurries;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.Tables;
import com.google.common.collect.TreeBasedTable;
import com.mojang.serialization.Codec;

import mekanism.api.MekanismAPI;
import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.mekanism.MekanismDataInjector;
import thelm.jaopca.compat.mekanism.MekanismHelper;
import thelm.jaopca.compat.mekanism.api.slurries.IMaterialFormSlurry;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryFormSettings;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryFormType;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryInfo;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.registries.RegistryHandler;
import thelm.jaopca.utils.MiscHelper;

public class SlurryFormType implements ISlurryFormType {

	private SlurryFormType() {};

	public static final SlurryFormType INSTANCE = new SlurryFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, Supplier<IMaterialFormSlurry>	> SLURRIES = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, ISlurryInfo> SLURRY_INFOS = TreeBasedTable.create();
	private static boolean registered = false;

	public static void init() {
		FormTypeHandler.registerFormType(INSTANCE);
	}

	@Override
	public String getName() {
		return "slurry";
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
		return !MekanismHelper.INSTANCE.getSlurryTags().contains(tagLocation);
	}

	@Override
	public ISlurryInfo getMaterialFormInfo(IForm form, IMaterial material) {
		ISlurryInfo info = SLURRY_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new SlurryInfo(SLURRIES.get(form, material).get());
			SLURRY_INFOS.put(form, material, info);
		}
		return info;
	}

	@Override
	public ISlurryFormSettings getNewSettings() {
		return new SlurryFormSettings();
	}

	@Override
	public Codec<IFormSettings> formSettingsCodec() {
		return SlurryCustomCodecs.SLURRY_FORM_SETTINGS;
	}

	@Override
	public void registerMaterialForms() {
		if(registered) {
			return;
		}
		registered = true;
		MiscHelper helper = MiscHelper.INSTANCE;
		for(IForm form : FORMS) {
			ISlurryFormSettings settings = (ISlurryFormSettings)form.getSettings();
			String secondaryName = form.getSecondaryName();
			for(IMaterial material : form.getMaterials()) {
				String name = form.getName()+'.'+material.getName();
				ResourceLocation registryName = new ResourceLocation("jaopca", name);

				Supplier<IMaterialFormSlurry> materialFormSlurry = Suppliers.memoize(()->settings.getSlurryCreator().create(form, material, settings));
				SLURRIES.put(form, material, materialFormSlurry);
				RegistryHandler.registerRegistryEntry(MekanismAPI.SLURRY_REGISTRY_NAME, name, ()->materialFormSlurry.get().toSlurry());

				MekanismDataInjector.registerSlurryTag(helper.createResourceLocation(secondaryName), registryName);
				MekanismDataInjector.registerSlurryTag(helper.getTagLocation(secondaryName, material.getName()), registryName);
				for(String alternativeName : material.getAlternativeNames()) {
					MekanismDataInjector.registerSlurryTag(helper.getTagLocation(secondaryName, alternativeName), registryName);
				}
			}
		}
	}

	public static Collection<IMaterialFormSlurry> getSlurries() {
		return Tables.transformValues(SLURRIES, Supplier::get).values();
	}
}
