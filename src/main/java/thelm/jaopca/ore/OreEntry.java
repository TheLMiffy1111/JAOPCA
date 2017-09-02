package thelm.jaopca.ore;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import thelm.jaopca.api.EnumOreType;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.utils.JAOPCAConfig;

public class OreEntry implements IOreEntry {

	protected final String oreName;
	protected String extra;
	protected String extra2;
	protected double energy;
	protected List<String> moduleBlacklist = Lists.<String>newArrayList();
	protected EnumOreType type = EnumOreType.INGOT;
	protected Color color = null;

	public OreEntry(String oreName) {
		this.oreName = oreName;
		this.extra = oreName;
		this.extra2 = oreName;
		this.energy = 1D;
	}

	@Override
	public String getOreName() {
		return oreName;
	}

	@Override
	public String getExtra() {
		return extra;
	}

	@Override
	public String getSecondExtra() {
		return extra2;
	}

	@Override
	public double getEnergyModifier() {
		return energy;
	}

	@Override
	public List<String> getModuleBlacklist() {
		return moduleBlacklist;
	}

	@Override
	public EnumOreType getOreType() {
		return type;
	}

	@Override
	public int getColor() {
		if(color == null) {
			if(Loader.instance().hasReachedState(LoaderState.AVAILABLE)&&FMLCommonHandler.instance().getEffectiveSide().isClient()) {
				String s = "ingot";
				switch(type) {
				case DUST:
					s = "dust";
					break;
				case GEM:
				case GEM_ORELESS:
					s = "gem";
					break;
				default:
					break;
				}
				color = OreColorer.getColor(s, oreName);
				JAOPCAConfig.initColorConfigs(this);
			}
			else {
				return 0xFFFFFF;
			}
		}

		return color.getRGB() & 0xFFFFFF;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public void setSecondExtra(String extra2) {
		this.extra2 = extra2;
	}

	public void setEnergyModifier(double energyModifier) {
		this.energy = energyModifier;
	}

	public void addBlacklistedModules(Collection<String> blacklist) {
		this.moduleBlacklist.addAll(blacklist);
	}

	public void setOreType(EnumOreType type) {
		this.type = type;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
