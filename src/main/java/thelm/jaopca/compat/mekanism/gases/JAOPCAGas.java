package thelm.jaopca.compat.mekanism.gases;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.mekanism.api.gases.IGasFormSettings;
import thelm.jaopca.compat.mekanism.api.gases.IMaterialFormGas;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCAGas extends Gas implements IMaterialFormGas {

	private final IForm form;
	private final IMaterial material;
	protected final IGasFormSettings settings;

	protected boolean isHidden;

	public JAOPCAGas(IForm form, IMaterial material, IGasFormSettings settings) {
		super(GasAttributes.builder(new ResourceLocation("jaopca", "gas/"+material.getModelType()+'/'+form.getName())));
		this.form = form;
		this.material = material;
		this.settings = settings;

		isHidden = settings.getIsHidden();
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
	public boolean isHidden() {
		return isHidden;
	}

	@Override
	public int getTint() {
		return 0xFFFFFF & material.getColor();
	}

	@Override
	public ITextComponent getTextComponent() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("gas.jaopca."+form.getName(), material, getTranslationKey());
	}
}
