package com.nbrevu.capitulator.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum ChapterTextFormat {
	// Formats used in Sovietwave videos.
	CHAPTER_FIRST_INCOMPLETE("^(.+) \\((\\d{2}):(\\d{2})\\)$")	{
		@Override
		protected IncompleteChapter extractData(Matcher matcher)	{
			String chapterName=matcher.group(1);
			double minutes=Double.parseDouble(matcher.group(2));
			double seconds=Double.parseDouble(matcher.group(3));
			return new IncompleteChapter(chapterName,condenseTime(minutes,seconds));
		}
	},
	CHAPTER_FIRST_COMPLETE("^(.+) \\((\\d{2}):(\\d{2}):(\\d{2})\\)$")	{
		@Override
		protected IncompleteChapter extractData(Matcher matcher)	{
			String chapterName=matcher.group(1);
			double hours=Double.parseDouble(matcher.group(2));
			double minutes=Double.parseDouble(matcher.group(3));
			double seconds=Double.parseDouble(matcher.group(4));
			return new IncompleteChapter(chapterName,condenseTime(hours,minutes,seconds));
		}
	},
	// Formats used in Krelez videos.
	TIME_FIRST_PREFIXED_INCOMPLETE("^\\d+ (\\d{2}):(\\d{2}) \\- (.+)$")	{
		@Override
		protected IncompleteChapter extractData(Matcher matcher)	{
			double minutes=Double.parseDouble(matcher.group(1));
			double seconds=Double.parseDouble(matcher.group(2));
			String chapterName=matcher.group(3);
			return new IncompleteChapter(chapterName,condenseTime(minutes,seconds));
		}
	},
	TIME_FIRST_PREFIXED_COMPLETE("^\\d+ (\\d{2}):(\\d{2}):(\\d{2}) \\- (.+)$")	{
		@Override
		protected IncompleteChapter extractData(Matcher matcher)	{
			double hours=Double.parseDouble(matcher.group(1));
			double minutes=Double.parseDouble(matcher.group(2));
			double seconds=Double.parseDouble(matcher.group(3));
			String chapterName=matcher.group(4);
			return new IncompleteChapter(chapterName,condenseTime(hours,minutes,seconds));
		}
	},
	// Format used in some chiptune compilations
	TIME_FIRST_UNPREFIXED_COMPLETE("^(\\d{2}):(\\d{2}):(\\d{2}) : (.+)$")	{
		@Override
		protected IncompleteChapter extractData(Matcher matcher)	{
			double hours=Double.parseDouble(matcher.group(1));
			double minutes=Double.parseDouble(matcher.group(2));
			double seconds=Double.parseDouble(matcher.group(3));
			String chapterName=matcher.group(4);
			return new IncompleteChapter(chapterName,condenseTime(hours,minutes,seconds));
		}
	},
	;

	private static class IncompleteChapter	{
		public final String name;
		public final double startTime;
		public IncompleteChapter(String name,double startTime)	{
			this.name=name;
			this.startTime=startTime;
		}
	}
	
	public static class ParseResult	{
		public final List<RawChapterDefinition> chapters;
		public final List<String> unparsedStrings;
		public ParseResult(List<RawChapterDefinition> chapters,List<String> unparsedStrings)	{
			this.chapters=chapters;
			this.unparsedStrings=unparsedStrings;
		}
	}

	private final Pattern pattern;
	private ChapterTextFormat(String pattern)	{
		this.pattern=Pattern.compile(pattern);
	}
	
	private final static ChapterTextFormat[] PATTERNS=values();
	
	protected abstract IncompleteChapter extractData(Matcher matcher);
	
	private static double condenseTime(double minutes,double seconds)	{
		return condenseTime(0,minutes,seconds);
	}
	
	private static double condenseTime(double hours,double minutes,double seconds)	{
		return seconds+60*(minutes+60*hours);
	}
	
	private static IncompleteChapter tryParse(String line)	{
		for (ChapterTextFormat format:PATTERNS)	{
			Matcher m=format.pattern.matcher(line);
			if (m.matches()) return format.extractData(m);
		}
		return null;
	}
	
	public static List<String> getPatterns()	{
		return Arrays.stream(PATTERNS).map((ChapterTextFormat f)->f.pattern.pattern()).collect(Collectors.toList());
	}
	
	public static ParseResult parse(String data,double videoLength)	{
		List<IncompleteChapter> parsedChapters=new ArrayList<>();
		List<String> invalidStrings=new ArrayList<>();
		for (String line:data.split("\\R"))	{
			line=line.trim();
			if (line.isBlank()) continue;
			IncompleteChapter parsed=tryParse(line);
			if (parsed==null) invalidStrings.add(line);
			else parsedChapters.add(parsed);
		}
		List<RawChapterDefinition> chapters=new LinkedList<>();
		double lastBound=videoLength;
		for (int i=parsedChapters.size()-1;i>=0;--i)	{
			IncompleteChapter chapter=parsedChapters.get(i);
			chapters.add(0,new RawChapterDefinition(chapter.name,chapter.startTime,lastBound));
			lastBound=chapter.startTime;
		}
		return new ParseResult(chapters,invalidStrings);
	}
}
