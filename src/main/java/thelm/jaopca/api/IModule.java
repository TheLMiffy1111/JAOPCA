package thelm.jaopca.api;

import java.util.List;

public interface IModule {
	
	public String getName();
	
	public List<ItemEntry> getItemRequests();
	
	public void registerCustom(ItemEntry itemEntry, List<IOreEntry> allOres);
	
	public void registerRecipes();
	
	public void setCustomProperties();
}
