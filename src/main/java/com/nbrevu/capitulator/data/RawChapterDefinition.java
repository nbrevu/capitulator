package com.nbrevu.capitulator.data;

public class RawChapterDefinition {
	public final String title;
	public final double startMark;
	public final double endMark;
	public RawChapterDefinition(String title,double startMark,double endMark)	{
		this.title=title;
		this.startMark=startMark;
		this.endMark=endMark;
	}
}
