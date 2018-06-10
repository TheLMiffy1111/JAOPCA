package thelm.jaopca.api;

import java.util.List;

public interface IOreEntry {

	/**
	 *
	 * @return The name of the ore
	 */
	public String getOreName();

	/**
	 *
	 * @return The name of the extra of the ore
	 */
	public String getExtra();


	public String getSecondExtra();

	public String getThirdExtra();

	/**
	 *
	 * @return The energy modifier of processing
	 */
	public double getEnergyModifier();

	public double getRarity();

	/**
	 *
	 * @return The list of blacklisted modules
	 */
	public List<String> getModuleBlacklist();

	/**
	 *
	 * @return The color of the ore
	 */
	public int getColor();

	public EnumOreType getOreType();

	public boolean getHasEffect();

	default boolean hasExtra() {
		return !getExtra().equals(getOreName());
	}

	default boolean hasSecondExtra() {
		return !getSecondExtra().equals(getOreName()) && !getSecondExtra().equals(getExtra());
	}

	default boolean hasThirdExtra() {
		return !getThirdExtra().equals(getOreName()) && !getThirdExtra().equals(getExtra()) && !getThirdExtra().equals(getSecondExtra());
	}
}
