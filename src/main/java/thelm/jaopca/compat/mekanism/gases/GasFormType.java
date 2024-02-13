package thelm.jaopca.compat.mekanism.gases;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.TreeBasedTable;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasRegistry;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.mekanism.api.gases.IGasFormSettings;
import thelm.jaopca.compat.mekanism.api.gases.IGasFormType;
import thelm.jaopca.compat.mekanism.api.gases.IGasInfo;
import thelm.jaopca.compat.mekanism.api.gases.IMaterialFormGas;
import thelm.jaopca.compat.mekanism.custom.json.GasFormSettingsDeserializer;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class GasFormType implements IGasFormType {

	private GasFormType() {}

	public static final GasFormType INSTANCE = new GasFormType();
	private static final TreeSet<IForm> FORMS = new TreeSet<>();
	private static final TreeBasedTable<IForm, IMaterial, IMaterialFormGas> GASES = TreeBasedTable.create();
	private static final TreeBasedTable<IForm, IMaterial, IGasInfo> GAS_INFOS = TreeBasedTable.create();
	private static boolean registered = false;

	public static void init() {
		FormTypeHandler.registerFormType(INSTANCE);
	}

	@Override
	public String getName() {
		return "gas";
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
		String fluidName = MiscHelper.INSTANCE.getFluidName(form.getSecondaryName(), material.getName());
		return GasRegistry.getGas(fluidName) != null;
	}

	@Override
	public IGasInfo getMaterialFormInfo(IForm form, IMaterial material) {
		IGasInfo info = GAS_INFOS.get(form, material);
		if(info == null && FORMS.contains(form) && form.getMaterials().contains(material)) {
			info = new GasInfo(GASES.get(form, material));
			GAS_INFOS.put(form, material, info);
		}
		return info;
	}

	@Override
	public IGasFormSettings getNewSettings() {
		return new GasFormSettings();
	}

	@Override
	public GsonBuilder configureGsonBuilder(GsonBuilder builder) {
		return builder;
	}

	@Override
	public IGasFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context) {
		return GasFormSettingsDeserializer.INSTANCE.deserialize(jsonElement, context);
	}

	@Override
	public void registerMaterialForms() {
		if(registered) {
			return;
		}
		registered = true;
		ApiImpl api = ApiImpl.INSTANCE;
		for(IForm form : FORMS) {
			IGasFormSettings settings = (IGasFormSettings)form.getSettings();
			for(IMaterial material : form.getMaterials()) {
				IMaterialFormGas materialFormGas = settings.getGasCreator().create(form, material, settings);
				GASES.put(form, material, materialFormGas);
				Gas gas = materialFormGas.toGas();
				GasRegistry.register(gas);

				api.registerTextures(0, ()->{
					if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/blocks/"+form.getName()+'.'+material.getName()+".png"))) {
						return Collections.singletonList("jaopca:"+form.getName()+'.'+material.getName());
					}
					else {
						return Collections.singletonList("jaopca:"+material.getModelType()+'/'+form.getName());
					}
				}, list->{
					gas.setIcon(list.get(0));
				});
			}
		}
	}

	public static Collection<IMaterialFormGas> getGases() {
		return GASES.values();
	}
}
