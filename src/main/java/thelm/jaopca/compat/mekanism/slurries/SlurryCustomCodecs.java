package thelm.jaopca.compat.mekanism.slurries;

import com.google.common.base.Functions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thelm.jaopca.api.custom.CustomCodecs;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.compat.mekanism.api.slurries.ISlurryFormSettings;

public class SlurryCustomCodecs {

	private SlurryCustomCodecs() {}

	public static final Codec<IFormSettings> SLURRY_FORM_SETTINGS =
			CustomCodecs.builder(
					instance->RecordCodecBuilder.point(SlurryFormType.INSTANCE.getNewSettings())).
			withField(
					Codec.BOOL.optionalFieldOf("isHidden"),
					s->s.getIsHidden(),
					(s, f)->s.setIsHidden(f)).
			build().
			flatComapMap(Functions.identity(),
					s->s instanceof ISlurryFormSettings ss ? DataResult.success(ss) : DataResult.error(()->"Not slurry form settings"));
}
