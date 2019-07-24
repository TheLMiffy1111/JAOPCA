package thelm.jaopca.compat.modules;

import com.EmosewaPixel.pixellib.materialsystem.lists.Materials;
import com.EmosewaPixel.pixellib.materialsystem.lists.ObjTypes;
import com.EmosewaPixel.pixellib.materialsystem.materials.DustMaterial;
import com.EmosewaPixel.pixellib.materialsystem.types.BlockType;
import com.EmosewaPixel.pixellib.materialsystem.types.ItemType;

import net.minecraft.util.ResourceLocation;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.JAOPCAModule;

@JAOPCAModule
public class PixelLibModule implements IModule {

	public PixelLibModule() {
		JAOPCAApi api = JAOPCAApi.instance();
		Materials.getAll().forEach(mat->{
			ObjTypes.getAll().stream().
			filter(type->(mat instanceof DustMaterial && type instanceof BlockType && type.isMaterialCompatible(mat) && !mat.hasBlacklisted(type))).
			forEach(type->{
				api.registerDefinedBlockTag(new ResourceLocation("forge", type.getName()+"s/"+mat.getName()));
				api.registerDefinedItemTag(new ResourceLocation("forge", type.getName()+"s/"+mat.getName()));
				api.registerDefinedBlockTag(new ResourceLocation("forge", type.getName()+"s"));
				api.registerDefinedItemTag(new ResourceLocation("forge", type.getName()+"s"));
				if(mat.hasSecondName()) {
					api.registerDefinedBlockTag(new ResourceLocation("forge", type.getName()+"s/"+mat.getSecondName()));
					api.registerDefinedItemTag(new ResourceLocation("forge", type.getName()+"s/"+mat.getSecondName()));
				}
			});
			ObjTypes.getAll().stream().
			filter(type->(type instanceof ItemType && type.isMaterialCompatible(mat) && !mat.hasBlacklisted(type))).
			forEach(type->{
				api.registerDefinedItemTag(new ResourceLocation("forge", type.getName()+"s/"+mat.getName()));
				api.registerDefinedItemTag(new ResourceLocation("forge", type.getName()+"s"));
				if(mat.hasSecondName()) {
					api.registerDefinedItemTag(new ResourceLocation("forge", type.getName()+"s/"+mat.getSecondName()));
				}
			});
		});
	}

	@Override
	public String getName() {
		return "pixellib";
	}
}
