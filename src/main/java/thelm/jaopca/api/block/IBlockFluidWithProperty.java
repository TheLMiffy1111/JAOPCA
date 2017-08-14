package thelm.jaopca.api.block;

import thelm.jaopca.api.IObjectWithProperty;
import thelm.jaopca.api.IOreEntry;
import thelm.jaopca.api.ItemEntry;

public interface IBlockFluidWithProperty extends IObjectWithProperty {
	
	IBlockFluidWithProperty setQuantaPerBlock(int quantaPerBlock);
}
