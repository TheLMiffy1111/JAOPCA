package thelm.jaopca.registry;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import thelm.jaopca.JAOPCA;
import thelm.jaopca.api.EnumEntryType;
import thelm.jaopca.api.IItemRequest;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;
import thelm.jaopca.api.ItemEntryGroup;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.ModuleBase;
import thelm.jaopca.api.block.BlockProperties;
import thelm.jaopca.api.block.IBlockFluidWithProperty;
import thelm.jaopca.api.block.IBlockWithProperty;
import thelm.jaopca.api.fluid.FluidProperties;
import thelm.jaopca.api.fluid.IFluidWithProperty;
import thelm.jaopca.api.item.IItemBlockFluidWithProperty;
import thelm.jaopca.api.item.IItemBlockWithProperty;
import thelm.jaopca.api.item.IItemWithProperty;
import thelm.jaopca.api.item.ItemProperties;
import thelm.jaopca.api.utils.Utils;
import thelm.jaopca.ore.OreFinder;
import thelm.jaopca.utils.JAOPCAConfig;

/**
 * Internal.
 * The class where most things of this mod is done.
 * Many methods may not be efficient.
 * @author TheLMiffy1111
 */
public class RegistryCore {

	public static final ArrayList<IItemRequest> ITEM_REQUEST_LIST = Lists.<IItemRequest>newArrayList();

	public static void preInit(FMLPreInitializationEvent event) {
		JAOPCAConfig.init(new File(event.getModConfigurationDirectory(), "JAOPCA.cfg"));
	}

	public static void preInit1() {
		initPrefixBlacklist();
		OreFinder.findOres();

		JAOPCAConfig.preInitModulewiseConfigs();

		initItemEntries();
		initBlacklists();
		initToOreMaps();

		JAOPCAConfig.initModulewiseConfigs();

		registerEntries();

		registerPreInit();
	}

	public static void init() {
		registerInit();
	}

	public static void postInit() {
		registerPostInit();
	}

	public static void registerBuiltInModules() {
		try {
			Class<?> moduleClass = Class.forName("thelm.jaopca.modules.RegistryModules");
			Method initMethod = moduleClass.getMethod("preInit");
			initMethod.invoke(null);
		}
		catch(ClassNotFoundException e) {
			JAOPCAApi.LOGGER.warn("Module registry not found! Will continue loading.");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void initPrefixBlacklist() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			OreFinder.PREFIX_BLACKLIST.addAll(module.addToPrefixBlacklist());
		}
	}

