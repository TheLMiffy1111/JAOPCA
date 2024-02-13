package thelm.jaopca.client.models.fluids;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidBase;
import thelm.jaopca.api.fluids.IFluidBlockModelMapCreator;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluidBlock;
import thelm.jaopca.utils.MiscHelper;

public class JAOPCAFluidBlockModelMapCreator implements IFluidBlockModelMapCreator {

	public static final JAOPCAFluidBlockModelMapCreator INSTANCE = new JAOPCAFluidBlockModelMapCreator();

	@Override
	public Map<IBlockState, ModelResourceLocation> create(IMaterialFormFluidBlock fluidBlock, IFluidFormSettings settings) {
		ResourceLocation baseModelLocation = getBaseModelLocation(fluidBlock);
		Map<IBlockState, ModelResourceLocation> map = new LinkedHashMap<>();
		for(IBlockState state : fluidBlock.toBlock().getBlockState().getValidStates()) {
			map.put(state, getModelLocation(baseModelLocation, state));
		}
		return map;
	}

	public ResourceLocation getBaseModelLocation(IMaterialFormFluidBlock materialFormFluidBlock) {
		Block block = materialFormFluidBlock.toBlock();
		ResourceLocation location = block.getRegistryName();
		ResourceLocation location1 = new ResourceLocation(location.getNamespace(), "blockstates/"+location.getPath()+".json");
		if(MiscHelper.INSTANCE.hasResource(location1)) {
			return location;
		}
		else {
			return new ResourceLocation(location.getNamespace(),
					materialFormFluidBlock.getMaterial().getModelType()+'/'+materialFormFluidBlock.getForm().getName());
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
		Map<IProperty<?>, Comparable<?>> map = new LinkedHashMap<>(state.getProperties());
		map.remove(BlockFluidBase.LEVEL);
		return new ModelResourceLocation(location, getPropertyString(map));
	}
}
