package thelm.jaopca.api.forms;

import java.util.List;

import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.modules.IModule;

public interface IFormRequest {

	IModule getModule();

	List<IForm> getForms();

	boolean isGrouped();

	IFormRequest setGrouped(boolean grouped);

	boolean isMaterialGroupValid(IMaterial material);
}
