package com.nbrevu.capitulator;

import java.nio.file.Path;
import java.util.List;

public class OperationList {
	public static class ChapterFile	{
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
	public final List<ChapterFile> chapters;
	public final boolean keepVideo;
	public final boolean removeSilent;
	public final String albumTag;
	public OperationList(List<ChapterFile> chapters,boolean keepVideo,boolean removeSilent,String albumTag)	{
		this.chapters=chapters;
		this.keepVideo=keepVideo;
		this.removeSilent=removeSilent;
		this.albumTag=albumTag;
	}
}
