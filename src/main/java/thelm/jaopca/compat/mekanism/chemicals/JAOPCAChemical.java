package thelm.jaopca.compat.mekanism.chemicals;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.mekanism.api.chemicals.IChemicalFormSettings;
import thelm.jaopca.compat.mekanism.api.chemicals.IMaterialFormChemical;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCAChemical extends Chemical implements IMaterialFormChemical {

	private final IForm form;
	private final IMaterial material;
	protected final IChemicalFormSettings settings;

	public JAOPCAChemical(IForm form, IMaterial material, IChemicalFormSettings settings) {
		super(ChemicalBuilder.builder(ResourceLocation.fromNamespaceAndPath("jaopca", "chemical/"+material.getModelType()+'/'+form.getName())));
		this.form = form;
		this.material = material;
		this.settings = settings;
	}

	@Override
	public IForm getForm() {
		return form;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	@Override
	public int getTint() {
		return 0xFFFFFF & material.getColor();
	}

	@Override
	public ResourceLocation getIcon() {
		if(MiscHelper.INSTANCE.hasResource(
				getRegistryName().withPath("textures/chemical/"+getRegistryName().getPath()+".png"))) {
			return getRegistryName().withPath("chemical/"+getRegistryName().getPath());
		}
		return super.getIcon();
	}

	@Override
	public Component getTextComponent() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("chemical.jaopca."+form.getName(), material, getTranslationKey());
	}

	@Override
	public ResourceLocation getRegistryName() {
		return MekanismAPI.CHEMICAL_REGISTRY.getKey(this);
	}
}
