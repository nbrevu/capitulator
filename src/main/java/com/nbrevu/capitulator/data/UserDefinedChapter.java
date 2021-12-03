package com.nbrevu.capitulator.data;

import java.nio.file.Path;

public class UserDefinedChapter	{
	public final int trackNumber;
	public final Path file;
	public final double startTime;
	public final double endTime;
	public final String artist;
	public final String track;
	public UserDefinedChapter(int trackNumber,Path file,double startTime,double endTime,String artist,String track)	{
		this.trackNumber=trackNumber;
		this.file=file;
		this.startTime=startTime;
		this.endTime=endTime;
		this.artist=artist;
		this.track=track;
	}
}