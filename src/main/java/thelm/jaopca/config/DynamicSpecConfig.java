package thelm.jaopca.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.utils.CommentedConfigWrapper;
import com.google.common.collect.Lists;

import thelm.jaopca.api.config.IDynamicSpecConfig;

public class DynamicSpecConfig extends CommentedConfigWrapper<CommentedConfig> implements IDynamicSpecConfig {

	final CommentedConfig config;

	public DynamicSpecConfig(CommentedConfig config) {
		super(config);
		this.config = config;
		if(config instanceof FileConfig) {
			FileConfig fileConfig = (FileConfig)config;
			try {
				fileConfig.load();
			}
			catch(ParsingException e) {
				throw new ParsingException("Failed to load config file "+fileConfig.getFile().getAbsolutePath(), e);
			}
		}
	}

	@Override
	public String getDefinedString(String path, String defaultValue, String comment) {
		return getDefinedString(split(path), defaultValue, comment);
	}

	@Override
	public String getDefinedString(List<String> path, String defaultValue, String comment) {
		return getDefinedString(path, defaultValue, v->true, comment);
	}

	@Override
	public String getDefinedString(String path, String defaultValue, Collection<String> validValues, String comment) {
		return getDefinedString(split(path), defaultValue, validValues, comment);
	}

	@Override
	public String getDefinedString(List<String> path, String defaultValue, Collection<String> validValues, String comment) {
		return getDefinedString(path, defaultValue, validValues::contains, comment);
	}

	@Override
	public String getDefinedString(String path, String defaultValue, Predicate<String> validator, String comment) {
		return getDefinedString(split(path), defaultValue, validator, comment);
	}

	@Override
	public String getDefinedString(List<String> path, String defaultValue, Predicate<String> validator, String comment) {
		if(!config.contains(path) || !validator.test(""+config.<Object>get(path))) {
			config.set(path, defaultValue);
		}
		if(comment != null) {
			config.setComment(path, comment);
		}
		return ""+config.<Object>get(path);
	}

	@Override
	public List<String> getDefinedStringList(String path, List<String> defaultValue, String comment) {
		return getDefinedStringList(split(path), defaultValue, comment);
	}

	@Override
	public List<String> getDefinedStringList(List<String> path, List<String> defaultValue, String comment) {
		return getDefinedStringList(path, defaultValue, v->true, comment);
	}

	@Override
	public List<String> getDefinedStringList(String path, List<String> defaultValue, Collection<String> validValues, String comment) {
		return getDefinedStringList(split(path), defaultValue, validValues, comment);
	}

	@Override
	public List<String> getDefinedStringList(List<String> path, List<String> defaultValue, Collection<String> validValues, String comment) {
		return getDefinedStringList(path, defaultValue, validValues::contains, comment);
	}

	@Override
	public List<String> getDefinedStringList(String path, List<String> defaultValue, Predicate<String> elementValidator, String comment) {
		return getDefinedStringList(split(path), defaultValue, elementValidator, comment);
	}

