package thelm.jaopca.api.forms;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;

public interface IFormRequest {

	IModule getModule();

	List<IForm> getForms();

	boolean isGrouped();

	IFormRequest setGrouped(boolean grouped);

	Set<IMaterial> getMaterials();

	boolean isMaterialGroupValid(IMaterial material);

	void setMaterials(Collection<IMaterial> materials);
}
