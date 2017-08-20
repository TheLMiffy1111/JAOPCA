package thelm.jaopca.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
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
import thelm.jaopca.modules.ModuleMolten;
import thelm.jaopca.utils.JAOPCAConfig;

/**
 * The class where most things of this mod is done.
 * Many methods may not be efficient.
 * @author TheLMiffy1111
 */
public class RegistryCore {

	public static final ArrayList<IItemRequest> ITEM_REQUEST_LIST = Lists.<IItemRequest>newArrayList();

	public static void preInit() {
		registerBuiltInModules();

		filterModules();

		JAOPCAConfig.preInitModulewiseConfigs();
		
		initItemEntries();
		initBlacklists();
		initToOreMaps();

		JAOPCAConfig.initModulewiseConfigs();

		registerItems();
		registerBlocks();
		registerFluids();
		registerCustoms();

		registerPreInit();
	}

	public static void init() {
		registerInit();
	}

	public static void postInit() {
		registerPostInit();
	}

	private static void registerBuiltInModules() {
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

	private static void filterModules() {
		HashSet<ModuleBase> toRemove = Sets.<ModuleBase>newHashSet();
		HashSet<String> toRemoveNames = Sets.<String>newHashSet();
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			for(String moduleName : module.getDependencies()) {
				if(!JAOPCAApi.NAME_TO_MODULE_MAP.containsKey(moduleName)) {
					toRemove.add(module);
					toRemoveNames.add(module.getName());
				}
			}

			if(JAOPCAConfig.moduleBlacklist.contains(module.getName())) {
				toRemove.add(module);
				toRemoveNames.add(module.getName());
			}
		}

		JAOPCAApi.MODULE_LIST.removeAll(toRemove);
		JAOPCAApi.NAME_TO_MODULE_MAP.remove(toRemoveNames);
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
			if(ore.getModuleBlacklist().contains("*")) {
				ore.getModuleBlacklist().clear();
				for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
					ore.getModuleBlacklist().add(module.getName());
				}
			}

