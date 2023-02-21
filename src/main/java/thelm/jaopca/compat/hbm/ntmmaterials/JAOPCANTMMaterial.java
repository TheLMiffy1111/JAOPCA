package thelm.jaopca.compat.hbm.ntmmaterials;

import java.util.ArrayList;
import java.util.List;

import com.hbm.inventory.OreDictManager;
import com.hbm.inventory.material.NTMMaterial;

import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materialforms.IMaterialForm;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.utils.ApiImpl;

public class JAOPCANTMMaterial extends NTMMaterial implements IMaterialForm {

	private final IForm form;
	private final IMaterial material;

	public JAOPCANTMMaterial(int id, IForm form, IMaterial material) {
		super(id, getDictFrame(material));
		this.omitItemGen = true;
		this.smeltable = SmeltingBehavior.SMELTABLE;
		this.form = form;
		this.material = material;
	}

	@Override
	public IForm getForm() {
		return form;
	}

	@Override
	public IMaterial getIMaterial() {
		return material;
	}

	@Override
	public String getUnlocalizedName() {
		return ApiImpl.INSTANCE.currentLocalizer().localizeMaterial(material);
	}

	public void setColors() {
		moltenColor = solidColor = material.getColor();
	}

	public static OreDictManager.DictFrame getDictFrame(IMaterial material) {
		List<String> names = new ArrayList<>();
		names.add(material.getName());
		names.addAll(material.getAlternativeNames());
		return new OreDictManager.DictFrame(names.toArray(new String[names.size()]));
	}
}
