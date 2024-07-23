package thelm.jaopca.compat.mekanism.slurries;

import java.util.function.Supplier;

import com.google.common.base.Strings;

import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.functions.MemoizingSuppliers;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.compat.mekanism.api.slurries.IMaterialFormSlurry;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryFormSettings;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCASlurry extends Slurry implements IMaterialFormSlurry {

	private final IForm form;
	private final IMaterial material;
	protected final ISlurryFormSettings settings;

	protected Supplier<TagKey<Item>> oreTag;

	public JAOPCASlurry(IForm form, IMaterial material, ISlurryFormSettings settings) {
		super(SlurryBuilder.builder(ResourceLocation.fromNamespaceAndPath("jaopca", "slurry/"+material.getModelType()+'/'+form.getName())));
		this.form = form;
		this.material = material;
		this.settings = settings;

		oreTag = MemoizingSuppliers.of(()->{
			String tag = settings.getOreTagFunction().apply(material);
			return Strings.isNullOrEmpty(tag) ? null : MiscHelper.INSTANCE.getItemTagKey(ResourceLocation.parse(tag));
		});
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
	public TagKey<Item> getOreTag() {
		return oreTag.get();
	}

	@Override
	public int getTint() {
		return 0xFFFFFF & material.getColor();
	}

	@Override
	public ResourceLocation getIcon() {
		if(MiscHelper.INSTANCE.hasResource(
				getRegistryName().withPath("textures/slurry/"+getRegistryName().getPath()+".png"))) {
			return getRegistryName().withPath("slurry/"+getRegistryName().getPath());
		}
		return super.getIcon();
	}

	@Override
	public Component getTextComponent() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterialForm("slurry.jaopca."+form.getName(), material, getTranslationKey());
	}
}