	@Override
	public List<String> getDefinedStringList(List<String> path, List<String> defaultValue, Predicate<String> elementValidator, String comment) {
		if(!config.contains(path) || !(config.get(path) instanceof List<?>)) {
			config.set(path, defaultValue);
		}
		List<?> list = config.<List<?>>get(path);
		list.removeIf(obj->!elementValidator.test(""+obj));
		if(comment != null) {
			config.setComment(path, comment);
		}
		return list.stream().map(Object::toString).collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public boolean getDefinedBoolean(String path, boolean defaultValue, String comment) {
		return getDefinedBoolean(split(path), defaultValue, comment);
	}

	@Override
	public boolean getDefinedBoolean(List<String> path, boolean defaultValue, String comment) {
		if(!config.contains(path) || !(config.get(path) instanceof Boolean)) {
			config.set(path, defaultValue);
		}
		if(comment != null) {
			config.setComment(path, comment);
		}
		return config.get(path);
	}

	@Override
	public int getDefinedInt(String path, int defaultValue, String comment) {
		return getDefinedInt(split(path), defaultValue, comment);
	}

	@Override
	public int getDefinedInt(List<String> path, int defaultValue, String comment) {
		return getDefinedInt(path, defaultValue, value->true, comment);
	}

	@Override
	public int getDefinedInt(String path, int defaultValue, int min, int max, String comment) {
		return getDefinedInt(split(path), defaultValue, min, max, comment);
	}

	@Override
	public int getDefinedInt(List<String> path, int defaultValue, int min, int max, String comment) {
		return getDefinedInt(path, defaultValue, value->value >= min && value <= max, comment);
	}

	@Override
	public int getDefinedInt(String path, int defaultValue, IntPredicate validator, String comment) {
		return getDefinedInt(split(path), defaultValue, validator, comment);
	}

	@Override
	public int getDefinedInt(List<String> path, int defaultValue, IntPredicate validator, String comment) {
		if(!config.contains(path) || !(config.get(path) instanceof Number) || !validator.test(config.getInt(path))) {
			config.set(path, defaultValue);
		}
		if(comment != null) {
			config.setComment(path, comment);
		}
		return config.getInt(path);
	}

	@Override
	public long getDefinedLong(String path, long defaultValue, String comment) {
		return getDefinedLong(split(path), defaultValue, comment);
	}

	@Override
	public long getDefinedLong(List<String> path, long defaultValue, String comment) {
		return getDefinedLong(path, defaultValue, value->true, comment);
	}

	@Override
	public long getDefinedLong(String path, long defaultValue, long min, long max, String comment) {
		return getDefinedLong(split(path), defaultValue, min, max, comment);
	}

	@Override
	public long getDefinedLong(List<String> path, long defaultValue, long min, long max, String comment) {
		return getDefinedLong(path, defaultValue, value->value >= min && value <= max, comment);
	}

	@Override
	public long getDefinedLong(String path, long defaultValue, LongPredicate validator, String comment) {
		return getDefinedLong(split(path), defaultValue, validator, comment);
	}

	@Override
	public long getDefinedLong(List<String> path, long defaultValue, LongPredicate validator, String comment) {
		if(!config.contains(path) || !(config.get(path) instanceof Number) || !validator.test(config.getLong(path))) {
			config.set(path, defaultValue);
		}
		if(comment != null) {
			config.setComment(path, comment);
		}
		return config.getLong(path);
	}

	@Override
	public float getDefinedFloat(String path, float defaultValue, String comment) {
		return getDefinedFloat(split(path), defaultValue, comment);
	}

	@Override
	public float getDefinedFloat(List<String> path, float defaultValue, String comment) {
		return getDefinedFloat(path, defaultValue, v->true, comment);
	}

	@Override
	public float getDefinedFloat(String path, float defaultValue, float min, float max, String comment) {
		return getDefinedFloat(split(path), defaultValue, min, max, comment);
	}

	@Override
	public float getDefinedFloat(List<String> path, float defaultValue, float min, float max, String comment) {
		return getDefinedFloat(path, defaultValue, value->value >= min && value <= max, comment);
	}

	@Override
	public float getDefinedFloat(String path, float defaultValue, Predicate<Float> validator, String comment) {
		return getDefinedFloat(split(path), defaultValue, validator, comment);
	}

	@Override
	public float getDefinedFloat(List<String> path, float defaultValue, Predicate<Float> validator, String comment) {
		if(!config.contains(path) || !(config.get(path) instanceof Number) || !validator.test(config.<Number>getRaw(path).floatValue())) {
			config.set(path, (double)defaultValue);
		}
		if(comment != null) {
			config.setComment(path, comment);
		}
		return config.<Number>getRaw(path).floatValue();
	}

	@Override
	public double getDefinedDouble(String path, double defaultValue, String comment) {
		return getDefinedDouble(split(path), defaultValue, comment);
	}

	@Override
	public double getDefinedDouble(List<String> path, double defaultValue, String comment) {
		return getDefinedDouble(path, defaultValue, value->true, comment);
	}

	@Override
	public double getDefinedDouble(String path, double defaultValue, double min, double max, String comment) {
		return getDefinedDouble(split(path), defaultValue, min, max, comment);
	}

	@Override
	public double getDefinedDouble(List<String> path, double defaultValue, double min, double max, String comment) {
		return getDefinedDouble(path, defaultValue, value->value >= min && value <= max, comment);
	}

	@Override
	public double getDefinedDouble(String path, double defaultValue, DoublePredicate validator, String comment) {
		return getDefinedDouble(split(path), defaultValue, validator, comment);
	}

	@Override
	public double getDefinedDouble(List<String> path, double defaultValue, DoublePredicate validator, String comment) {
		if(!config.contains(path) || !(config.get(path) instanceof Number) || !validator.test(config.<Number>getRaw(path).doubleValue())) {
			config.set(path, defaultValue);
		}
		if(comment != null) {
			config.setComment(path, comment);
		}
		return config.<Number>getRaw(path).doubleValue();
	}

	@Override
	public byte getDefinedByte(String path, byte defaultValue, String comment) {
		return getDefinedByte(split(path), defaultValue, comment);
	}

	@Override
	public byte getDefinedByte(List<String> path, byte defaultValue, String comment) {
		return getDefinedByte(path, defaultValue, v->true, comment);
	}

	@Override
	public byte getDefinedByte(String path, byte defaultValue, byte min, byte max, String comment) {
		return getDefinedByte(split(path), defaultValue, min, max, comment);
	}

	@Override
	public byte getDefinedByte(List<String> path, byte defaultValue, byte min, byte max, String comment) {
		return getDefinedByte(path, defaultValue, value->value >= min && value <= max, comment);
	}

	@Override
	public byte getDefinedByte(String path, byte defaultValue, Predicate<Byte> validator, String comment) {
		return getDefinedByte(split(path), defaultValue, validator, comment);
	}

	@Override
	public byte getDefinedByte(List<String> path, byte defaultValue, Predicate<Byte> validator, String comment) {
		if(!config.contains(path) || !(config.get(path) instanceof Number) || !validator.test(config.getByte(path))) {
			config.set(path, (int)defaultValue);
		}
		if(comment != null) {
			config.setComment(path, comment);
		}
		return config.getByte(path);
	}

	@Override
	public short getDefinedShort(String path, short defaultValue, String comment) {
		return getDefinedShort(split(path), defaultValue, comment);
	}

	@Override
	public short getDefinedShort(List<String> path, short defaultValue, String comment) {
		return getDefinedShort(path, defaultValue, v->true, comment);
	}

	@Override
	public short getDefinedShort(String path, short defaultValue, short min, short max, String comment) {
		return getDefinedShort(split(path), defaultValue, min, max, comment);
	}

	@Override
	public short getDefinedShort(List<String> path, short defaultValue, short min, short max, String comment) {
		return getDefinedShort(path, defaultValue, value->value >= min && value <= max, comment);
	}

	@Override
	public short getDefinedShort(String path, short defaultValue, Predicate<Short> validator, String comment) {
		return getDefinedShort(split(path), defaultValue, validator, comment);
	}

	@Override
	public short getDefinedShort(List<String> path, short defaultValue, Predicate<Short> validator, String comment) {
		if(!config.contains(path) || !(config.get(path) instanceof Number) || !validator.test(config.getShort(path))) {
			config.set(path, (int)defaultValue);
		}
		if(comment != null) {
			config.setComment(path, comment);
		}
		return config.getShort(path);
	}

	@Override
	public char getDefinedChar(String path, char defaultValue, String comment) {
		return getDefinedChar(split(path), defaultValue, comment);
	}

	@Override
	public char getDefinedChar(List<String> path, char defaultValue, String comment) {
		return getDefinedChar(path, defaultValue, v->true, comment);
	}

	@Override
	public char getDefinedChar(String path, char defaultValue, Collection<Character> validValues, String comment) {
		return getDefinedChar(split(path), defaultValue, validValues, comment);
	}

	@Override
	public char getDefinedChar(List<String> path, char defaultValue, Collection<Character> validValues, String comment) {
		return getDefinedChar(path, defaultValue, validValues::contains, comment);
	}

	@Override
	public char getDefinedChar(String path, char defaultValue, Predicate<Character> validator, String comment) {
		return getDefinedChar(split(path), defaultValue, validator, comment);
	}

	@Override
	public char getDefinedChar(List<String> path, char defaultValue, Predicate<Character> validator, String comment) {
		if(!config.contains(path) || !(config.get(path) instanceof Number) && !(config.get(path) instanceof CharSequence) || !validator.test(config.getChar(path))) {
			config.set(path, Character.toString(defaultValue));
		}
		if(comment != null) {
			config.setComment(path, comment);
		}
		return config.getChar(path);
	}

	@Override
	public <T extends Enum<T>> T getDefinedEnum(String path, Class<T> enumType, T defaultValue, String comment) {
		return getDefinedEnum(split(path), enumType, defaultValue, comment);
	}

	@Override
	public <T extends Enum<T>> T getDefinedEnum(List<String> path, Class<T> enumType, T defaultValue, String comment) {
		return getDefinedEnum(path, enumType, defaultValue, v->true, comment);
	}

	@Override
	public <T extends Enum<T>> T getDefinedEnum(String path, Class<T> enumType, T defaultValue, Collection<T> validValues, String comment) {
		return getDefinedEnum(split(path), enumType, defaultValue, validValues, comment);
	}

	@Override
	public <T extends Enum<T>> T getDefinedEnum(List<String> path, Class<T> enumType, T defaultValue, Collection<T> validValues, String comment) {
		return getDefinedEnum(path, enumType, defaultValue, validValues::contains, comment);
	}

	@Override
	public <T extends Enum<T>> T getDefinedEnum(String path, Class<T> enumType, T defaultValue, Predicate<T> validator, String comment) {
		return getDefinedEnum(split(path), enumType, defaultValue, validator, comment);
	}

	@Override
	public <T extends Enum<T>> T getDefinedEnum(List<String> path, Class<T> enumType, T defaultValue, Predicate<T> validator, String comment) {
		if(!config.contains(path) || !(config.get(path) instanceof CharSequence) ||  !(config.get(path) instanceof Number) ||!validator.test(config.getEnum(path, enumType, EnumGetMethod.ORDINAL_OR_NAME_IGNORECASE))) {
			config.set(path, defaultValue.name());
		}
		if(comment != null) {
			config.setComment(path, comment);
		}
		return config.getEnum(path, enumType, EnumGetMethod.ORDINAL_OR_NAME_IGNORECASE);
	}

	static List<String> split(String str) {
		return Lists.newArrayList(StringUtils.split(str, '.'));
	}
}
