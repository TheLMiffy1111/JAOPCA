package thelm.jaopca.compat.hbm.ntmmaterials;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.hbm.inventory.material.Mats;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.forms.IFormType;
import thelm.jaopca.api.materialforms.IMaterialFormInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialColorEvent;
import thelm.jaopca.forms.FormTypeHandler;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

/**
 * This form type only supports a single form.
 */
public class NTMMaterialFormType implements IFormType {

	private NTMMaterialFormType() {}

	public static final NTMMaterialFormType INSTANCE = new NTMMaterialFormType();
	private static IForm form = null;
	private static final TreeMap<IMaterial, JAOPCANTMMaterial> NTM_MATERIALS = new TreeMap<>();
	private static final TreeMap<IMaterial, IMaterialFormInfo> NTM_MATERIAL_INFOS = new TreeMap<>();
	private static boolean registered = false;
	private static int id = 25000;

	public static void init() {
		FormTypeHandler.registerFormType(INSTANCE);
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}

	@Override
	public String getName() {
		return "ntm_material";
	}

	@Override
	public void addForm(IForm f) {
		if(form == null) {
			form = f;
		}
	}

	@Override
	public Set<IForm> getForms() {
		return form != null ? Collections.singleton(form) : Collections.emptySet();
	}

	@Override
	public boolean shouldRegister(IForm form, IMaterial material) {
		return !Mats.matByName.containsKey(material.getName());
	}

	@Override
	public IMaterialFormInfo getMaterialFormInfo(IForm f, IMaterial material) {
		IMaterialFormInfo info = NTM_MATERIAL_INFOS.get(material);
		if(info == null && form.getMaterials().contains(material)) {
			info = new NTMMaterialInfo(NTM_MATERIALS.get(material));
			NTM_MATERIAL_INFOS.put(material, info);
		}
		return info;
	}

	@Override
	public IFormSettings getNewSettings() {
		return new NTMMaterialFormSettings();
	}

	@Override
	public GsonBuilder configureGsonBuilder(GsonBuilder builder) {
		return builder;
	}

	@Override
	public IFormSettings deserializeSettings(JsonElement jsonElement, JsonDeserializationContext context) {
		throw new UnsupportedOperationException("Cannot be used in custom");
	}

	@Override
	public void registerMaterialForms() {
		if(registered) {
			return;
		}
		registered = true;
		ApiImpl api = ApiImpl.INSTANCE;
		MiscHelper helper = MiscHelper.INSTANCE;
		if(form != null) {
			for(IMaterial material : form.getMaterials()) {
				while(Mats.matById.containsKey(id)) {
					++id;
				}
				JAOPCANTMMaterial ntmMaterial = new JAOPCANTMMaterial(id++, form, material);
				NTM_MATERIALS.put(material, ntmMaterial);
			}
		}
	}

	@SubscribeEvent
	public void onMaterialColor(MaterialColorEvent event) {
		if(NTM_MATERIALS.containsKey(event.getMaterial())) {
			NTM_MATERIALS.get(event.getMaterial()).setColors();
		}
	}

	public static Collection<JAOPCANTMMaterial> getNTMMaterials() {
		return NTM_MATERIALS.values();
	}
}
