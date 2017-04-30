package thelm.jaopca.ore;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.FMLCommonHandler;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.utils.JAOPCAConfig;

public class OreEntry implements IOreEntry {

	protected final String oreName;
	protected String extra;
	protected double energy;
	protected List<String> moduleBlacklist = Lists.<String>newArrayList();
	protected Color color = null;

	public OreEntry(String oreName) {
		this.oreName = oreName;
		this.extra = oreName;
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
	public double getEnergyModifier() {
		return energy;
	}

	@Override
	public List<String> getModuleBlacklist() {
		return moduleBlacklist;
	}

	@Override
	public int getColor() {
		if(color == null) {
			if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
				color = OreColorer.getColor("ingot", oreName);
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

	public void setEnergyModifier(double energyModifier) {
		this.energy = energyModifier;
	}

	public void addBlacklistedModules(Collection<String> blacklist) {
		this.moduleBlacklist.addAll(blacklist);
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
