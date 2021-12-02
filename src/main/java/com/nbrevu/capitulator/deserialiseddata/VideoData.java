package com.nbrevu.capitulator.deserialiseddata;

import java.util.List;

public class VideoData {
	public final String title;
	public final List<Chapter> chapters;
	
	public VideoData(String title,List<Chapter> chapters)	{
		this.title=title;
		this.chapters=chapters;
	}
}
