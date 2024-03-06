package thelm.jaopca.items;

import com.google.common.base.Functions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.Rarity;
import thelm.jaopca.api.custom.CustomCodecs;
import thelm.jaopca.api.forms.IFormSettings;
import thelm.jaopca.api.items.IItemFormSettings;

public class ItemCustomCodecs {

	private ItemCustomCodecs() {}

	public static final Codec<IFormSettings> ITEM_FORM_SETTINGS =
			CustomCodecs.builder(
					instance->RecordCodecBuilder.point(ItemFormType.INSTANCE.getNewSettings())).
			withField(
					CustomCodecs.materialIntFunction(64).optionalFieldOf("maxStackSize"),
					s->s.getMaxStackSizeFunction(),
					(s, f)->s.setMaxStackSizeFunction(f)).
			withField(
					CustomCodecs.materialPredicate(false).optionalFieldOf("hasEffect"),
					s->s.getHasEffectFunction(),
					(s, f)->s.setHasEffectFunction(f)).
			withField(
					CustomCodecs.materialEnumFunction(Rarity.class, Rarity.COMMON).optionalFieldOf("rarity"),
					s->s.getDisplayRarityFunction(),
					(s, f)->s.setDisplayRarityFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(-1).optionalFieldOf("burnTime"),
					s->s.getBurnTimeFunction(),
					(s, f)->s.setBurnTimeFunction(f)).
			build().
			flatComapMap(Functions.identity(),
					s->s instanceof IItemFormSettings is ? DataResult.success(is) : DataResult.error(()->"Not item form settings"));
}
