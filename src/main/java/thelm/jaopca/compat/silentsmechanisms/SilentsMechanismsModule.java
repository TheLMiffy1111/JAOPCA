package thelm.jaopca.compat.silentsmechanisms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "silents_mechanisms")
public class SilentsMechanismsModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"aluminum", "bismuth", "copper", "gold", "iron", "lead", "nickel", "platinum", "silver", "super_useless",
			"tin", "uranium", "useless", "zinc"));

	static {
		if(ModList.get().isLoaded("silentgear")) {
			Collections.addAll(BLACKLIST, "azure_silver", "crimson_iron");
		}
	}

	private Map<IMaterial, IDynamicSpecConfig> configs;

	private final IForm chunkForm = ApiImpl.INSTANCE.newForm(this, "silents_mechanisms_chunks", ItemFormType.INSTANCE).
			setMaterialTypes(MaterialType.INGOT).setSecondaryName("silents_mechanisms:chunks").setDefaultMaterialBlacklist(BLACKLIST);

	@Override
	public String getName() {
		return "silents_mechanisms";
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
		this.configs = configs;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		SilentsMechanismsHelper helper = SilentsMechanismsHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : chunkForm.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			IItemInfo chunksInfo = ItemFormType.INSTANCE.getMaterialFormInfo(chunkForm, material);
			ResourceLocation chunksLocation = miscHelper.getTagLocation("silents_mechanisms:chunks", material.getName());
			ResourceLocation dustLocation = miscHelper.getTagLocation("dusts", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("silents_mechanisms.byproduct", "minecraft:cobblestone",
					s->ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s)), "The byproduct material to output in Silent's Mechanisms' Crusher.");
			Item byproduct = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configByproduct));

			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "silents_mechanisms.ore_to_chunks."+material.getName()), oreLocation, 400, new Object[] {
							chunksInfo, 2,
							byproduct, 1, 0.1F,
					});
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "silents_mechanisms.chunks_to_dust."+material.getName()), chunksLocation, 300, new Object[] {
							dustLocation, 1,
							dustLocation, 1, 0.1F,
					});
			api.registerSmeltingRecipe(
					new ResourceLocation("jaopca", "silents_mechanisms.chunks_to_material."+material.getName()),
					chunksLocation, materialLocation, 1, 0.7F, 200);
			api.registerBlastingRecipe(
					new ResourceLocation("jaopca", "silents_mechanisms.chunks_to_material_blasting."+material.getName()),
					chunksLocation, materialLocation, 1, 0.7F, 100);
		}
	}
}
