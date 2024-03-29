package com.nbrevu.capitulator.data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AppConfig {
	public final static String YOUTUBE_DL_EXE="youtubeDlExecutablePath";
	public final static String FFMPEG_EXE="ffmpegExecutablePath";
	public final static String TMP_FOLDER="tmpFolder";
	public final static String DEFAULT_SAVE_FOLDER="defaultSaveFolder";
	public final static String PREFERRED_UI_CLASS="preferredUiClass";
	
	private final static String[] ALL_PROPERTIES=new String[] {YOUTUBE_DL_EXE,FFMPEG_EXE,TMP_FOLDER,DEFAULT_SAVE_FOLDER,PREFERRED_UI_CLASS};
	
	public final Path youtubeDlExe;
	public final Path ffmpegExe;
	public final Path tmpFolder;
	public final Path saveFolder;
	public final String preferredUiClass;
	
	public AppConfig()	{
		Path emptyPath=Paths.get("");
		youtubeDlExe=emptyPath;
		ffmpegExe=emptyPath;
		tmpFolder=emptyPath;
		saveFolder=emptyPath;
		preferredUiClass="";
	}
	
	public AppConfig(Properties props)	{
		for (String prop:ALL_PROPERTIES) if (!props.containsKey(prop)) throw new IllegalArgumentException("Property not found: "+prop+".");
		youtubeDlExe=Paths.get(props.getProperty(YOUTUBE_DL_EXE));
		ffmpegExe=Paths.get(props.getProperty(FFMPEG_EXE));
		tmpFolder=Paths.get(props.getProperty(TMP_FOLDER));
		saveFolder=Paths.get(props.getProperty(DEFAULT_SAVE_FOLDER));
		preferredUiClass=props.getProperty(PREFERRED_UI_CLASS);
	}
}
