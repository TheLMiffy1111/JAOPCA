package thelm.oredictinit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Woodchopper {
	
	public static final Logger logger = LogManager.getLogger("OreDictInit");
	
	public static void error(Object message) {
		logger.log(Level.ERROR, message);
	}
	
	public static void warn(Object message) {
		logger.log(Level.WARN, message);
	}
	
	public static void info(Object message) {
		logger.log(Level.INFO, message);
	}
	
	public static void debug(Object message) {
		logger.log(Level.DEBUG, message);
	}
}
