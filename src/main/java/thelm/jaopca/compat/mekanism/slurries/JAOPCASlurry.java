package thelm.jaopca.compat.mekanism.slurries;

import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.mekanism.api.slurries.IMaterialFormSlurry;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryFormSettings;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCASlurry extends Slurry implements IMaterialFormSlurry {

	private final IForm form;
	private final IMaterial material;
	protected final ISlurryFormSettings settings;

	protected boolean isHidden;

	public JAOPCASlurry(IForm form, IMaterial material, ISlurryFormSettings settings) {
		super(SlurryBuilder.builder(new ResourceLocation("jaopca", "slurry/"+material.getModelType()+'/'+form.getName())));
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
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("slurry.jaopca."+form.getName(), material, getTranslationKey());
	}
}
