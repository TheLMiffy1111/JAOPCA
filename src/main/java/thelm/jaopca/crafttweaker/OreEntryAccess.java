package thelm.jaopca.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.utils.Utils;

@ZenClass("thelm.jaopca.OreEntry")
@ZenRegister
public class OreEntryAccess {

	private final IOreEntry entry;

	public OreEntryAccess(IOreEntry entry) {
		this.entry = entry;
	}

	@ZenGetter("oreName")
	public String getOreName() {
		return entry.getOreName();
	}

	@ZenMethod
	public IOreDictEntry getOreDictEntry(String prefix) {
		return CraftTweakerMC.getOreDict(prefix+entry.getOreName());
	}

	@ZenMethod
	public IItemStack getItemStack(String prefix) {
		return CraftTweakerMC.getIItemStack(Utils.getOreStack(prefix, entry, 1));
	}

	@ZenMethod
	public IItemStack getItemStack(String name, String prefix) {
		return CraftTweakerMC.getIItemStack(Utils.getJAOPCAOrOreStack(name, prefix, entry, 1));
	}

	@ZenMethod
	public ILiquidStack getLiquidStack(String prefix) {
		return (ILiquidStack)CraftTweakerMC.getIIngredient(Utils.getFluidStack(prefix, entry, 1));
	}

	@ZenMethod
	public ILiquidStack getLiquidStack(String name, String prefix) {
		return (ILiquidStack)CraftTweakerMC.getIIngredient(Utils.getJAOPCAOrFluidStack(name, prefix, entry, 1));
	}

	@ZenGetter("hasExtra")
	public boolean hasExtra() {
		return entry.hasExtra();
	}

	@ZenGetter("extra")
	public OreEntryAccess getExtra() {
		return JAOPCAApi.ORE_ENTRY_LIST.stream().filter(entry->this.entry.getExtra().equals(entry.getExtra())).map(OreEntryAccess::new).findAny().orElse(null);
	}

	@ZenGetter("extraName")
	public String getExtraName() {
		return entry.getExtra();
	}

	@ZenMethod
	public IOreDictEntry getOreDictEntryExtra(String prefix) {
		return CraftTweakerMC.getOreDict(prefix+entry.getExtra());
	}

	@ZenMethod
	public IItemStack getItemStackExtra(String prefix) {
		return CraftTweakerMC.getIItemStack(Utils.getOreStackExtra(prefix, entry, 1));
	}

	@ZenMethod
	public IItemStack getItemStackExtra(String name, String prefix) {
		return CraftTweakerMC.getIItemStack(Utils.getJAOPCAOrOreStackExtra(name, prefix, entry, 1));
	}

	@ZenMethod
	public ILiquidStack getLiquidStackExtra(String prefix) {
		return (ILiquidStack)CraftTweakerMC.getIIngredient(Utils.getFluidStackExtra(prefix, entry, 1));
	}

	@ZenMethod
	public ILiquidStack getLiquidStackExtra(String name, String prefix) {
		return (ILiquidStack)CraftTweakerMC.getIIngredient(Utils.getJAOPCAOrFluidStackExtra(name, prefix, entry, 1));
	}

	@ZenGetter("hasSecondExtra")
	public boolean hasSecondExtra() {
		return entry.hasSecondExtra();
	}

	@ZenGetter("secondExtra")
	public OreEntryAccess getSecondExtra() {
		return JAOPCAApi.ORE_ENTRY_LIST.stream().filter(entry->this.entry.getSecondExtra().equals(entry.getSecondExtra())).map(OreEntryAccess::new).findAny().orElse(null);
	}

	@ZenGetter("secondExtraName")
	public String getSecondExtraName() {
		return entry.getSecondExtra();
	}

	@ZenMethod
	public IOreDictEntry getOreDictEntrySecondExtra(String prefix) {
		return CraftTweakerMC.getOreDict(prefix+entry.getSecondExtra());
	}

	@ZenMethod
	public IItemStack getItemStackSecondExtra(String prefix) {
		return CraftTweakerMC.getIItemStack(Utils.getOreStackSecondExtra(prefix, entry, 1));
	}

	@ZenMethod
	public IItemStack getItemStackSecondExtra(String name, String prefix) {
		return CraftTweakerMC.getIItemStack(Utils.getJAOPCAOrOreStackSecondExtra(name, prefix, entry, 1));
	}

	@ZenMethod
	public ILiquidStack getLiquidStackSecondExtra(String prefix) {
		return (ILiquidStack)CraftTweakerMC.getIIngredient(Utils.getFluidStackSecondExtra(prefix, entry, 1));
	}

	@ZenMethod
	public ILiquidStack getLiquidStackSecondExtra(String name, String prefix) {
		return (ILiquidStack)CraftTweakerMC.getIIngredient(Utils.getJAOPCAOrFluidStackSecondExtra(name, prefix, entry, 1));
	}

	@ZenGetter("energyModifier")
	public double getEnergyModifier() {
		return entry.getEnergyModifier();
	}

	@ZenGetter("rarity")
	public double getRarity() {
		return entry.getRarity();
	}

	@ZenGetter("oreType")
	public String getOreType() {
		return Utils.toLowerCase(entry.getOreType().name());
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof OreEntryAccess)) {
			return false;
		}
		OreEntryAccess other = (OreEntryAccess)obj;
		return entry == other.entry;
	}

	@Override
	public int hashCode() {
		return entry.hashCode()+7;
	}
}
