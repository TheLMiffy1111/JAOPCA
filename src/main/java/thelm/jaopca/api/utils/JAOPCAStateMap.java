package thelm.jaopca.api.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class JAOPCAStateMap extends StateMapperBase {

	private ResourceLocation location;
	private List<IProperty<?>> ignored;

	private JAOPCAStateMap(ResourceLocation location, List<IProperty<?>> ignored) {
		this.location = location;
		this.ignored = ignored;
	}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		if(!(location instanceof ModelResourceLocation)) {
			LinkedHashMap<IProperty<?>,Comparable<?>> map = Maps.<IProperty<?>,Comparable<?>>newLinkedHashMap(state.getProperties());
			for(IProperty<?> iproperty : ignored) {
				map.remove(iproperty);
			}
			return new ModelResourceLocation(location, getPropertyString(map));
		}
		return (ModelResourceLocation)location;
	}

	public static class Builder {
		private ResourceLocation location;
		private ArrayList<IProperty<?>> ignored = Lists.<IProperty<?>>newArrayList();

		public Builder(ResourceLocation location) {
			this.location = location;
		}

		public Builder ignore(IProperty<?>... ignored) {
			Collections.addAll(this.ignored, ignored);
			return this;
		}

		public JAOPCAStateMap build() {
			return new JAOPCAStateMap(location, ignored);
		}
	}
}