			for(IItemRequest request : ITEM_REQUEST_LIST) {
				if(request instanceof ItemEntry) {
					ItemEntry entry = (ItemEntry)request;
					if(entry.type == EnumEntryType.BLOCK || entry.type == EnumEntryType.ITEM) {
						if(!OreDictionary.getOres(entry.prefix+ore.getOreName()).isEmpty()) {
							entry.blacklist.add(ore.getOreName());
						}
					}
					else if(entry.type == EnumEntryType.FLUID) {
						if((entry == ModuleMolten.MOLTEN_ENTRY && FluidRegistry.isFluidRegistered(Utils.to_under_score(ore.getOreName())))
								|| FluidRegistry.isFluidRegistered(entry.prefix+"_"+Utils.to_under_score(ore.getOreName()))) {
							entry.blacklist.add(ore.getOreName());
						}
					}
					else {
						for(ModuleBase module : entry.moduleList) {
							if(module.blacklistCustom(entry, ore)) {
								entry.blacklist.add(ore.getOreName());
							}
						}
					}

					for(String moduleName : ore.getModuleBlacklist()) {
						for(ModuleBase module : entry.moduleList) {
							if(!module.getDependencies().isEmpty() && module.getDependencies().contains(moduleName)) {
								entry.blacklist.add(ore.getOreName());
							}
						}

						if(entry.moduleList.contains(JAOPCAApi.NAME_TO_MODULE_MAP.get(moduleName))) {
							entry.blacklist.add(ore.getOreName());
						}
					}
				}
				else if(request instanceof ItemEntryGroup) {
					ItemEntryGroup entryGroup = (ItemEntryGroup)request;
					boolean flag = true;
					for(ItemEntry entry : entryGroup.entryList) {
						if(!entry.skipWhenGrouped) {
							if(entry.type == EnumEntryType.BLOCK || entry.type == EnumEntryType.ITEM) {
								flag &= !OreDictionary.getOres(entry.prefix+ore.getOreName()).isEmpty();
							}
							else if(entry.type == EnumEntryType.FLUID) {
								flag &= (entry == ModuleMolten.MOLTEN_ENTRY && FluidRegistry.isFluidRegistered(Utils.to_under_score(ore.getOreName())))
										|| FluidRegistry.isFluidRegistered(entry.prefix+"_"+Utils.to_under_score(ore.getOreName()));
							}
							else {
								for(ModuleBase module : entry.moduleList) {
									flag &= module.blacklistCustom(entry, ore);
								}
							}
						}

						for(String moduleName : ore.getModuleBlacklist()) {
							for(ModuleBase module : entry.moduleList) {
								if(!module.getDependencies().isEmpty() && module.getDependencies().contains(moduleName)) {
									entry.blacklist.add(ore.getOreName());
								}
							}

							if(entry.moduleList.contains(JAOPCAApi.NAME_TO_MODULE_MAP.get(moduleName))) {
								entry.blacklist.add(ore.getOreName());
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

	private static void initToOreMaps() {
		for(ItemEntry entry : JAOPCAApi.ITEM_ENTRY_LIST) {
			LinkedHashSet<IOreEntry> oreSet = Sets.<IOreEntry>newLinkedHashSet();
			JAOPCAApi.ORE_ENTRY_LIST.stream().
			filter(oreEntry->!entry.blacklist.contains(oreEntry.getOreName())).
			forEach(oreEntry->oreSet.add(oreEntry));

			JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.putAll(entry.name, oreSet);
		}

		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			LinkedHashSet<IOreEntry> oreSet = Sets.<IOreEntry>newLinkedHashSet();
			JAOPCAApi.ORE_ENTRY_LIST.stream().
			filter(oreEntry->!module.getOreBlacklist().contains(oreEntry.getOreName()) && !oreEntry.getModuleBlacklist().contains(module.getName())).
			forEach(oreEntry->oreSet.add(oreEntry));

			JAOPCAApi.MODULE_TO_ORES_MAP.putAll(module, oreSet);
		}
	}

	private static void registerItems() {
		for(ItemEntry entry : JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.get(EnumEntryType.ITEM)) {
			ItemProperties ppt = entry.itemProperties;

			for(IOreEntry ore : JAOPCAApi.ORE_ENTRY_LIST) {
				if(entry.blacklist.contains(ore.getOreName())) {
					continue;
				}

				try {
					IItemWithProperty item = ppt.itemClass.getConstructor(ItemEntry.class, IOreEntry.class).newInstance(entry, ore);
					item.
					setMaxStackSize(ppt.maxStkSize).
					setFull3D(ppt.full3D).
					setRarity(ppt.rarity);
					GameRegistry.register((Item)item);
					JAOPCA.proxy.handleItemRegister((Item)item);
					OreDictionary.registerOre(entry.prefix+ore.getOreName(), new ItemStack((Item)item, 1, 0));
					JAOPCAApi.ITEMS_TABLE.put(entry.name, ore.getOreName(), (Item)item);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void registerBlocks() {
		for(ItemEntry entry : JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.get(EnumEntryType.BLOCK)) {
			BlockProperties ppt = entry.blockProperties;

			for(IOreEntry ore : JAOPCAApi.ORE_ENTRY_LIST) {
				if(entry.blacklist.contains(ore.getOreName())) {
					continue;
				}

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
					GameRegistry.register((Block)block);
					IItemBlockWithProperty itemblock = ppt.itemBlockClass.getConstructor(IBlockWithProperty.class).newInstance(block);
					itemblock.
					setMaxStackSize(ppt.maxStkSize).
					setRarity(ppt.rarity);
					GameRegistry.register((ItemBlock)itemblock);
					JAOPCA.proxy.handleBlockRegister((Block)block, (ItemBlock)itemblock);
					OreDictionary.registerOre(entry.prefix+ore.getOreName(), new ItemStack((Block)block, 1, 0));
					JAOPCAApi.BLOCKS_TABLE.put(entry.name, ore.getOreName(), (Block)block);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void registerFluids() {
		for(ItemEntry entry : JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.get(EnumEntryType.FLUID)) {
			FluidProperties ppt = entry.fluidProperties;

			if(!ppt.hasBlock) {
				JAOPCAApi.TEXTURES.add(new ResourceLocation("jaopca:fluids/"+entry.prefix+"_still"));
				JAOPCAApi.TEXTURES.add(new ResourceLocation("jaopca:fluids/"+entry.prefix+"_flowing"));
			}

			for(IOreEntry ore : JAOPCAApi.ORE_ENTRY_LIST) {
				if(entry.blacklist.contains(ore.getOreName())) {
					continue;
				}

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
						GameRegistry.register((Block)blockfluid);
						IItemBlockFluidWithProperty itemblockfluid = ppt.itemBlockFluidClass.getConstructor(IBlockFluidWithProperty.class).newInstance(blockfluid);
						GameRegistry.register((ItemBlock)itemblockfluid);
						JAOPCA.proxy.handleBlockRegister((Block)blockfluid, (ItemBlock)itemblockfluid);
					}
					JAOPCAApi.FLUIDS_TABLE.put(entry.name, ore.getOreName(), (Fluid)fluid);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void registerCustoms() {
		for(ItemEntry entry : JAOPCAApi.TYPE_TO_ITEM_ENTRY_MAP.get(EnumEntryType.CUSTOM)) {
			List<IOreEntry> oreList = Lists.newArrayList(JAOPCAApi.ENTRY_NAME_TO_ORES_MAP.get(entry.name));

			for(ModuleBase module : entry.moduleList) {
				module.registerCustom(entry, oreList);
			}
		}
	}

	private static void registerPreInit() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			JAOPCAApi.LOGGER.debug("PreInitializing module "+module.getName());
			module.preInit();
		}
	}

	private static void registerInit() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			JAOPCAApi.LOGGER.debug("Initializing module "+module.getName());
			module.init();
		}
	}

	private static void registerPostInit() {
		for(ModuleBase module : JAOPCAApi.MODULE_LIST) {
			JAOPCAApi.LOGGER.debug("PostInitializing module "+module.getName());
			module.postInit();
		}
	}
}
