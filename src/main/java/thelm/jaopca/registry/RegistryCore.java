package thelm.jaopca.registry;

import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IModule;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.block.BlockBase;
import thelm.jaopca.fluid.FluidBase;
import thelm.jaopca.item.ItemBase;
import thelm.jaopca.item.ItemBlockBase;
import thelm.jaopca.modules.ModuleDust;
import thelm.jaopca.modules.ModuleIC2;
import thelm.jaopca.modules.ModuleMekanism;
import thelm.jaopca.modules.ModuleNugget;
import thelm.jaopca.modules.ModuleTConstruct;

public class RegistryCore {

	public static void preInit() {
		registerBuiltInModules();

		initItemEntries();
		initItemBlacklist();
		initEntryToOreMap();

		registerItems();
		registerBlocks();
		registerFluids();
		registerCustoms();

		setProperties();
	}

	public static void init() {
		registerRecipes();
	}

	private static void registerBuiltInModules() {
		JAOPCAApi.registerModule(new ModuleDust());
		JAOPCAApi.registerModule(new ModuleNugget());
		if(Loader.isModLoaded("Mekanism"))
			JAOPCAApi.registerModule(new ModuleMekanism());
		if(Loader.isModLoaded("tconstruct"))
			JAOPCAApi.registerModule(new ModuleTConstruct());
		if(Loader.isModLoaded("IC2"))
			JAOPCAApi.registerModule(new ModuleIC2());
	}

	private static void initItemEntries() {
		for(IModule module : JAOPCAApi.MODULE_LIST) {
			List<ItemEntry> entries = module.getItemRequests();
			for(ItemEntry entry : entries) {
				if(!JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.containsKey(entry.name)) {
					JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.put(entry.name, entry);
					JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.put(entry.type, entry);
					JAOPCAApi.ITEM_ENTRY_LIST.add(entry);
				}

				JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.get(entry.name).moduleList.add(module);
			}
		}
	}

	private static void initItemBlacklist() {
		for(ItemEntry entry : JAOPCAApi.ITEM_ENTRY_LIST) {
			for(IOreEntry ore : JAOPCAApi.ORE_ENTRY_LIST) {
				if(entry.type == EnumEntryType.BLOCK || entry.type == EnumEntryType.ITEM) {
					if(!OreDictionary.getOres(entry.prefix+ore.getOreName()).isEmpty()) {
						entry.blacklist.add(ore.getOreName());
					}
				}

				for(String moduleName : ore.getModuleBlacklist()) {
					if(entry.moduleList.contains(JAOPCAApi.NAME_TO_MODULE_MAP.get(moduleName))) {
						entry.blacklist.add(ore.getOreName());
					}
				}
			}
		}
	}

	private static void initEntryToOreMap() {
		for(ItemEntry entry : JAOPCAApi.ITEM_ENTRY_LIST) {
			LinkedHashSet<IOreEntry> oreSet = Sets.<IOreEntry>newLinkedHashSet();
			JAOPCAApi.ORE_ENTRY_LIST.stream().filter((oreEntry) -> {
				return !entry.blacklist.contains(oreEntry.getOreName());
			}).forEach((oreEntry) -> {
				oreSet.add(oreEntry);
			});

			JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.putAll(entry.name, oreSet);
		}
	}

	private static void registerItems() {
		for(ItemEntry entry : JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.get(EnumEntryType.ITEM)) {
			for(IOreEntry ore : JAOPCAApi.ORE_ENTRY_LIST) {
				if(entry.blacklist.contains(ore.getOreName())) {
					continue;
				}

				ItemBase item = new ItemBase(entry, ore);
				GameRegistry.register(item);
				JAOPCA.proxy.handleItemRegister(entry, ore, item);
				OreDictionary.registerOre(entry.prefix+ore.getOreName(), new ItemStack(item, 1, 0));
				JAOPCAApi.ITEMS_TABLE.put(entry.name, ore.getOreName(), item);
			}
		}
	}

	private static void registerBlocks() {
		for(ItemEntry entry : JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.get(EnumEntryType.BLOCK)) {
			for(IOreEntry ore : JAOPCAApi.ORE_ENTRY_LIST) {
				if(entry.blacklist.contains(ore.getOreName())) {
					continue;
				}

				BlockBase block = new BlockBase(entry, ore);
				GameRegistry.register(block);
				ItemBlockBase itemblock = new ItemBlockBase(block);
				GameRegistry.register(itemblock);
				JAOPCA.proxy.handleBlockRegister(entry, ore, block, itemblock);
				OreDictionary.registerOre(entry.prefix+ore.getOreName(), new ItemStack(block, 1, 0));
				JAOPCAApi.BLOCKS_TABLE.put(entry.name, ore.getOreName(), block);
			}
		}
	}

	private static void registerFluids() {
		for(ItemEntry entry : JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.get(EnumEntryType.FLUID)) {
			JAOPCAApi.TEXTURES.add(new ResourceLocation("jaopca:fluids/"+entry.prefix+"_still"));
			JAOPCAApi.TEXTURES.add(new ResourceLocation("jaopca:fluids/"+entry.prefix+"_flowing"));

			for(IOreEntry ore : JAOPCAApi.ORE_ENTRY_LIST) {
				if(entry.blacklist.contains(ore.getOreName())) {
					continue;
				}

				FluidBase fluid = new FluidBase(entry, ore);
				FluidRegistry.registerFluid(fluid);
				JAOPCAApi.FLUIDS_TABLE.put(entry.name, ore.getOreName(), fluid);
			}
		}
	}

	private static void registerCustoms() {
		for(ItemEntry entry : JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.get(EnumEntryType.CUSTOM)) {
			List<IOreEntry> oreList = Lists.newArrayList(JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get(entry.name));

			for(IModule module : entry.moduleList) {
				module.registerCustom(entry, oreList);
			}
		}
	}

	private static void setProperties() {
		for(IModule module : JAOPCAApi.MODULE_LIST) {
			module.setCustomProperties();
		}
	}

	private static void registerRecipes() {
		for(IModule module : JAOPCAApi.MODULE_LIST) {
			module.registerRecipes();
		}
	}
}
