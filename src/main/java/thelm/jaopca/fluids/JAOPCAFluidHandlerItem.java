package thelm.jaopca.fluids;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;

public class JAOPCAFluidHandlerItem implements IFluidHandlerItem {

	protected final IMaterialFormFluid fluid;
	protected ItemStack container;

	public JAOPCAFluidHandlerItem(IMaterialFormFluid fluid, ItemStack container) {
		this.fluid = fluid;
		this.container = container;
	}

	@Override
	public ItemStack getContainer() {
		return container;
	}

	public FluidStack getFluid() {
		return new FluidStack(fluid.toFluid(), FluidType.BUCKET_VOLUME);
	}

	protected void clearFluid() {
		container = new ItemStack(Items.BUCKET);
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return FluidType.BUCKET_VOLUME;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return true;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action)  {
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if(container.getCount() != 1 || resource.getAmount() < FluidType.BUCKET_VOLUME) {
			return FluidStack.EMPTY;
		}
		FluidStack fluidStack = getFluid();
		if(!fluidStack.isEmpty() && fluidStack.isFluidEqual(resource)) {
			if(action.execute()) {
				clearFluid();
			}
			return fluidStack;
		}
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if(container.getCount() != 1 || maxDrain < FluidType.BUCKET_VOLUME) {
			return FluidStack.EMPTY;
		}
		FluidStack fluidStack = getFluid();
		if(!fluidStack.isEmpty()) {
			if(action.execute()) {
				clearFluid();
			}
			return fluidStack;
		}
		return FluidStack.EMPTY;
	}
}
