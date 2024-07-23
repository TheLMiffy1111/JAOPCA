package thelm.jaopca.compat.essentialcraft;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import essentialcraft.api.OreSmeltingRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialColorEvent;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "essentialcraft")
public class EssentialCraftModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>();

	private Map<IMaterial, OreSmeltingRecipe> addedRecipes = new TreeMap<>();
	private boolean colorsLoaded;

	public EssentialCraftModule() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public String getName() {
		return "essentialcraft";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.copyOf(Arrays.asList(MaterialType.ORE));
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		if(BLACKLIST.isEmpty()) {
			OreSmeltingRecipe.RECIPES.stream().map(r->r.oreName).
			filter(n->n.startsWith("ore")).map(n->n.substring(3)).forEach(BLACKLIST::add);
		}
		return BLACKLIST;
	}

	@Override
	public void onInit(IModuleData moduleData, FMLInitializationEvent event) {
		EssentialCraftHelper helper = EssentialCraftHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		for(IMaterial material : moduleData.getMaterials()) {
			String oreOredict = miscHelper.getOredictName("ore", material.getName());
			String materialOredict = miscHelper.getOredictName(material.getType().getFormName(), material.getName());
			helper.registerMagmaticSmelterRecipe(
					miscHelper.getRecipeKey("essentialcraft.magmatic_smelter", material.getName()),
					oreOredict, materialOredict, (material.getType().isDust() ? 2 : 1),
					r->addedRecipes.put(material, r));
		}
	}

	@SubscribeEvent
	public void onMaterialColor(MaterialColorEvent event) {
		if(addedRecipes.containsKey(event.getMaterial())) {
			addedRecipes.get(event.getMaterial()).color = event.getColor();
		}
	}
}
