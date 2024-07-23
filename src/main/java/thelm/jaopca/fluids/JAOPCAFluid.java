package thelm.jaopca.fluids;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.minecraft.item.EnumRarity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCAFluid extends Fluid implements IMaterialFormFluid {

	private final IForm form;
	private final IMaterial material;
	protected final IFluidFormSettings settings;

	protected Supplier<EnumRarity> rarity;
	protected IntSupplier opacity;
	protected Supplier<String> translationKey;

	public JAOPCAFluid(IForm form, IMaterial material, IFluidFormSettings settings) {
		super(MiscHelper.INSTANCE.getFluidName(form.getSecondaryName(), material.getName()));
		this.form = form;
		this.material = material;
		this.settings = settings;

		setLuminosity(settings.getLuminosityFunction().applyAsInt(material));
		setDensity(settings.getDensityFunction().applyAsInt(material));
		setTemperature(settings.getTemperatureFunction().applyAsInt(material));
		setViscosity(settings.getViscosityFunction().applyAsInt(material));
		setGaseous(settings.getIsGaseousFunction().test(material));

		rarity = MemoizingSuppliers.of(settings.getDisplayRarityFunction(), ()->material);
		opacity = MemoizingSuppliers.of(settings.getOpacityFunction(), ()->material);
		translationKey = MemoizingSuppliers.of(()->"fluid.jaopca."+MiscHelper.INSTANCE.toLowercaseUnderscore(material.getName()));
	}

	@Override
	public IForm getForm() {
		return form;
	}

	@Override
	public IMaterial getIMaterial() {
		return material;
	}

	@Override
	public EnumRarity getRarity() {
		return rarity.get();
	}

	public int getOpacity() {
		return opacity.getAsInt();
	}

	@Override
	public int getColor() {
		return material.getColor() & 0xFFFFFF | getOpacity() << 24;
	}

	@Override
	public String getUnlocalizedName() {
		return translationKey.get();
	}

	@Override
	public String getLocalizedName(FluidStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("fluid.jaopca."+getForm().getName(), getIMaterial(), getUnlocalizedName());
	}
}
