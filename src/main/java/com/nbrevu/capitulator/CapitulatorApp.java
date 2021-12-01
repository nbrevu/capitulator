package com.nbrevu.capitulator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.nbrevu.capitulator.deserialiseddata.AppConfig;
import com.nbrevu.capitulator.deserialiseddata.Chapter;

public class CapitulatorApp {
	public static void main(String[] args) throws IOException	{
		AppConfig config=IoFunctions.readConfig();
		//Path file=Paths.get("D:\\Archivos de Programa\\Youtube-dl\\oPXonS-Q_bA.info.json");
		Path file=Paths.get("D:\\Archivos de Programa\\Youtube-dl\\PDHf_ETjRE4.info.json");
		List<Chapter> chapters=IoFunctions.readChapters(file);
		System.out.println(chapters.size());
	}
}
