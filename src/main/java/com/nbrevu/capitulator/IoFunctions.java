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
import com.nbrevu.capitulator.deserialiseddata.VideoData;

public final class IoFunctions {
	public final static String CONFIG_FILE="config.properties";
	
	@SuppressWarnings("unchecked")
	public static VideoData readJsonVideoData(Path file) throws IOException	{
		/*
		 * HERE BE DRAGONS. There are much better ways to do this (with actual type safety). This should be improved.
		 */
		Map<String,Object> jsonData=new Gson().fromJson(Files.newBufferedReader(file),Map.class);
		String title=(String)jsonData.get("title");
		if (title==null) title="";
		double duration=((Double)jsonData.get("duration")).doubleValue();
		List<Map<String,Object>> chapterMap=(List<Map<String,Object>>)jsonData.get("chapters");
		if (chapterMap==null) return new VideoData(title,duration,List.of());
		List<Chapter> chapters=new ArrayList<>(chapterMap.size());
		for (Map<String,Object> chapter:chapterMap)	{
			String chapterName=(String)chapter.get("title");
			double startTime=((Double)chapter.get("start_time")).doubleValue();
			double endTime=((Double)chapter.get("end_time")).doubleValue();
			chapters.add(new Chapter(chapterName,startTime,endTime));
		}
		return new VideoData(title,duration,chapters);
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
