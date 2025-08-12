package thelm.jaopca.api.config;

import java.util.function.Function;

public class FormattedNumber extends Number {

	public final Function<Number, String> format;
	public final Number value;

	public FormattedNumber(Function<Number, String> format, Number value) {
		this.format = format;
		this.value = value;
	}

	public FormattedNumber(String format, Number value) {
		this.format = format::formatted;
		this.value = value;
	}

	@Override
	public int intValue() {
		return value.intValue();
	}

	@Override
	public long longValue() {
		return value.longValue();
	}

	@Override
	public float floatValue() {
		return value.floatValue();
	}

	@Override
	public double doubleValue() {
		return value.doubleValue();
	}

	@Override
	public String toString() {
		return format.apply(value);
	}
}
