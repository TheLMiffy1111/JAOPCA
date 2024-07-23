package thelm.jaopca.fluids;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fluids.FluidAttributes;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.PlaceableFluid;
import thelm.jaopca.api.fluids.PlaceableFluidBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materials.IMaterial;

public class JAOPCAFluid extends PlaceableFluid implements IMaterialFormFluid {

	private final IForm form;
	private final IMaterial material;
	protected final IFluidFormSettings settings;

	protected IntSupplier tickRate;
	protected DoubleSupplier explosionResistance;
	protected BooleanSupplier canSourcesMultiply;
	protected IntSupplier levelDecreasePerBlock;

	public JAOPCAFluid(IForm form, IMaterial material, IFluidFormSettings settings) {
		super(settings.getMaxLevelFunction().applyAsInt(material));
		this.form = form;
		this.material = material;
		this.settings = settings;

		tickRate = MemoizingSuppliers.of(settings.getTickRateFunction(), material);
		explosionResistance = MemoizingSuppliers.of(settings.getExplosionResistanceFunction(), material);
		canSourcesMultiply = MemoizingSuppliers.of(settings.getCanSourcesMultiplyFunction(), material);
		levelDecreasePerBlock = MemoizingSuppliers.of(settings.getLevelDecreasePerBlockFunction(), material);
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
	public int getTickDelay(IWorldReader world) {
		return tickRate.getAsInt();
	}

	@Override
	protected float getExplosionResistance() {
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	protected boolean canSourcesMultiply() {
		return canSourcesMultiply.getAsBoolean();
	}

	@Override
	protected int getLevelDecreasePerBlock(IWorldReader world) {
		return levelDecreasePerBlock.getAsInt();
	}

	@Override
	protected FluidAttributes createAttributes() {
		return settings.getFluidAttributesCreator().create(this, settings);
	}

	@Override
	public Item getBucket() {
		return FluidFormType.INSTANCE.getMaterialFormInfo(form, material).getBucketItem();
	}

	@Override
	protected PlaceableFluidBlock getFluidBlock() {
		return (PlaceableFluidBlock)FluidFormType.INSTANCE.getMaterialFormInfo(form, material).getMaterialFormFluidBlock().toBlock();
	}

	@Override
	public FluidState getSourceState() {
		return defaultFluidState().setValue(levelProperty, maxLevel);
	}
}
