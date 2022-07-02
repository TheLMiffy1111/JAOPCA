package thelm.jaopca.compat.mekanism.gases;

import java.util.Optional;

import mekanism.api.gas.Gas;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.mekanism.api.gases.IGasFormSettings;
import thelm.jaopca.compat.mekanism.api.gases.IMaterialFormGas;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCAGas extends Gas implements IMaterialFormGas {

	private final IForm form;
	private final IMaterial material;
	protected final IGasFormSettings settings;

	protected boolean isHidden;
	protected Optional<String> translationKey = Optional.empty();

	public JAOPCAGas(IForm form, IMaterial material, IGasFormSettings settings) {
		super(MiscHelper.INSTANCE.getFluidName(form.getSecondaryName(), material.getName()),
				new ResourceLocation("jaopca", "gas/"+material.getModelType()+'/'+form.getName()));
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
	public boolean isVisible() {
		return !isHidden;
	}

	@Override
	public int getTint() {
		return 0xFF000000 | material.getColor();
	}

	@Override
	public String getTranslationKey() {
		if(!translationKey.isPresent()) {
			translationKey = Optional.of("gas.jaopca."+MiscHelper.INSTANCE.toLowercaseUnderscore(material.getName()));
		}
		return translationKey.get();
	}

	@Override
	public String getLocalizedName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("gas.jaopca."+form.getName(), material, getTranslationKey());
	}
}
