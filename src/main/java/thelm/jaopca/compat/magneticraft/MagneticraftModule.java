package thelm.jaopca.compat.magneticraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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

@JAOPCAModule(modDependencies = "magneticraft", classDependencies = "com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipeManager")
public class MagneticraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"Aluminium", "Aluminum", "Cobalt", "Copper", "Galena", "Gold", "Iron", "Lead", "Mithril", "Nickel",
			"Osmium", "Silver", "Tin", "Tungsten", "Zinc"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	private final IForm rockyChunkForm = ApiImpl.INSTANCE.newForm(this, "magneticraft_rocky_chunk", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("rockyChunk").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm chunkForm = ApiImpl.INSTANCE.newForm(this, "magneticraft_chunk", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("chunk").setDefaultMaterialBlacklist(BLACKLIST);
	private final IFormRequest formRequest = ApiImpl.INSTANCE.newFormRequest(this, rockyChunkForm, chunkForm).setGrouped(true);

	@Override
	public String getName() {
		return "magneticraft";
	}

	@Override
	public Multimap<Integer, String> getModuleDependencies() {
		ImmutableSetMultimap.Builder<Integer, String> builder = ImmutableSetMultimap.builder();
		builder.put(1, "dust");
		builder.put(2, "dust");
		return builder.build();
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(formRequest);
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
		MagneticraftHelper helper = MagneticraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		for(IMaterial material : formRequest.getMaterials()) {
			IItemInfo rockyChunkInfo = itemFormType.getMaterialFormInfo(rockyChunkForm, material);
			String rockyChunkOredict = miscHelper.getOredictName("rockyChunk", material.getName());
			IItemInfo chunkInfo = itemFormType.getMaterialFormInfo(chunkForm, material);
			String chunkOredict = miscHelper.getOredictName("chunk", material.getName());
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String extraDustOredict = miscHelper.getOredictName("dust", material.getExtra(1).getName());
			String secondExtraDustOredict = miscHelper.getOredictName("dust", material.getExtra(2).getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("magneticraft.grinderByproduct", "minecraft:gravel",
					miscHelper.metaItemPredicate(), "The default byproduct material to output in Magneticraft's grinder.");
			ItemStack byproduct = miscHelper.parseMetaItem(configByproduct);

			helper.registerCrushingTableRecipe(
					miscHelper.getRecipeKey("magneticraft.ore_to_rocky_chunk_crushing_table", material.getName()),
					oreOredict, rockyChunkInfo, 1);
			helper.registerGrinderRecipe(
					miscHelper.getRecipeKey("magneticraft.ore_to_rocky_chunk_grinder", material.getName()),
					oreOredict, rockyChunkInfo, 1, byproduct, 1, 0.15F, 50);

			configByproduct = config.getDefinedString("magneticraft.sluiceBoxByproduct", "minecraft:cobblestone",
					miscHelper.metaItemPredicate(), "The default byproduct material to output in Magneticraft's sluice box.");
			byproduct = miscHelper.parseMetaItem(configByproduct);

			if(material.hasExtra(2)) {
				helper.registerSluiceBoxRecipe(
						miscHelper.getRecipeKey("magneticraft.rocky_chunk_to_chunk_sluice_box", material.getName()),
						rockyChunkOredict, new Object[] {
								chunkInfo, 1, 1F, extraDustOredict, 1, 0.15F, secondExtraDustOredict, 1, 0.15F, byproduct, 1, 0.15F,
						});
				helper.registerSieveRecipe(
						miscHelper.getRecipeKey("magneticraft.rocky_chunk_to_chunk_sieve", material.getName()),
						rockyChunkOredict, chunkInfo, 1, 1F, extraDustOredict, 1, 0.15F, secondExtraDustOredict, 1, 0.15F, 50);
			}
			else if(material.hasExtra(1)) {
				helper.registerSluiceBoxRecipe(
						miscHelper.getRecipeKey("magneticraft.rocky_chunk_to_chunk_sluice_box", material.getName()),
						rockyChunkOredict, new Object[] {
								chunkInfo, 1, 1F, extraDustOredict, 1, 0.15F, byproduct, 1, 0.15F,
						});
				helper.registerSieveRecipe(
						miscHelper.getRecipeKey("magneticraft.rocky_chunk_to_chunk_sieve", material.getName()),
						rockyChunkOredict, chunkInfo, 1, 1F, extraDustOredict, 1, 0.15F, 50);
			}
			else {
				helper.registerSluiceBoxRecipe(
						miscHelper.getRecipeKey("magneticraft.rocky_chunk_to_chunk_sluice_box", material.getName()),
						rockyChunkOredict, new Object[] {
								chunkInfo, 1, 1F, byproduct, 1, 0.15F,
						});
				helper.registerSieveRecipe(
						miscHelper.getRecipeKey("magneticraft.rocky_chunk_to_chunk_sieve", material.getName()),
						rockyChunkOredict, chunkInfo, 1, 1F, 50);
			}

			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("magneticraft.rocky_chunk_to_material", material.getName()),
					rockyChunkOredict, materialOredict, 1, 0.1F);
			api.registerSmeltingRecipe(
					miscHelper.getRecipeKey("magneticraft.chunk_to_material", material.getName()),
					chunkOredict, materialOredict, 2, 0.1F);
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		builder.put("rockychunk", "magneticraft_rocky_chunk");
		builder.put("chunk", "magneticraft_chunk");
		return builder.build();
	}
}
