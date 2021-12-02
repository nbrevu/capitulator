package com.nbrevu.capitulator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.nbrevu.capitulator.deserialiseddata.AppConfig;
import com.nbrevu.capitulator.deserialiseddata.Chapter;

public class CapitulatorApp {
	public static void main(String[] args) throws IOException	{
		if (args.length<1) throw new IllegalStateException("Nicht möglich.");
		AppConfig config=IoFunctions.readConfig();
		ExternalExecutor executor=new ExternalExecutor(config);
		Path file=executor.dumpJsonFile(args[0]);
		List<Chapter> chapters=IoFunctions.readChapters(file);
		System.out.println(chapters.size());
	}
}
