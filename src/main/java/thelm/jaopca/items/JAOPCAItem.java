package thelm.jaopca.items;

import java.util.Optional;
import java.util.OptionalInt;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.items.IItemFormSettings;
import thelm.jaopca.api.items.IMaterialFormItem;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.client.renderer.JAOPCAItemRenderer;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCAItem extends Item implements IMaterialFormItem {

	private final IForm form;
	private final IMaterial material;
	protected final IItemFormSettings settings;

	protected OptionalInt itemStackLimit = OptionalInt.empty();
	protected Optional<Boolean> hasEffect = Optional.empty();
	protected Optional<EnumRarity> rarity = Optional.empty();
	protected OptionalInt burnTime = OptionalInt.empty();
	protected Optional<String> translationKey = Optional.empty();

	public JAOPCAItem(IForm form, IMaterial material, IItemFormSettings settings) {
		this.form = form;
		this.material = material;
		this.settings = settings;
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
	public int getItemStackLimit(ItemStack stack) {
		if(!itemStackLimit.isPresent()) {
			itemStackLimit = OptionalInt.of(settings.getItemStackLimitFunction().applyAsInt(material));
		}
		return itemStackLimit.getAsInt();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		if(!hasEffect.isPresent()) {
			hasEffect = Optional.of(settings.getHasEffectFunction().test(material));
		}
		return hasEffect.get() || super.hasEffect(stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		if(!rarity.isPresent()) {
			rarity = Optional.of(settings.getDisplayRarityFunction().apply(material));
		}
		return rarity.get();
	}

	@Override
	public String getUnlocalizedName() {
		if(!translationKey.isPresent()) {
			String name = itemRegistry.getNameForObject(this);
			translationKey = Optional.of("item."+name.replaceFirst(":", ".").replace('/', '.'));
		}
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
	@Override
	public IItemRenderer getRenderer() {
		return JAOPCAItemRenderer.INSTANCE;
	}

	@SideOnly(Side.CLIENT)
	protected IIcon overlayIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister) {
		if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/items/"+form.getName()+'.'+material.getName()+".png"))) {
			itemIcon = iconRegister.registerIcon("jaopca:"+form.getName()+'.'+material.getName());
		}
		else {
			itemIcon = iconRegister.registerIcon("jaopca:"+material.getModelType()+'/'+form.getName());
		}
		if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/items/"+form.getName()+'.'+material.getName()+"_overlay.png"))) {
			overlayIcon = iconRegister.registerIcon("jaopca:"+form.getName()+'.'+material.getName());
		}
		else if(MiscHelper.INSTANCE.hasResource(new ResourceLocation("jaopca", "textures/items/"+material.getModelType()+'/'+form.getName()+"_overlay.png"))) {
			overlayIcon = iconRegister.registerIcon("jaopca:"+material.getModelType()+'/'+form.getName()+"_overlay");
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
		return pass == 0 ? material.getColor() : 0xFFFFFF;
	}
}
