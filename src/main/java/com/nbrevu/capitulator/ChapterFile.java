package com.nbrevu.capitulator;

import java.nio.file.Path;

public class ChapterFile	{
	public final Path file;
	public final double startTime;
	public final double endTime;
	public final String artist;
	public final String track;
	public ChapterFile(Path file,double startTime,double endTime,String artist,String track)	{
		this.file=file;
		this.startTime=startTime;
		this.endTime=endTime;
		this.artist=artist;
		this.track=track;
	}
}