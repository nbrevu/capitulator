package com.nbrevu.capitulator.data;

import java.util.List;

public class VideoData {
	public final String title;
	public final double duration;
	public final List<RawChapterDefinition> chapters;
	
	public VideoData(String title,double duration,List<RawChapterDefinition> chapters)	{
		this.title=title;
		this.duration=duration;
		this.chapters=chapters;
	}
}
