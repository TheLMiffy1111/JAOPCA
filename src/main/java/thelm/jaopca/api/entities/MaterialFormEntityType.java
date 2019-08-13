package thelm.jaopca.api.entities;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import com.mojang.datafixers.types.Type;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages.SpawnEntity;
import thelm.jaopca.api.materialforms.IMaterialForm;

public abstract class MaterialFormEntityType<T extends Entity> extends EntityType<T> implements IMaterialForm {

	public MaterialFormEntityType(IFactory<T> factory, boolean serializable, Predicate<EntityType<?>> velocityUpdateSupplier, ToIntFunction<EntityType<?>> trackingRangeSupplier, ToIntFunction<EntityType<?>> updateIntervalSupplier, BiFunction<SpawnEntity, World, T> customClientFactory) {
		super(factory, EntityClassification.MONSTER, serializable, true, false, true, EntitySize.flexible(0, 0), velocityUpdateSupplier, trackingRangeSupplier, updateIntervalSupplier, customClientFactory);
	}
}
