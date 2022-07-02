package thelm.jaopca.api.config;

import java.util.Collection;
import java.util.List;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import com.electronwill.nightconfig.core.CommentedConfig;

public interface IDynamicSpecConfig extends CommentedConfig {

	String getDefinedString(String path, String defaultValue, String comment);

	String getDefinedString(List<String> path, String defaultValue, String comment);

	String getDefinedString(String path, String defaultValue, Collection<String> validValues, String comment);

	String getDefinedString(List<String> path, String defaultValue, Collection<String> validValues, String comment);

	String getDefinedString(String path, String defaultValue, Predicate<String> validator, String comment);

	String getDefinedString(List<String> path, String defaultValue, Predicate<String> validator, String comment);

	List<String> getDefinedStringList(String path, List<String> defaultValue, String comment);

	List<String> getDefinedStringList(List<String> path, List<String> defaultValue, String comment);

	List<String> getDefinedStringList(String path, List<String> defaultValue, Collection<String> validValues, String comment);

	List<String> getDefinedStringList(List<String> path, List<String> defaultValue, Collection<String> validValues, String comment);

	List<String> getDefinedStringList(String path, List<String> defaultValue, Predicate<String> elementValidator, String comment);

	List<String> getDefinedStringList(List<String> path, List<String> defaultValue, Predicate<String> elementValidator, String comment);

	boolean getDefinedBoolean(String path, boolean defaultValue, String comment);

	boolean getDefinedBoolean(List<String> path, boolean defaultValue, String comment);

	int getDefinedInt(String path, int defaultValue, String comment);

	int getDefinedInt(List<String> path, int defaultValue, String comment);

	int getDefinedInt(String path, int defaultValue, int min, int max, String comment);

	int getDefinedInt(List<String> path, int defaultValue, int min, int max, String comment);

	int getDefinedInt(String path, int defaultValue, IntPredicate validator, String comment);

	int getDefinedInt(List<String> path, int defaultValue, IntPredicate validator, String comment);

	long getDefinedLong(String path, long defaultValue, String comment);

	long getDefinedLong(List<String> path, long defaultValue, String comment);

	long getDefinedLong(String path, long defaultValue, long min, long max, String comment);

	long getDefinedLong(List<String> path, long defaultValue, long min, long max, String comment);

	long getDefinedLong(String path, long defaultValue, LongPredicate validator, String comment);

	long getDefinedLong(List<String> path, long defaultValue, LongPredicate validator, String comment);

	float getDefinedFloat(String path, float defaultValue, String comment);

	float getDefinedFloat(List<String> path, float defaultValue, String comment);

	float getDefinedFloat(String path, float defaultValue, float min, float max, String comment);

	float getDefinedFloat(List<String> path, float defaultValue, float min, float max, String comment);

	float getDefinedFloat(String path, float defaultValue, Predicate<Float> validator, String comment);

	float getDefinedFloat(List<String> path, float defaultValue, Predicate<Float> validator, String comment);

	double getDefinedDouble(String path, double defaultValue, String comment);

	double getDefinedDouble(List<String> path, double defaultValue, String comment);

	double getDefinedDouble(String path, double defaultValue, double min, double max, String comment);

	double getDefinedDouble(List<String> path, double defaultValue, double min, double max, String comment);

	double getDefinedDouble(String path, double defaultValue, DoublePredicate validator, String comment);

	double getDefinedDouble(List<String> path, double defaultValue, DoublePredicate validator, String comment);

	byte getDefinedByte(String path, byte defaultValue, String comment);

	byte getDefinedByte(List<String> path, byte defaultValue, String comment);

	byte getDefinedByte(String path, byte defaultValue, byte min, byte max, String comment);

	byte getDefinedByte(List<String> path, byte defaultValue, byte min, byte max, String comment);

	byte getDefinedByte(String path, byte defaultValue, Predicate<Byte> validator, String comment);

	byte getDefinedByte(List<String> path, byte defaultValue, Predicate<Byte> validator, String comment);

	short getDefinedShort(String path, short defaultValue, String comment);

	short getDefinedShort(List<String> path, short defaultValue, String comment);

	short getDefinedShort(String path, short defaultValue, short min, short max, String comment);

	short getDefinedShort(List<String> path, short defaultValue, short min, short max, String comment);

	short getDefinedShort(String path, short defaultValue, Predicate<Short> validator, String comment);

	short getDefinedShort(List<String> path, short defaultValue, Predicate<Short> validator, String comment);

	char getDefinedChar(String path, char defaultValue, String comment);

	char getDefinedChar(List<String> path, char defaultValue, String comment);

	char getDefinedChar(String path, char defaultValue, Collection<Character> validValues, String comment);

	char getDefinedChar(List<String> path, char defaultValue, Collection<Character> validValues, String comment);

	char getDefinedChar(String path, char defaultValue, Predicate<Character> validator, String comment);

	char getDefinedChar(List<String> path, char defaultValue, Predicate<Character> validator, String comment);

	<T extends Enum<T>> T getDefinedEnum(List<String> path, Class<T> enumType, T defaultValue, String comment);

	<T extends Enum<T>> T getDefinedEnum(String path, Class<T> enumType, T defaultValue, String comment);

	<T extends Enum<T>> T getDefinedEnum(List<String> path, Class<T> enumType, T defaultValue, Collection<T> validValues, String comment);

	<T extends Enum<T>> T getDefinedEnum(String path, Class<T> enumType, T defaultValue, Collection<T> validValues, String comment);

	<T extends Enum<T>> T getDefinedEnum(List<String> path, Class<T> enumType, T defaultValue, Predicate<T> validator, String comment);

	<T extends Enum<T>> T getDefinedEnum(String path, Class<T> enumType, T defaultValue, Predicate<T> validator, String comment);
}
