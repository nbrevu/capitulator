package com.nbrevu.capitulator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;
import com.nbrevu.capitulator.deserialiseddata.AppConfig;
import com.nbrevu.capitulator.deserialiseddata.Chapter;

public final class IoFunctions {
	public final static String CONFIG_FILE="config.properties";
	
	@SuppressWarnings("unchecked")
	public static List<Chapter> readChapters(Path file) throws IOException	{
		/*
		 * HERE BE DRAGONS. There are much better ways to do this (with actual type safety). This should be improved.
		 */
		Map<String,Object> cosa=new Gson().fromJson(Files.newBufferedReader(file),Map.class);
		List<Map<String,Object>> chapters=(List<Map<String,Object>>)cosa.get("chapters");
		if (chapters==null) return List.of();
		List<Chapter> result=new ArrayList<>(chapters.size());
		for (Map<String,Object> chapter:chapters)	{
			String title=(String)chapter.get("title");
			double startTime=((Double)chapter.get("start_time")).doubleValue();
			double endTime=((Double)chapter.get("end_time")).doubleValue();
			result.add(new Chapter(title,startTime,endTime));
		}
		return result;
	}
	
	public static AppConfig readConfig() throws IOException	{
		Properties properties=new Properties();
		ClassLoader loader=IoFunctions.class.getClassLoader();
		try (InputStream is=loader.getResourceAsStream(CONFIG_FILE))	{
			properties.load(is);
		}
		return new AppConfig(properties);
	}
}
