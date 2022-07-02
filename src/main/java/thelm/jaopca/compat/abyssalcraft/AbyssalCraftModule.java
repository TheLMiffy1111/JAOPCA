package thelm.jaopca.compat.abyssalcraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableMap;
import com.shinoow.abyssalcraft.api.necronomicon.condition.DimensionCondition;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACLib;

import net.minecraft.block.SoundType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.blocks.IBlockInfo;
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
import thelm.jaopca.blocks.BlockFormType;
import thelm.jaopca.compat.abyssalcraft.blocks.JAOPCAUnlockableBlockItem;
import thelm.jaopca.compat.abyssalcraft.items.JAOPCAUnlockableItem;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "abyssalcraft@[1.9.19,)")
public class AbyssalCraftModule implements IModule {

	private static final Set<String> FORM_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Aluminium", "Aluminum", "Beryllium", "Calcium", "Carbon", "Copper", "Coralium",
			"Dreadium", "Gold", "Iron", "LiquifiedCoralium", "Magnesium", "Potassium", "Silicon", "Tin", "Zinc"));
	private static final Set<String> ORE_TO_CRYSTAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Aluminium", "Aluminum", "Copper", "Coralium", "Gold", "Iron", "LiquifiedCoralium",
			"Tin", "Zinc"));
	private static final Set<String> TRANSMUTE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Aluminium", "Aluminum", "Calcium", "Copper", "Coralium", "Dreadium", "Gold", "Iron",
			"LiquifiedCoralium", "Magnesium", "Tin", "Zinc"));
	private static final Set<String> MATERIALIZE_TO_MATERIAL_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Copper", "Coralium", "Dreadium", "Gold", "Iron", "LiquifiedCoralium", "Tin"));
	private static final Set<String> TO_ORE_BLACKLIST = new TreeSet<>(Arrays.asList(
			"Abyssalnite", "Coralium", "Dreadium", "Gold", "Iron", "LiquifiedCoralium"));

	public AbyssalCraftModule() {
		MinecraftForge.EVENT_BUS.register(AbyssalCraftHelper.INSTANCE);
	}

	private final IForm crystalFragmentForm = ApiImpl.INSTANCE.newForm(this, "abyssalcraft_crystal_fragment", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("crystalFragment").setDefaultMaterialBlacklist(FORM_BLACKLIST).
			setSettings(ItemFormType.INSTANCE.getNewSettings().
					setItemCreator((f, m, s)->
					new JAOPCAUnlockableItem(f, m, s).
					setUnlockCondition(new DimensionCondition(ACLib.dreadlands_id))));
	private final IForm crystalShardForm = ApiImpl.INSTANCE.newForm(this, "abyssalcraft_crystal_shard", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("crystalShard").setDefaultMaterialBlacklist(FORM_BLACKLIST).
			setSettings(ItemFormType.INSTANCE.getNewSettings().
					setItemCreator((f, m, s)->
					new JAOPCAUnlockableItem(f, m, s).
					setUnlockCondition(new DimensionCondition(ACLib.dreadlands_id))));
	private final IForm crystalForm = ApiImpl.INSTANCE.newForm(this, "abyssalcraft_crystal", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("crystal").setDefaultMaterialBlacklist(FORM_BLACKLIST).
			setSettings(ItemFormType.INSTANCE.getNewSettings().
					setItemCreator((f, m, s)->
					new JAOPCAUnlockableItem(f, m, s).
					setUnlockCondition(new DimensionCondition(ACLib.dreadlands_id))));
	private final IForm crystalClusterForm = ApiImpl.INSTANCE.newForm(this, "abyssalcraft_crystal_cluster", BlockFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("crystalCluster").setDefaultMaterialBlacklist(FORM_BLACKLIST).
			setSettings(BlockFormType.INSTANCE.getNewSettings().
					setBlockHardnessFunction(material->0.4).setExplosionResistanceFunction(material->0.8).
					setLightOpacityFunction(material->0).setSoundTypeFunction(material->SoundType.GLASS).
					setHarvestToolFunction(material->"pickaxe").setHarvestLevelFunction(material->3).
					setBoundingBox(new AxisAlignedBB(0.2D, 0.0D, 0.2D, 0.8D, 0.7D, 0.8D)).
					setBlockItemCreator((mf, s)->
					new JAOPCAUnlockableBlockItem(mf, s).
					setUnlockCondition(new DimensionCondition(ACLib.dreadlands_id))));

	@Override
	public String getName() {
		return "abyssalcraft";
	}

	@Override
	public List<IFormRequest> getFormRequests() {
		return Collections.singletonList(ApiImpl.INSTANCE.newFormRequest(this,
				crystalFragmentForm, crystalShardForm, crystalForm, crystalClusterForm).setGrouped(true));
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.INGOT);
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		AbyssalCraftHelper helper = AbyssalCraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		IItemFormType itemFormType = ItemFormType.INSTANCE;
		Set<String> oredict = api.getOredict();
		boolean rework = ACConfig.crystal_rework;
		for(IMaterial material : crystalFragmentForm.getMaterials()) {
			String crystalShardOredict = miscHelper.getOredictName("crystalShard", material.getName());
			IItemInfo crystalFragmentInfo = itemFormType.getMaterialFormInfo(crystalFragmentForm, material);
			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("abyssalcraft.crystal_shard_to_crystal_fragment", material.getName()),
					crystalFragmentInfo, 9, new Object[] {
							crystalShardOredict,
					});
			helper.registerCrystal(crystalFragmentInfo);
			if(!rework) {
				helper.registerFuel(crystalFragmentInfo, 17);
			}
		}
		for(IMaterial material : crystalShardForm.getMaterials()) {
			String crystalFragmentOredict = miscHelper.getOredictName("crystalFragment", material.getName());
			String crystalOredict = miscHelper.getOredictName("crystal", material.getName());
			IItemInfo crystalShardInfo = itemFormType.getMaterialFormInfo(crystalShardForm, material);
			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("abyssalcraft.crystal_fragment_to_crystal_shard", material.getName()),
					crystalShardInfo, 1, new Object[] {
							crystalFragmentOredict, crystalFragmentOredict, crystalFragmentOredict,
							crystalFragmentOredict, crystalFragmentOredict, crystalFragmentOredict,
							crystalFragmentOredict, crystalFragmentOredict, crystalFragmentOredict,
					});
			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("abyssalcraft.crystal_to_crystal_shard", material.getName()),
					crystalShardInfo, 9, new Object[] {
							crystalOredict,
					});
			helper.registerCrystal(crystalShardInfo);
			if(!rework) {
				helper.registerFuel(crystalShardInfo, 150);
			}
		}
		for(IMaterial material : crystalForm.getMaterials()) {
			String crystalShardOredict = miscHelper.getOredictName("crystalShard", material.getName());
			String crystalClusterOredict = miscHelper.getOredictName("crystalCluster", material.getName());
			IItemInfo crystalInfo = itemFormType.getMaterialFormInfo(crystalForm, material);
			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("abyssalcraft.crystal_shard_to_crystal", material.getName()),
					crystalInfo, 1, new Object[] {
							crystalShardOredict, crystalShardOredict, crystalShardOredict,
							crystalShardOredict, crystalShardOredict, crystalShardOredict,
							crystalShardOredict, crystalShardOredict, crystalShardOredict,
					});
			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("abyssalcraft.crystal_cluster_to_crystal", material.getName()),
					crystalInfo, 9, new Object[] {
							crystalClusterOredict,
					});
			helper.registerCrystal(crystalInfo);
			if(!rework) {
				helper.registerFuel(crystalInfo, 1350);
			}
		}
		for(IMaterial material : crystalClusterForm.getMaterials()) {
			String crystalOredict = miscHelper.getOredictName("crystal", material.getName());
			IBlockInfo crystalClusterInfo = BlockFormType.INSTANCE.getMaterialFormInfo(crystalClusterForm, material);
			api.registerShapelessRecipe(
					miscHelper.getRecipeKey("abyssalcraft.crystal_to_crystal_cluster", material.getName()),
					crystalClusterInfo, 1, new Object[] {
							crystalOredict, crystalOredict, crystalOredict,
							crystalOredict, crystalOredict, crystalOredict,
							crystalOredict, crystalOredict, crystalOredict,
					});
			helper.registerCrystal(crystalClusterInfo);
			if(!rework) {
				helper.registerFuel(crystalClusterInfo, 12150);
			}
		}
		for(IMaterial material : moduleData.getMaterials()) {
			String name = material.getName();
			if(!ORE_TO_CRYSTAL_BLACKLIST.contains(name)) {
				String oreOredict = miscHelper.getOredictName("ore", name);
				if(rework) {
					String crystalOredict = miscHelper.getOredictName("crystal", name);
					if(oredict.contains(crystalOredict)) {
						helper.registerCrystallizationRecipe(
								miscHelper.getRecipeKey("abyssalcraft.ore_to_crystal", name),
								oreOredict, crystalOredict, 2, 0.1F);
					}
				}
				else {
					String crystalShardOredict = miscHelper.getOredictName("crystalShard", name);
					if(oredict.contains(crystalShardOredict)) {
						helper.registerCrystallizationRecipe(
								miscHelper.getRecipeKey("abyssalcraft.ore_to_crystal_shard", name),
								oreOredict, crystalShardOredict, 4, 0.1F);
					}
				}
			}
			if(!TRANSMUTE_BLACKLIST.contains(name)) {
				String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), name);
				if(rework) {
					String crystalClusterOredict = miscHelper.getOredictName("crystalCluster", name);
					if(oredict.contains(crystalClusterOredict)) {
						helper.registerTransmutationRecipe(
								miscHelper.getRecipeKey("abyssalcraft.crystal_cluster_to_material", name),
								crystalClusterOredict, materialOredict, 1, 0.2F);
					}
				}
				else {
					String crystalOredict = miscHelper.getOredictName("crystal", name);
					if(oredict.contains(crystalOredict)) {
						helper.registerTransmutationRecipe(
								miscHelper.getRecipeKey("abyssalcraft.crystal_to_material_transmutation", name),
								crystalOredict, materialOredict, 1, 0.2F);
					}
				}
			}
			if(!MATERIALIZE_TO_MATERIAL_BLACKLIST.contains(name)) {
				String crystalOredict = miscHelper.getOredictName("crystal", name);
				String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), name);
				if(oredict.contains(crystalOredict)) {
					helper.registerMaterializationRecipe(
							miscHelper.getRecipeKey("abyssalcraft.crystal_to_material", name),
							materialOredict, 1, new Object[] {
									crystalOredict, 1,
							});
				}
			}
			if(!TO_ORE_BLACKLIST.contains(name)) {
				String crystalOredict = miscHelper.getOredictName("crystal", name);
				String oreOredict = miscHelper.getOredictName("ore", name);
				if(oredict.contains(crystalOredict)) {
					helper.registerMaterializationRecipe(
							miscHelper.getRecipeKey("abyssalcraft.crystal_to_ore", name),
							oreOredict, 1, new Object[] {
									"crystalSilica", 1,
									"crystalMagnesia", 1,
									crystalOredict, 1,
							});
				}
			}
		}
	}

	@Override
	public Map<String, String> getLegacyRemaps() {
		ImmutableMap.Builder builder = ImmutableMap.builder();
		builder.put("crystalfragment", "abyssalcraft_crystal_fragment");
		builder.put("crystalshard", "abyssalcraft_crystal_shard");
		builder.put("crystalabyss", "abyssalcraft_crystal");
		builder.put("crystalcluster", "abyssalcraft_crystal_cluster");
		return builder.build();
	}
}
