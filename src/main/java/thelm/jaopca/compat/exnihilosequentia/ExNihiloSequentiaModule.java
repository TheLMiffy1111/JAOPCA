package thelm.jaopca.compat.exnihilosequentia;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
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

@JAOPCAModule(modDependencies = "exnihilosequentia")
public class ExNihiloSequentiaModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "aluminium", "copper", "gold", "iron", "lead", "nickel", "platinum", "silver", "tin",
			"uranium", "zinc"));

	private static boolean registerOreTags = true;

	static {
		if(ModList.get().isLoaded("exnihilomekanism")) {
			Collections.addAll(BLACKLIST, "osmium");
		}
	}

	private Map<IMaterial, IDynamicSpecConfig> configs;

	private final IForm pieceForm = ApiImpl.INSTANCE.newForm(this, "exnihilosequentia_pieces", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("exnihilosequentia:pieces").setDefaultMaterialBlacklist(BLACKLIST);
	private final IForm chunkForm = ApiImpl.INSTANCE.newForm(this, "exnihilosequentia_chunks", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("exnihilosequentia:chunks").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "exnihilosequentia";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(ApiImpl.INSTANCE.newFormRequest(this, pieceForm, chunkForm));
	}

	@Override
	public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
		registerOreTags = config.getDefinedBoolean("tags.registerOreTags", registerOreTags, "Should the module register ore tags for chunks.");
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		ExNihiloSequentiaHelper helper = ExNihiloSequentiaHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		Item gravel = Items.GRAVEL;
		Item crushedNetherrack = ForgeRegistries.ITEMS.getValue(new ResourceLocation("exnihilosequentia:crushed_netherrack"));
		Item crushedEndstone = ForgeRegistries.ITEMS.getValue(new ResourceLocation("exnihilosequentia:crushed_end_stone"));
		for(IMaterial material : pieceForm.getMaterials()) {
			IItemInfo pieceInfo = itemFormType.getMaterialFormInfo(pieceForm, material);

			IDynamicSpecConfig config = configs.get(material);
			boolean sieveGravel = config.getDefinedBoolean("exnihilosequentia.sieveGravel", true,
					"Should gravel sieve recipes be added.");
			boolean sieveCrushedNetherrack = config.getDefinedBoolean("exnihilosequentia.sieveCrushedNetherrack", false,
					"Should crushed netherrack sieve recipes be added.");
			boolean sieveCrushedEndstone = config.getDefinedBoolean("exnihilosequentia.sieveCrushedEndstone", false,
					"Should crushed end stone sieve recipes be added.");
			float sieveFlintChance = config.getDefinedFloat("exnihilosequentia.sieveFlintChance", 0F, 1F, 0.05F,
					"The output chance of the sieve recipes added using a flint mesh.");
			float sieveIronChance = config.getDefinedFloat("exnihilosequentia.sieveIronChance", 0F, 1F, 0.075F,
					"The output chance of the sieve recipes added using an iron mesh.");
			float sieveDiamondChance = config.getDefinedFloat("exnihilosequentia.sieveDiamondChance", 0F, 1F, 0.1F,
					"The output chance of the sieve recipes added using a diamond mesh.");

			if(sieveGravel) {
				helper.registerSieveRecipe(
						new ResourceLocation("jaopca", "exnihilosequentia.piece_gravel."+material.getName()),
						gravel, pieceInfo, 1, new Number[] {
								2, sieveFlintChance,
								3, sieveIronChance,
								4, sieveDiamondChance,
						}, false);
			}
			if(sieveCrushedNetherrack) {
				helper.registerSieveRecipe(
						new ResourceLocation("jaopca", "exnihilosequentia.piece_crushed_netherrack."+material.getName()),
						crushedNetherrack, pieceInfo, 1, new Number[] {
								2, sieveFlintChance,
								3, sieveIronChance,
								4, sieveDiamondChance,
						}, false);
			}
			if(sieveCrushedEndstone) {
				helper.registerSieveRecipe(
						new ResourceLocation("jaopca", "exnihilosequentia.piece_crushed_end_stone."+material.getName()),
						crushedEndstone, pieceInfo, 1, new Number[] {
								2, sieveFlintChance,
								3, sieveIronChance,
								4, sieveDiamondChance,
						}, false);
			}
		}
		for(IMaterial material : chunkForm.getMaterials()) {
			ResourceLocation pieceLocation = miscHelper.getTagLocation("exnihilosequentia:pieces", material.getName());
			IItemInfo chunkInfo = ItemFormType.INSTANCE.getMaterialFormInfo(chunkForm, material);
			ResourceLocation chunkLocation = miscHelper.getTagLocation("exnihilosequentia:chunks", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());

			api.registerShapelessRecipe(
					new ResourceLocation("jaopca", "exnihilosequentia.piece_to_chunk."+material.getName()),
					chunkInfo, 1, new Object[] {
							pieceLocation, pieceLocation,
							pieceLocation, pieceLocation,
					});
			api.registerSmeltingRecipe(
					new ResourceLocation("jaopca", "exnihilosequentia.chunk_to_material."+material.getName()),
					chunkLocation, materialLocation, 1, 0.7F, 200);
			api.registerBlastingRecipe(
					new ResourceLocation("jaopca", "exnihilosequentia.chunk_to_material_blasting."+material.getName()),
					chunkLocation, materialLocation, 1, 0.7F, 100);

			if(registerOreTags) {
				api.registerItemTag(miscHelper.getTagLocation("ores", material.getName()), chunkInfo.asItem());
			}
		}
	}
}
