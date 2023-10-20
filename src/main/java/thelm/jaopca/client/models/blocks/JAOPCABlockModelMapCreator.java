package thelm.jaopca.client.models.blocks;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.blocks.IBlockFormSettings;
import thelm.jaopca.api.blocks.IBlockModelMapCreator;
import thelm.jaopca.api.blocks.IMaterialFormBlock;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCABlockModelMapCreator implements IBlockModelMapCreator {

	public static final JAOPCABlockModelMapCreator INSTANCE = new JAOPCABlockModelMapCreator();

	@Override
	public Map<IBlockState, ModelResourceLocation> create(IMaterialFormBlock block, IBlockFormSettings settings) {
		ResourceLocation baseModelLocation = getBaseModelLocation(block);
		Map<IBlockState, ModelResourceLocation> map = new LinkedHashMap<>();
		for(IBlockState state : block.asBlock().getBlockState().getValidStates()) {
			map.put(state, getModelLocation(baseModelLocation, state));
		}
		return map;
	}

	public ResourceLocation getBaseModelLocation(IMaterialFormBlock materialFormBlock) {
		Block block = materialFormBlock.asBlock();
		ResourceLocation location = block.getRegistryName();
		ResourceLocation location1 = new ResourceLocation(location.getNamespace(), "blockstates/"+location.getPath()+".json");
		if(MiscHelper.INSTANCE.hasResource(location1)) {
			return location;
		}
		else {
			return new ResourceLocation(location.getNamespace(),
					materialFormBlock.getMaterial().getModelType()+'/'+materialFormBlock.getForm().getName());
		}
	}

	public String getPropertyString(Map<IProperty<?>, Comparable<?>> values) {
		StringBuilder stringbuilder = new StringBuilder();
		for(Entry<IProperty<?>, Comparable<?>> entry : values.entrySet()) {
			if(stringbuilder.length() != 0) {
				stringbuilder.append(",");
			}
			IProperty<?> property = entry.getKey();
			stringbuilder.append(property.getName());
			stringbuilder.append("=");
			stringbuilder.append(getPropertyName(property, entry.getValue()));
		}
		if(stringbuilder.length() == 0) {
			stringbuilder.append("normal");
		}
		return stringbuilder.toString();
	}

	public <T extends Comparable<T>> String getPropertyName(IProperty<T> property, Comparable<?> value) {
		return property.getName((T)value);
	}

	public ModelResourceLocation getModelLocation(ResourceLocation location, IBlockState state) {
		return new ModelResourceLocation(location, getPropertyString(new LinkedHashMap<>(state.getProperties())));
	}
}
