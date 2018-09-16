package thelm.jaopca.crafttweaker;

import java.util.List;

import com.google.common.collect.Lists;

import crafttweaker.annotations.ZenRegister;
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

@ZenClass("mods.jaopca.OreEntry")
@ZenRegister
public class OreEntry {

	private final IOreEntry entry;

	public OreEntry(IOreEntry entry) {
		this.entry = entry;
	}

	@ZenGetter("oreName")
	public String getOreName() {
		return entry.getOreName();
	}

	@ZenGetter("oreNameSynonyms")
	public List<String> getOreNameSynonyms() {
		return Lists.newArrayList(entry.getOreNameSynonyms());	
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
	public OreEntry getExtra() {
		return JAOPCAApi.ORE_ENTRY_LIST.stream().filter(entry->this.entry.getExtra().equals(entry.getExtra())).map(OreEntry::new).findAny().orElse(null);
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
	public OreEntry getSecondExtra() {
		return JAOPCAApi.ORE_ENTRY_LIST.stream().filter(entry->this.entry.getSecondExtra().equals(entry.getSecondExtra())).map(OreEntry::new).findAny().orElse(null);
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

	@ZenGetter("hasThirdExtra")
	public boolean hasThirdExtra() {
		return entry.hasThirdExtra();
	}

	@ZenGetter("thirdExtra")
	public OreEntry getThirdExtra() {
		return JAOPCAApi.ORE_ENTRY_LIST.stream().filter(entry->this.entry.getThirdExtra().equals(entry.getThirdExtra())).map(OreEntry::new).findAny().orElse(null);
	}

	@ZenGetter("thirdExtraName")
	public String getThirdExtraName() {
		return entry.getThirdExtra();
	}

	@ZenMethod
	public IOreDictEntry getOreDictEntryThirdExtra(String prefix) {
		return CraftTweakerMC.getOreDict(prefix+entry.getThirdExtra());
	}

	@ZenMethod
	public IItemStack getItemStackThirdExtra(String prefix) {
		return CraftTweakerMC.getIItemStack(Utils.getOreStackThirdExtra(prefix, entry, 1));
	}

	@ZenMethod
	public IItemStack getItemStackThirdExtra(String name, String prefix) {
		return CraftTweakerMC.getIItemStack(Utils.getJAOPCAOrOreStackThirdExtra(name, prefix, entry, 1));
	}

	@ZenMethod
	public ILiquidStack getLiquidStackThirdExtra(String prefix) {
		return (ILiquidStack)CraftTweakerMC.getIIngredient(Utils.getFluidStackThirdExtra(prefix, entry, 1));
	}

	@ZenMethod
	public ILiquidStack getLiquidStackThirdExtra(String name, String prefix) {
		return (ILiquidStack)CraftTweakerMC.getIIngredient(Utils.getJAOPCAOrFluidStackThirdExtra(name, prefix, entry, 1));
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
		if(!(obj instanceof OreEntry)) {
			return false;
		}
		OreEntry other = (OreEntry)obj;
		return entry == other.entry;
	}

	@Override
	public int hashCode() {
		return entry.hashCode()+7;
	}
}
