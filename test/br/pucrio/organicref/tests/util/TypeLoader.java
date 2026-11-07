package br.pucrio.organicref.tests.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.pucrio.organicref.model.entities.Type;
import br.pucrio.organicref.model.builder.TypesBuilder;
import br.pucrio.organicref.util.resources.JavaFilesFinder;
import br.pucrio.organicref.util.resources.SourceFilesLoader;

public class TypeLoader {

	public static Type loadOne(File file) throws IOException {
		Type type = loadAll(file).get(0);
		return type;
	}
	
	public static Type loadOneDummyClass(String dummyClass) throws IOException {
		Type type = loadAllDummyClass(dummyClass).get(0);
		return type;
	}
	
	public static List<Type> loadAll(File file) throws IOException {
		JavaFilesFinder finder = new JavaFilesFinder(new File("test").getAbsolutePath());
		SourceFilesLoader loader = new SourceFilesLoader(finder, file);
		TypesBuilder source = loader.getTypeBuilders().get(0);
		return source.getTypes();
	}
	
	public static List<Type> loadAllFromDir(File sourcePath) throws IOException {
		JavaFilesFinder finder = new JavaFilesFinder(sourcePath.getAbsolutePath());
		SourceFilesLoader loader = new SourceFilesLoader(finder);
		List<Type> types = new ArrayList<>();
		for (TypesBuilder source : loader.getTypeBuilders()) {
			types.addAll(source.getTypes());
		}
		return types;
	}
	
	public static List<Type> loadAllDummyClass(String dummyClassPath) throws IOException {
		String path = "test/br/pucrio/opus/smells/tests/dummy/";
		return loadAll(new File(path + dummyClassPath));
	}
}
