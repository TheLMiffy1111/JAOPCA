package thelm.jaopca.fluids;

import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;

public class JAOPCABucketResourceHandler extends ItemAccessResourceHandler<FluidResource> {

	public JAOPCABucketResourceHandler(ItemAccess access) {
		super(access, 1);
	}

	@Override
	protected FluidResource getResourceFrom(ItemResource accessResource, int index) {
		if(accessResource.getItem() instanceof IMaterialFormBucketItem materialForm) {
			return FluidResource.of(FluidFormType.INSTANCE.getMaterialFormInfo(materialForm.getForm(), materialForm.getMaterial()).asFluid());
		}
		return FluidResource.EMPTY;
	}

	@Override
	protected int getAmountFrom(ItemResource accessResource, int index) {
		FluidResource resource = getResourceFrom(accessResource, index);
		return resource.isEmpty() ? 0 : FluidType.BUCKET_VOLUME;
	}

	@Override
	protected ItemResource update(ItemResource accessResource, int index, FluidResource newResource, int newAmount) {
		if(newAmount == 0) {
			return ItemResource.of(Items.BUCKET);
		}
		else if(newAmount != 1000) {
			return ItemResource.EMPTY;
		}
		else {
			FluidStack newStack = newResource.toStack(newAmount);
			return ItemResource.of(newStack.getFluidType().getBucket(newStack));
		}
	}

	@Override
	protected int getCapacity(int index, FluidResource resource) {
		return 1000;
	}
}
