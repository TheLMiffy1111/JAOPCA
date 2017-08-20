package thelm.jaopca.api;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IObjectWithProperty {
	
	IOreEntry getOreEntry();
	ItemEntry getItemEntry();
	
	@SideOnly(Side.CLIENT)
	void registerModels();
}
