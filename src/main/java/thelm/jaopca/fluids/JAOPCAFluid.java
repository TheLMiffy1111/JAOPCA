package thelm.jaopca.fluids;

import java.util.Optional;
import java.util.OptionalInt;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCAFluid extends Fluid implements IMaterialFormFluid {

	private final IForm form;
	private final IMaterial material;
	protected final IFluidFormSettings settings;

	protected Optional<Boolean> gaseous = Optional.empty();
	protected Optional<EnumRarity> rarity = Optional.empty();
	protected OptionalInt opacity = OptionalInt.empty();
	protected Optional<String> translationKey = Optional.empty();

	public JAOPCAFluid(IForm form, IMaterial material, IFluidFormSettings settings) {
		super(MiscHelper.INSTANCE.getFluidName(form.getSecondaryName(), material.getName()),
				new ResourceLocation("jaopca", "fluid/"+material.getModelType()+'/'+form.getName()+"_still"),
				new ResourceLocation("jaopca", "fluid/"+material.getModelType()+'/'+form.getName()+"_flow"));
		this.form = form;
		this.material = material;
		this.settings = settings;

		setLuminosity(settings.getLuminosityFunction().applyAsInt(material));
		setDensity(settings.getDensityFunction().applyAsInt(material));
		setTemperature(settings.getTemperatureFunction().applyAsInt(material));
		setViscosity(settings.getViscosityFunction().applyAsInt(material));
		setGaseous(settings.getIsGaseousFunction().test(material));
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
	public EnumRarity getRarity() {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.getDisplayRarityFunction().apply(material));
		}
		return rarity.get();
	}

	@Override
	public SoundEvent getFillSound() {
		return settings.getFillSoundSupplier().get();
	}

	@Override
	public SoundEvent getEmptySound() {
		return settings.getEmptySoundSupplier().get();
	}

	public int getOpacity() {
		if(!opacity.isPresent()) {
			opacity = OptionalInt.of(settings.getOpacityFunction().applyAsInt(material));
		}
		return opacity.getAsInt();
	}

	@Override
	public int getColor() {
		return material.getColor() & 0xFFFFFF | getOpacity() << 24;
	}

	@Override
	public String getUnlocalizedName() {
		if(!translationKey.isPresent()) {
			translationKey = Optional.of("fluid.jaopca."+MiscHelper.INSTANCE.toLowercaseUnderscore(material.getName()));
		}
		return translationKey.get();
	}

	@Override
	public String getLocalizedName(FluidStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("fluid.jaopca."+getForm().getName(), getMaterial(), getUnlocalizedName());
	}
}
