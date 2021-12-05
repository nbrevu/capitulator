package com.nbrevu.capitulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.gson.Gson;
import com.nbrevu.capitulator.data.AppConfig;
import com.nbrevu.capitulator.data.RawChapterDefinition;
import com.nbrevu.capitulator.data.VideoData;

public final class IoFunctions {
	public final static Path CONFIG_FILE=Paths.get("config.properties");
	private final static Charset CHAR_SET=Charset.forName(System.getProperty("file.encoding"));
	
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
		List<RawChapterDefinition> chapters=new ArrayList<>(chapterMap.size());
		for (Map<String,Object> chapter:chapterMap)	{
			String chapterName=(String)chapter.get("title");
			double startTime=((Double)chapter.get("start_time")).doubleValue();
			double endTime=((Double)chapter.get("end_time")).doubleValue();
			chapters.add(new RawChapterDefinition(chapterName,startTime,endTime));
		}
		return new VideoData(title,duration,chapters);
	}
	
	public static AppConfig readConfig() throws IOException	{
		Properties properties=new Properties();
		try (BufferedReader reader=Files.newBufferedReader(CONFIG_FILE,CHAR_SET))	{
			properties.load(reader);
		}
		return new AppConfig(properties);
	}
	
	public static void writeConfig(Properties properties) throws IOException	{
		try (BufferedWriter writer=Files.newBufferedWriter(CONFIG_FILE,CHAR_SET))	{
			properties.store(writer,null);
		}
	}
}
