package com.nbrevu.capitulator.deserialiseddata;

import java.util.List;

public class VideoData {
	public final String title;
	public final double duration;
	public final List<Chapter> chapters;
	
	public VideoData(String title,double duration,List<Chapter> chapters)	{
		this.title=title;
		this.duration=duration;
		this.chapters=chapters;
	}
}