	private static void initItemEntries() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			List<? extends IItemRequest> requests = module.getItemRequests();
			for(IItemRequest request : requests) {
				if(request instanceof ItemEntry) {
					ItemEntry entry = (ItemEntry)request;
					if(!JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.containsKey(entry.name)) {
						JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.put(entry.name, entry);
						JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.put(entry.type, entry);
						JAOPCAApi.ITEM_ENTRY_LIST.add(entry);
					}
					else {
						JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.get(entry.name).blacklist.addAll(entry.blacklist);
						continue;
					}

					JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.get(entry.name).moduleList.add(module);
				}
				else if(request instanceof ItemEntryGroup) {
					for(ItemEntry entry : ((ItemEntryGroup)request).entryList) {
						if(!JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.containsKey(entry.name)) {
							JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.put(entry.name, entry);
							JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.put(entry.type, entry);
							JAOPCAApi.ITEM_ENTRY_LIST.add(entry);
						}
						else {
							JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.get(entry.name).blacklist.addAll(entry.blacklist);
							continue;
						}

						JAOPCAApi.NAME_TO_ITEM_ENTRY_MAP.get(entry.name).moduleList.add(module);
					}
				}
				ITEM_REQUEST_LIST.add(request);
			}
		}
	}

	private static void initBlacklists() {
		for(IOreEntry ore : JAOPCAApi.ORE_ENTRY_LIST) {
			for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
				if(ore.getModuleBlacklist().stream().anyMatch(name->module.getDependencies().contains(name))) {
					ore.getModuleBlacklist().add(module.getName());
				}
			}

			for(IItemRequest request : ITEM_REQUEST_LIST) {
				if(request instanceof ItemEntry) {
					ItemEntry entry = (ItemEntry)request;
					if(entry.oreTypes.contains(ore.getOreType())) {
						if(entry.type.checker.test(entry, ore)) {
							entry.blacklist.add(ore.getOreName());
						}

						for(String moduleName : ore.getModuleBlacklist()) {
							for(ModuleBase module : entry.moduleList) {
								if(module.getDependencies().contains(moduleName)) {
									entry.blacklist.add(ore.getOreName());
								}
							}

							if(entry.moduleList.contains(JAOPCAApi.NAME_TO_MODULE_MAP.get(moduleName))) {
								entry.blacklist.add(ore.getOreName());
							}
						}
					}
				}
				else if(request instanceof ItemEntryGroup) {
					ItemEntryGroup entryGroup = (ItemEntryGroup)request;
					boolean flag = true;
					for(ItemEntry entry : entryGroup.entryList) {
						if(entry.oreTypes.contains(ore.getOreType())) {
							flag &= entry.type.checker.test(entry, ore);

							for(String moduleName : ore.getModuleBlacklist()) {
								for(ModuleBase module : entry.moduleList) {
									if(module.getDependencies().contains(moduleName)) {
										entry.blacklist.add(ore.getOreName());
									}
								}

								if(entry.moduleList.contains(JAOPCAApi.NAME_TO_MODULE_MAP.get(moduleName))) {
									entry.blacklist.add(ore.getOreName());
								}
							}
						}
					}
					if(flag) {
						for(ItemEntry entry : entryGroup.entryList) {
							entry.blacklist.add(ore.getOreName());
						}
					}
				}
			}
		}
	}

	public static boolean checkEntry(ItemEntry entry, IOreEntry ore) {
		if(entry.type == EnumEntryType.BLOCK || entry.type == EnumEntryType.ITEM) {
			return Utils.doesOreNameExist(entry.prefix+ore.getOreName());
		}
		else if(entry.type == EnumEntryType.FLUID) {
			return entry.prefix.isEmpty() && FluidRegistry.isFluidRegistered(Utils.to_under_score(ore.getOreName()))
					|| FluidRegistry.isFluidRegistered(entry.prefix+'_'+ore.getOreName());
		}
		else if(entry.type == EnumEntryType.CUSTOM) {
			boolean flag = true;
			for(ModuleBase module : entry.moduleList) {
				flag &= module.blacklistCustom(entry, ore);
			}
			return flag;
		}
		else {
			throw new IllegalArgumentException("Unsupported entry type: "+entry.type);
		}
	}

	private static void initToOreMaps() {
		for(ItemEntry entry : JAOPCAApi.ITEM_ENTRY_LIST) {
			LinkedHashSet<IOreEntry> oreSet = Sets.<IOreEntry>newLinkedHashSet();
			JAOPCAApi.ORE_ENTRY_LIST.stream().
			filter(oreEntry->entry.oreTypes.contains(oreEntry.getOreType())&&!entry.blacklist.contains(oreEntry.getOreName())).
			forEach(oreEntry->oreSet.add(oreEntry));

			JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.putAll(entry.name, oreSet);
		}

		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			LinkedHashSet<IOreEntry> oreSet = Sets.<IOreEntry>newLinkedHashSet();
			JAOPCAApi.ORE_ENTRY_LIST.stream().
			filter(oreEntry->module.getOreTypes().contains(oreEntry.getOreType())&&!module.getOreBlacklist().contains(oreEntry.getOreName())&&!oreEntry.getModuleBlacklist().contains(module.getName())).
			forEach(oreEntry->oreSet.add(oreEntry));

			JAOPCAApi.MODULE_TO_ORES_MAP.putAll(module, oreSet);
		}
	}

	private static void registerEntries() {
		for(EnumEntryType type : EnumEntryType.values()) {
			for(ItemEntry entry : JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.get(type)) {
				type.registerer.accept(entry);
			}
		}
	}

	private static void registerBlocks(ItemEntry entry) {
		BlockProperties ppt = (BlockProperties)entry.properties;

		for(IOreEntry ore : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get(entry.name)) {
			try {
				IBlockWithProperty block = ppt.blockClass.getConstructor(Material.class, MapColor.class, ItemEntry.class, IOreEntry.class).newInstance(ppt.material, ppt.mapColor, entry, ore);
				block.
				setHardness(ppt.hardnessFunc.applyAsFloat(ore)).
				setResistance(ppt.resisFunc.applyAsFloat(ore)).
				setLightOpacity(ppt.lgtOpacFunc.applyAsInt(ore)).
				setLightLevel(ppt.lgtValFunc.applyAsFloat(ore)).
				setSlipperiness(ppt.slippyFunc.applyAsFloat(ore)).
				setSoundType(ppt.soundType).
				setBeaconBase(ppt.beaconBase).
				setBoundingBox(ppt.boundingBox).
				setHarvestTool(ppt.harvestTool).
				setHarvestLevel(ppt.harvestLevel).
				setFull(ppt.full).
				setOpaque(ppt.opaque).
				setBlockLayer(ppt.layer).
				setFlammability(ppt.flammabFunc.applyAsInt(ore)).
				setFireSpreadSpeed(ppt.fireSpdFunc.applyAsInt(ore)).
				setFireSource(ppt.fireSource).
				setFallable(ppt.fallable);
				ForgeRegistries.BLOCKS.register((Block)block);
				IItemBlockWithProperty itemblock = ppt.itemBlockClass.getConstructor(IBlockWithProperty.class).newInstance(block);
				itemblock.
				setMaxStackSize(ppt.maxStkSize).
				setRarity(ppt.rarity);
				ForgeRegistries.ITEMS.register((ItemBlock)itemblock);
				JAOPCA.proxy.handleBlockRegister((Block)block, (ItemBlock)itemblock);
				for(int i = 0; i <= block.getMaxMeta(); ++i) {
					if(block.hasMeta(i)) {
						OreDictionary.registerOre(block.getPrefix(i)+ore.getOreName(), new ItemStack((Block)block, 1, i));
					}
				}
				JAOPCAApi.BLOCKS_TABLE.put(entry.name, ore.getOreName(), (Block)block);
			}
			catch(RuntimeException e) {
				if(e.getMessage().contains("maximum id range exceeded")) {
					throw e;
				}
				e.printStackTrace();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void registerItems(ItemEntry entry) {
		ItemProperties ppt = (ItemProperties)entry.properties;

		for(IOreEntry ore : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get(entry.name)) {
			try {
				IItemWithProperty item = ppt.itemClass.getConstructor(ItemEntry.class, IOreEntry.class).newInstance(entry, ore);
				item.
				setMaxStackSize(ppt.maxStkSize).
				setFull3D(ppt.full3D).
				setRarity(ppt.rarity).
				setHasEffect(ppt.hasEffect.test(ore));
				ForgeRegistries.ITEMS.register((Item)item);
				JAOPCA.proxy.handleItemRegister((Item)item);
				for(int i = 0; i <= item.getMaxMeta(); ++i) {
					if(item.hasMeta(i)) {
						OreDictionary.registerOre(item.getPrefix(i)+ore.getOreName(), new ItemStack((Item)item, 1, i));
					}
				}
				JAOPCAApi.ITEMS_TABLE.put(entry.name, ore.getOreName(), (Item)item);
			}
			catch(RuntimeException e) {
				if(e.getMessage().contains("maximum id range exceeded")) {
					throw e;
				}
				e.printStackTrace();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void registerFluids(ItemEntry entry) {
		FluidProperties ppt = (FluidProperties)entry.properties;

		if(!ppt.hasBlock) {
			JAOPCAApi.TEXTURES.add(new ResourceLocation("jaopca:fluids/"+entry.name+"_still"));
			JAOPCAApi.TEXTURES.add(new ResourceLocation("jaopca:fluids/"+entry.name+"_flowing"));
		}

		for(IOreEntry ore : JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get(entry.name)) {
			try {
				IFluidWithProperty fluid = ppt.fluidClass.getConstructor(ItemEntry.class, IOreEntry.class).newInstance(entry, ore);
				fluid.
				setLuminosity(ppt.luminosFunc.applyAsInt(ore)).
				setTemperature(ppt.tempFunc.applyAsInt(ore)).
				setDensity(ppt.densityFunc.applyAsInt(ore)).
				setViscosity(ppt.viscosFunc.applyAsInt(ore)).
				setGaseous(ppt.gaseous.test(ore)).
				setRarity(ppt.rarity).
				setFillSound(ppt.fillSound).
				setEmptySound(ppt.emptySound).
				setOpacity(ppt.opacityFunc.applyAsInt(ore));
				FluidRegistry.registerFluid((Fluid)fluid);
				FluidRegistry.addBucketForFluid((Fluid)fluid);
				if(ppt.hasBlock) {
					IBlockFluidWithProperty blockfluid = ppt.blockFluidClass.getConstructor(IFluidWithProperty.class, Material.class).newInstance(fluid, ppt.material);
					blockfluid.
					setQuantaPerBlock(ppt.quantaFunc.applyAsInt(ore));
					ForgeRegistries.BLOCKS.register((Block)blockfluid);
					IItemBlockFluidWithProperty itemblockfluid = ppt.itemBlockFluidClass.getConstructor(IBlockFluidWithProperty.class).newInstance(blockfluid);
					ForgeRegistries.ITEMS.register((ItemBlock)itemblockfluid);
					JAOPCA.proxy.handleBlockRegister((Block)blockfluid, (ItemBlock)itemblockfluid);
				}
				JAOPCAApi.FLUIDS_TABLE.put(entry.name, ore.getOreName(), (Fluid)fluid);
			}
			catch(RuntimeException e) {
				if(e.getMessage().contains("maximum id range exceeded")) {
					throw e;
				}
				e.printStackTrace();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void registerCustoms(ItemEntry entry) {
		List<IOreEntry> oreList = Lists.newArrayList(JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get(entry.name));

		for(ModuleBase module : entry.moduleList) {
			module.registerCustom(entry, oreList);
		}
	}

	private static void registerPreInit() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			JAOPCAApi.LOGGER.debug("PreInit-ing module "+module.getName());
			module.preInit();
		}
	}

	private static void registerInit() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			JAOPCAApi.LOGGER.debug("Init-ing module "+module.getName());
			module.init();
		}
	}

	private static void registerPostInit() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			JAOPCAApi.LOGGER.debug("PostInit-ing module "+module.getName());
			module.postInit();
		}
	}

	private static boolean initRemaps = true;
	private static final List<Pair<String,String>> REMAPS = Lists.<Pair<String,String>>newArrayList();

	public static <T extends IForgeRegistryEntry<T>> void onMissingMappings(List<Mapping<T>> missingMappings) {
		if(initRemaps) {
			for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
				REMAPS.addAll(module.remaps());
			}
			initRemaps = false;
		}
		main:for(Mapping<T> missingMapping : missingMappings) {
			String[] names = missingMapping.key.getResourcePath().split("_");
			if(names.length == 2) {
				for(Pair<String, String> pair : REMAPS) {
					if(names[1].startsWith(pair.getLeft())) {
						String oreName = names[1].substring(pair.getLeft().length());
						if(JAOPCAApi.ORE_ENTRY_LIST.stream().anyMatch(entry->entry.getOreName().equalsIgnoreCase(oreName))) {
							ResourceLocation remap = new ResourceLocation(missingMapping.key.getResourceDomain(), names[0]+'_'+pair.getRight()+oreName);
							//Had to do this because apparently Forge can't find my stuff
							IForgeRegistry<T> reg = GameRegistry.<T>findRegistry(missingMapping.registry.getRegistrySuperType());
							if(reg.containsKey(remap)) {
								missingMapping.remap(reg.getValue(remap));
							}
							continue main;
						}
					}
				}
			}
		}
	}
}
