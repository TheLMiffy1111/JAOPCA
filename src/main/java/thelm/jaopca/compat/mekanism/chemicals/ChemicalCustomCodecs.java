package thelm.jaopca.compat.mekanism.chemicals;

import com.google.common.base.Functions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thelm.jaopca.api.custom.CustomCodecs;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.compat.mekanism.api.chemicals.IChemicalFormSettings;

public class ChemicalCustomCodecs {

	private ChemicalCustomCodecs() {}

	public static final Codec<IFormSettings> CHEMICAL_FORM_SETTINGS =
			CustomCodecs.<IChemicalFormSettings>builder(
					instance->RecordCodecBuilder.point(ChemicalFormType.INSTANCE.getNewSettings())).
			withField(
					CustomCodecs.materialStringFunction("").optionalFieldOf("oreTag"),
					s->s.getOreTagFunction(),
					(s, f)->s.setOreTagFunction(f)).
			build().
			flatComapMap(Functions.identity(),
					s->s instanceof IChemicalFormSettings ss ? DataResult.success(ss) : DataResult.error(()->"Not chemical form settings"));
}
