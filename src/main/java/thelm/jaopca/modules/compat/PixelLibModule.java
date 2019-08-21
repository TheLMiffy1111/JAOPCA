package thelm.jaopca.modules.compat;

import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.JAOPCAModule;

@JAOPCAModule(modDependencies = "pixellib")
public class PixelLibModule implements IModule {

	public PixelLibModule() {
		/*
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
		 */
	}

	@Override
	public String getName() {
		return "pixellib";
	}
}
