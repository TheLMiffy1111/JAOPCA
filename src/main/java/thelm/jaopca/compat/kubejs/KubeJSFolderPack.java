package thelm.jaopca.compat.kubejs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Joiner;

import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackFileNotFoundException;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;

public class KubeJSFolderPack implements IResourcePack {

	private static String getFullPath(ResourcePackType type, ResourceLocation location) {
		return String.format("%s/%s/%s", type.getDirectoryName(), location.getNamespace(), location.getPath());
	}

	@Override
	public InputStream getRootResourceStream(String fileName) throws IOException {
		throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), fileName);
	}

	@Override
	public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
		String resourcePath = getFullPath(type, location);
		Path file = KubeJSPaths.DIRECTORY.resolve(resourcePath);
		if(Files.exists(file)) {
			return Files.newInputStream(file);
		}
		throw new ResourcePackFileNotFoundException(KubeJSPaths.DIRECTORY.toFile(), resourcePath);
	}

	@Override
	public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
		return Files.exists(KubeJSPaths.DIRECTORY.resolve(getFullPath(type, location)));
	}

	@Override
	public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespace, String path, int maxDepth, Predicate<String> filter) {
		List<ResourceLocation> list = new ArrayList<>();
		UtilsJS.tryIO(() -> {
			Path root = KubeJSPaths.get(type).toAbsolutePath();
			if(Files.exists(root) && Files.isDirectory(root)) {
				Path inputPath = root.getFileSystem().getPath(path);
				Files.walk(root).
				map(p->root.relativize(p.toAbsolutePath())).
				filter(p->p.getNameCount() > 1 && p.getNameCount() - 1 <= maxDepth).
				filter(p->!p.toString().endsWith(".mcmeta")).
				filter(p->p.subpath(1, p.getNameCount()).startsWith(inputPath)).
				filter(p->filter.test(p.getFileName().toString())).
				map(p->new ResourceLocation(p.getName(0).toString(), Joiner.on('/').join(p.subpath(1, Math.min(maxDepth, p.getNameCount()))))).
				forEach(list::add);
			} 
		});
		return list;
	}

	@Override
	public Set<String> getResourceNamespaces(ResourcePackType type) {
		HashSet<String> namespaces = new HashSet<>();
		UtilsJS.tryIO(() -> {
			Path root = KubeJSPaths.get(type).toAbsolutePath();
			if(Files.exists(root) && Files.isDirectory(root)) {
				Files.walk(root, 1).
				map(path->root.relativize(path.toAbsolutePath())).
				filter(path->path.getNameCount() > 0).
				map(p->p.toString().replaceAll("/$", "")).
				filter(s->!s.isEmpty()).
				forEach(namespaces::add);
			} 
		});
		return namespaces;
	}

	@Override
	public <T> T getMetadata(IMetadataSectionSerializer<T> serializer) {
		return null;
	}

	@Override
	public String getName() {
		return "JAOPCA KubeJS Folder Resource Pack";
	}

	@Override
	public void close() {}
}
