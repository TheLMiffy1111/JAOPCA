package thelm.jaopca.fluids;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormBucketItem;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCABucketItem extends Item implements IMaterialFormBucketItem {

	private final IMaterialFormFluid fluid;
	private final IFluidFormSettings settings;

	protected IntSupplier itemStackLimit;
	protected BooleanSupplier hasEffect;
	protected Supplier<EnumRarity> rarity;
	protected Supplier<String> translationKey;

	public JAOPCABucketItem(IMaterialFormFluid fluid, IFluidFormSettings settings) {
		this.fluid = fluid;
		this.settings = settings;

		itemStackLimit = MemoizingSuppliers.of(settings.getItemStackLimitFunction(), fluid::getIMaterial);
		hasEffect = MemoizingSuppliers.of(settings.getHasEffectFunction(), fluid::getIMaterial);
		rarity = MemoizingSuppliers.of(settings.getDisplayRarityFunction(), fluid::getIMaterial);
		translationKey = MemoizingSuppliers.of(()->{
			String name = itemRegistry.getNameForObject(this);
			return "item."+name.replaceFirst(":", ".").replace('/', '.');
		});
	}

	@Override
	public IForm getForm() {
		return fluid.getForm();
	}

	@Override
	public IMaterial getIMaterial() {
		return fluid.getIMaterial();
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return itemStackLimit.getAsInt();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return hasEffect.getAsBoolean() || super.hasEffect(stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return rarity.get();
	}

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, false);
		if(mop == null) {
			return stack;
		}
		else {
			FillBucketEvent event = new FillBucketEvent(player, stack, world, mop);
			if(MinecraftForge.EVENT_BUS.post(event)) {
				return stack;
			}
			if(event.getResult() == Event.Result.ALLOW) {
				if(player.capabilities.isCreativeMode) {
					return stack;
				}
				if(--stack.stackSize <= 0) {
					return event.result;
				}
				if(!player.inventory.addItemStackToInventory(event.result)) {
					player.dropPlayerItemWithRandomChoice(event.result, false);
				}
				return stack;
			}
			if(mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				int x = mop.blockX;
				int y = mop.blockY;
				int z = mop.blockZ;
				if(!world.canMineBlock(player, x, y, z)) {
					return stack;
				}
				switch(mop.sideHit) {
				case 0: --y; break;
				case 1: ++y; break;
				case 2: --z; break;
				case 3: ++z; break;
				case 4: --x; break;
				case 5: ++x; break;
				}
				if(!player.canPlayerEdit(x, y, z, mop.sideHit, stack)) {
					return stack;
				}
				if(tryPlaceContainedLiquid(world, x, y, z) && !player.capabilities.isCreativeMode) {
					return new ItemStack(Items.bucket);
				}
			}
			return stack;
		}
	}

	public boolean tryPlaceContainedLiquid(World world, int x, int y, int z) {
		Material material = world.getBlock(x, y, z).getMaterial();
		boolean flag = !material.isSolid();
		if(!world.isAirBlock(x, y, z) && !flag) {
			return false;
		}
		else {
			if(!world.isRemote && flag && !material.isLiquid()) {
				world.func_147480_a(x, y, z, true);
			}
			world.setBlock(x, y, z, fluid.toFluid().getBlock(), 0, 3);
			return true;
		}
	}

	@Override
	public String getUnlocalizedName() {
		return translationKey.get();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedName();
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("item.jaopca."+getForm().getName(), getIMaterial(), getUnlocalizedName(stack));
	}

	@SideOnly(Side.CLIENT)
	protected IIcon overlayIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister) {
		if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/items/"+getForm().getName()+'.'+getIMaterial().getName()+".png"))) {
			itemIcon = iconRegister.registerIcon("jaopca:"+getForm().getName()+'.'+getIMaterial().getName());
		}
		else {
			itemIcon = iconRegister.registerIcon("jaopca:"+getIMaterial().getModelType()+'/'+getForm().getName());
		}
		if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/items/"+getForm().getName()+'.'+getIMaterial().getName()+"_overlay.png"))) {
			overlayIcon = iconRegister.registerIcon("jaopca:"+getForm().getName()+'.'+getIMaterial().getName());
		}
		else if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/items/"+getIMaterial().getModelType()+'/'+getForm().getName()+"_overlay.png"))) {
			overlayIcon = iconRegister.registerIcon("jaopca:"+getIMaterial().getModelType()+'/'+getForm().getName()+"_overlay");
		}
		else {
			overlayIcon = null;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() {
		return overlayIcon != null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
		return pass == 0 ? itemIcon : overlayIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return pass == 0 ? getIMaterial().getColor() : 0xFFFFFF;
	}
}
