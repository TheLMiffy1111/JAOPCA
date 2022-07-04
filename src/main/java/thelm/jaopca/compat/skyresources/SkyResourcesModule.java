package thelm.jaopca.compat.skyresources;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.bartz24.skyresources.registry.ModFluids;
import com.bartz24.skyresources.registry.ModItems;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "skyresources")
public class SkyResourcesModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Ardite", "Boron", "Chrome", "Cobalt", "Copper", "Draconium", "Gold", "Iridium",
			"Iron", "Lead", "Lithium", "Magnesium", "Mithril", "Nickel", "Osmium", "Platinum", "Silver", "Thorium",
			"Tin", "Titanium", "Tungsten", "Uranium", "Yellorium", "Zinc"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	private final IForm alchDustForm = ApiImpl.INSTANCE.newForm(this, "skyresources_alch_dust", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("skyresources:alchDust").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "skyresources";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dust");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(alchDustForm.toRequest());
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		SkyResourcesHelper helper = SkyResourcesHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : alchDustForm.getMaterials()) {
			String dustOredict = miscHelper.getOredictName("dust", material.getName());
			IItemInfo alchDustInfo = itemFormType.getMaterialFormInfo(alchDustForm, material);

			IDynamicSpecConfig config = configs.get(material);
			int rarity = config.getDefinedInt("skyresources.rarity", 6, i->i>0, "The rarity of this material as used in Sky Resources.");

			Object oreItemDust = getOreItemDust(rarity);
			helper.registerFusionRecipe(
					miscHelper.getRecipeKey("skyresources.dust_to_alch_dust", material.getName()),
					alchDustInfo, 1, rarity*0.0021F, new Object[] {
							dustOredict, 1, oreItemDust, 2,
					});
		}
		for(IMaterial material : moduleData.getMaterials()) {
			String alchDustOredict = miscHelper.getOredictName("skyresources:alchDust", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String dustOredict = miscHelper.getOredictName("dust", material.getName());

			IDynamicSpecConfig config = configs.get(material);
			int rarity = config.getIntOrElse("skyresources.rarity", 6);
			String configOreBase = config.getDefinedString("skyresources.oreBase", "minecraft:stone",
					this::isOredictOrItemValid, "The default base to use in Sky Resources' condenser to create ores.");
			Object oreBase = getOredictOrItem(configOreBase);
			String configDustBase = config.getDefinedString("skyresources.dustBase", "skyresources:techitemcomponent@0",
					this::isOredictOrItemValid, "The item to use in Sky Resources' cauldron cleaning to dust.");
			Object dustBase = getOredictOrItem(configDustBase);

			helper.registerCondenserRecipe(
					miscHelper.getRecipeKey("skyresources.alch_dust_to_material", material.getName()),
					alchDustOredict, ModFluids.crystalFluid, true, materialOredict, 1, (float)Math.pow(1.4, rarity)*50);
			helper.registerCondenserRecipe(
					miscHelper.getRecipeKey("skyresources.alch_dust_to_ore", material.getName()),
					alchDustOredict, oreBase, false, oreOredict, 1, (float)Math.pow(1.72, rarity)*62);
			helper.registerCauldronCleanRecipe(
					miscHelper.getRecipeKey("skyresources.dust_cauldron_clean", material.getName()),
					dustBase, dustOredict, 1, 1/(float)Math.pow(rarity+2.5, 3.7));
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("dustalch", "skyresources_alch_dust");
		return builder.build();
	}

	public Object getOreItemDust(int rarity) {
		if(rarity <= 2) {
			return Items.GUNPOWDER;
		}
		if(rarity <= 4) {
			return Items.BLAZE_POWDER;
		}
		if(rarity <= 6) {
			return Items.GLOWSTONE_DUST;
		}
		if(rarity <= 8) {
			return new ItemStack(Items.DYE, 1, 4);
		}
		return new ItemStack(ModItems.baseComponent, 1, 3);
	}

	public boolean isOredictOrItemValid(String s) {
		if(StringUtils.startsWith(s, "#")) {
			return ApiImpl.INSTANCE.getOredict().contains(s.substring(1));
		}
		else {
			return ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s.split("@(?=\\d+)")[0]));
		}
	}

	public Object getOredictOrItem(String s) {
		if(StringUtils.startsWith(s, "#")) {
			return s.substring(1);
		}
		else {
			return MiscHelper.INSTANCE.parseMetaItem(s);
		}
	}
}
