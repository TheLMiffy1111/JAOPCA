package thelm.jaopca.fluids;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import org.apache.commons.lang3.tuple.Pair;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.api.fluids.PlaceableFluid;
import thelm.jaopca.api.fluids.PlaceableFluidBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public class JAOPCAFluid extends PlaceableFluid implements IMaterialFormFluid {

	private final IForm form;
	private final IMaterial material;
	protected final IFluidFormSettings settings;

	protected BlockRenderLayer renderLayer;
	protected OptionalInt tickRate = OptionalInt.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected Optional<Boolean> canSourcesMultiply = Optional.empty();
	protected Optional<Boolean> canFluidBeDisplaced = Optional.empty();
	protected OptionalInt levelDecreasePerBlock = OptionalInt.empty();

	public JAOPCAFluid(IForm form, IMaterial material, IFluidFormSettings settings) {
		super(settings.getMaxLevelFunction().applyAsInt(material));
		this.form = form;
		this.material = material;
		this.settings = settings;

		renderLayer = settings.getRenderLayer();
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
	public BlockRenderLayer getRenderLayer() {
		return renderLayer;
	}

	@Override
	public int getTickRate(IWorldReader world) {
		if(!tickRate.isPresent()) {
			tickRate = OptionalInt.of(settings.getTickRateFunction().applyAsInt(material));
		}
		return tickRate.getAsInt();
	}

	@Override
	protected float getExplosionResistance() {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(material));
		}
		return (float)explosionResistance.getAsDouble();
	}

	protected boolean canSourcesMultiply() {
		if(!canSourcesMultiply.isPresent()) {
			canSourcesMultiply = Optional.of(settings.getCanSourcesMultiplyFunction().test(material));
		}
		return canSourcesMultiply.get();
	}

	protected int getLevelDecreasePerBlock(IWorldReader world) {
		if(!levelDecreasePerBlock.isPresent()) {
			levelDecreasePerBlock = OptionalInt.of(settings.getLevelDecreasePerBlockFunction().applyAsInt(material));
		}
		return levelDecreasePerBlock.getAsInt();
	}

	@Override
	protected FluidAttributes createAttributes() {
		return settings.getFluidAttributesCreator().create(this, settings);
	}

	@Override
	public Item getFilledBucket() {
		return FluidFormType.INSTANCE.getMaterialFormInfo(form, material).getBucketItem();
	}

	@Override
	protected PlaceableFluidBlock getFluidBlock() {
		return (PlaceableFluidBlock)FluidFormType.INSTANCE.getMaterialFormInfo(form, material).getMaterialFormFluidBlock().asBlock();
	}

	@Override
	public IFluidState getSourceState() {
		return getDefaultState().with(levelProperty, maxLevel);
	}
}
