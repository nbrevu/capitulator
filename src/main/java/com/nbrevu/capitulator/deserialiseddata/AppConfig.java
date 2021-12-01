package com.nbrevu.capitulator.deserialiseddata;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AppConfig {
	private final static String YOUTUBE_DL_EXE="youtubeDlExecutablePath";
	private final static String FFMPEG_EXE="ffmpegExecutablePath";
	private final static String TMP_FOLDER="tmpFolder";
	private final static String DEFAULT_SAVE_FOLDER="defaultSaveFolder";
	
	private final static String[] ALL_PROPERTIES=new String[] {YOUTUBE_DL_EXE,FFMPEG_EXE,TMP_FOLDER,DEFAULT_SAVE_FOLDER};
	
	public final Path youtubeDlExe;
	public final Path ffmpegExe;
	public final Path tmpFolder;
	public final Path saveFolder;
	
	public AppConfig(Path youtubeDlExe,Path ffmpegExe,Path tmpFolder,Path saveFolder)	{
		this.youtubeDlExe=youtubeDlExe;
		this.ffmpegExe=ffmpegExe;
		this.tmpFolder=tmpFolder;
		this.saveFolder=saveFolder;
	}
	
	public AppConfig(Properties props)	{
		for (String prop:ALL_PROPERTIES) if (!props.containsKey(prop)) throw new IllegalArgumentException("Property not found: "+prop+".");
		youtubeDlExe=Paths.get(props.getProperty(YOUTUBE_DL_EXE));
		ffmpegExe=Paths.get(props.getProperty(FFMPEG_EXE));
		tmpFolder=Paths.get(props.getProperty(TMP_FOLDER));
		saveFolder=Paths.get(props.getProperty(DEFAULT_SAVE_FOLDER));
	}
}
