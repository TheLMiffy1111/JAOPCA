package thelm.jaopca.api;

/**
 * Add an instance of this interface to JAOPCAApi.ORE_DICT_COMPAT_LIST to register ore dict entries at the end of preInit.
 * @author TheLMiffy1111
 */
public interface ICompat {
	
	/**
	 * Register all of your ore dict entries here
	 * @author TheLMiffy1111
	 */
	public void register();
}
