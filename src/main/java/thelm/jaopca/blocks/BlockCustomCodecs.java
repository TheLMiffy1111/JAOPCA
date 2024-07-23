package thelm.jaopca.blocks;

import com.google.common.base.Functions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.custom.CustomCodecs;
import thelm.jaopca.api.forms.IFormSettings;

public class BlockCustomCodecs {

	private BlockCustomCodecs() {}

	public static final Codec<IFormSettings> BLOCK_FORM_SETTINGS =
			CustomCodecs.<IBlockFormSettings>builder(
					instance->RecordCodecBuilder.point(BlockFormType.INSTANCE.getNewSettings())).
			withField(
					CustomCodecs.materialMapColorFunction(MapColor.METAL).optionalFieldOf("mapColor"),
					s->s.getMapColorFunction(),
					(s, f)->s.setMapColorFunction(f)).
			withField(
					Codec.BOOL.optionalFieldOf("replaceable"),
					s->s.getReplaceable(),
					(s, f)->s.setReplaceable(f)).
			withField(
					CustomCodecs.materialSoundTypeFunction(SoundType.METAL).optionalFieldOf("soundType"),
					s->s.getSoundTypeFunction(),
					(s, f)->s.setSoundTypeFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(15).optionalFieldOf("lightOpacity"),
					s->s.getLightOpacityFunction(),
					(s, f)->s.setLightOpacityFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(0).optionalFieldOf("lightValue"),
					s->s.getLightValueFunction(),
					(s, f)->s.setLightValueFunction(f)).
			withField(
					CustomCodecs.materialDoubleFunction(5).optionalFieldOf("blockHardness"),
					s->s.getBlockHardnessFunction(),
					(s, f)->s.setBlockHardnessFunction(f)).
			withField(
					CustomCodecs.materialDoubleFunction(6).optionalFieldOf("explosionResistance"),
					s->s.getExplosionResistanceFunction(),
					(s, f)->s.setExplosionResistanceFunction(f)).
			withField(
					CustomCodecs.materialDoubleFunction(0.6).optionalFieldOf("friction"),
					s->s.getFrictionFunction(),
					(s, f)->s.setFrictionFunction(f)).
			withField(
					CustomCodecs.VOXEL_SHAPE.optionalFieldOf("shape"),
					s->s.getShape(),
					(s, f)->s.setShape(f)).
			withField(
					CustomCodecs.VOXEL_SHAPE.optionalFieldOf("interactionShape"),
					s->s.getInteractionShape(),
					(s, f)->s.setInteractionShape(f)).
			withField(
					CustomCodecs.materialPredicate(false).optionalFieldOf("requiresTool"),
					s->s.getRequiresToolFunction(),
					(s, f)->s.setRequiresToolFunction(f)).
			withField(
					CustomCodecs.materialStringFunction("minecraft:mineable/pickaxe").optionalFieldOf("harvestToolTag"),
					s->s.getHarvestToolTagFunction(),
					(s, f)->s.setHarvestToolTagFunction(f)).
			withField(
					CustomCodecs.materialStringFunction("").optionalFieldOf("harvestTierTag"),
					s->s.getHarvestTierTagFunction(),
					(s, f)->s.setHarvestTierTagFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(0).optionalFieldOf("flammability"),
					s->s.getFlammabilityFunction(),
					(s, f)->s.setFlammabilityFunction(f)).
			withField(
					CustomCodecs.materialIntFunction(0).optionalFieldOf("fireSpreadSpeed"),
					s->s.getFireSpreadSpeedFunction(),
					(s, f)->s.setFireSpreadSpeedFunction(f)).
			withField(
					CustomCodecs.materialPredicate(false).optionalFieldOf("isFireSource"),
					s->s.getIsFireSourceFunction(),
					(s, f)->s.setIsFireSourceFunction(f)).
			withField(
					CustomCodecs.materialEnumFunction(PushReaction.class, PushReaction.NORMAL).optionalFieldOf("pushReaction"),
					s->s.getPushReactionFunction(),
					(s, f)->s.setPushReactionFunction(f)).
			withField(
					CustomCodecs.materialEnumFunction(NoteBlockInstrument.class, NoteBlockInstrument.HARP).optionalFieldOf("instrument"),
					s->s.getInstrumentFunction(),
					(s, f)->s.setInstrumentFunction(f)).
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
					s->s instanceof IBlockFormSettings bs ? DataResult.success(bs) : DataResult.error(()->"Not block form settings"));
}
