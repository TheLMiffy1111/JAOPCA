package thelm.jaopca.modules.compat.silentsmechanisms;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;

@JAOPCAModule(modDependencies = "silents_mechanisms")
public class SilentsMechanismsIngotModule implements IModule {

	private static final Set<String> BLACKLIST = Sets.newHashSet(
			"aluminum", "bismuth", "copper", "gold", "iron", "lead", "nickel", "platinum", "silver", "super_useless",
			"tin", "uranium", "useless", "zinc");

	static {
		if(ModList.get().isLoaded("silentgear")) {
			Collections.addAll(BLACKLIST, "crimson_iron");
		}
	}

	private static final TreeMap<IMaterial, Item> BYPRODUCTS = new TreeMap<>();

	private final IForm chunkForm = JAOPCAApi.instance().newForm(this, "silents_mechanisms_chunks", JAOPCAApi.instance().itemFormType()).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("chunks").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "silents_mechanisms_ingot";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder builder = ImmutableSetMultimap.builder();
		builder.put(0, "dusts");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(chunkForm.toRequest());
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		for(IMaterial material : chunkForm.getMaterials()) {
			IDynamicSpecConfig config = configs.get(material);
			String byproduct = config.getDefinedString("silents_mechanisms.byproduct", "minecraft:cobblestone",
					s->ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s)), "The byproduct material to output in Silent's Mechanisms' Crusher.");
			BYPRODUCTS.put(material, ForgeRegistries.ITEMS.getValue(new ResourceLocation(byproduct)));
		}
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = JAOPCAApi.instance();
		SilentsMechanismsHelper helper = SilentsMechanismsHelper.INSTANCE;
		for(IMaterial material : chunkForm.getMaterials()) {
			ResourceLocation oreLocation = api.miscHelper().getTagLocation("ores", material.getName());
			IItemInfo chunksInfo = api.itemFormType().getMaterialFormInfo(chunkForm, material);
			ResourceLocation dustLocation = api.miscHelper().getTagLocation("dusts", material.getName());
			ResourceLocation materialLocation = api.miscHelper().getTagLocation(material.getType().getFormName(), material.getName());
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "silents_mechanisms.ore_to_chunks."+material.getName()), oreLocation, 400, new Object[] {
							chunksInfo, 2,
							BYPRODUCTS.get(material), 1, 0.1F,
					});
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "silents_mechanisms.chunks_to_dust."+material.getName()), chunksInfo, 300, new Object[] {
							dustLocation, 1,
							dustLocation, 1, 0.1F,
					});
			api.registerFurnaceRecipe(
					new ResourceLocation("jaopca", "silents_mechanisms.chunks_to_material."+material.getName()),
					chunksInfo, materialLocation, 1, 0.7F, 200);
			api.registerBlastingRecipe(
					new ResourceLocation("jaopca", "silents_mechanisms.chunks_to_material_blasting."+material.getName()),
					chunksInfo, materialLocation, 1, 0.7F, 100);
		}
	}
}
