package thelm.jaopca.tags;

import java.util.Collections;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags;

public class EmptyNamedTag<T> implements Tags.IOptionalNamedTag<T> {

	private final ResourceLocation name;

	public EmptyNamedTag(ResourceLocation name) {
		this.name = name;
	}

	@Override
	public boolean contains(T element) {
		return false;
	}

	@Override
	public List<T> getValues() {
		return Collections.emptyList();
	}

	@Override
	public ResourceLocation getName() {
		return name;
	}

	public boolean isDefaulted() {
		return false;
	}
}
