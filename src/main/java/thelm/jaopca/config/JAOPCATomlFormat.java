package thelm.jaopca.config;

import java.time.temporal.Temporal;
import java.util.Map;
import java.util.function.Supplier;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;

public class JAOPCATomlFormat implements ConfigFormat<CommentedConfig> {

	public static final JAOPCATomlFormat INSTANCE = new JAOPCATomlFormat();

	private JAOPCATomlFormat() {}

	@Override
	public TomlWriter createWriter() {
		TomlWriter writer = new TomlWriter();
		writer.setIndentArrayElementsPredicate(list->list.stream().allMatch(String.class::isInstance));
		return writer;
	}

	@Override
	public TomlParser createParser() {
		TomlParser parser = new TomlParser();
		parser.setLenientWithSeparators(true);
		parser.setLenientWithBareKeys(true);
		return parser;
	}

	@Override
	public CommentedConfig createConfig(Supplier<Map<String, Object>> mapCreator) {
		return CommentedConfig.of(mapCreator, this);
	}

	@Override
	public boolean supportsComments() {
		return true;
	}

	@Override
	public boolean supportsType(Class<?> type) {
		return type != null && (ConfigFormat.super.supportsType(type) || Temporal.class.isAssignableFrom(type));
	}
}
