package com.nbrevu.capitulator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;

import com.nbrevu.capitulator.data.AppConfig;
import com.nbrevu.capitulator.data.UserDefinedChapter;
import com.nbrevu.capitulator.ui.ProgressDialog;

public class ExternalExecutor	{
	private final static String[] YTDL_DUMP_JSON=new String[] {"--dump-json"};
	private final static String[] YTDL_MAIN_INVOCATION=new String[] {"--format","mp4","--output"};
	private final static String[] FFMPEG_NO_VIDEO_ARGS=new String[] {"-vn","-acodec","-mp3","-c:a","libmp3lame","-write_id3v2","1"};
	private final static String[] FFMPEG_KEEP_VIDEO_ARGS=new String[] {"-c","copy","-write_id3v2","1"};
	private final static String[] FFMPEG_SILENCE_DETECTION=new String[] {"-af","silencedetect=d=60","-f","null","-"};
	private final static String MD="-metadata";
	
	private final AppConfig appConfig;
	
	private CommandLine getDumpJsonCommand(String youtubeUrl)	{
		CommandLine cmd=new CommandLine(appConfig.youtubeDlExe.toFile());
		cmd.addArguments(YTDL_DUMP_JSON);
		cmd.addArgument(youtubeUrl);
		return cmd;
	}
	
	private CommandLine getYoutubeDlMainInvocation(Path outputFile,String youtubeUrl)	{
		CommandLine cmd=new CommandLine(appConfig.youtubeDlExe.toFile());
		cmd.addArguments(YTDL_MAIN_INVOCATION);
		cmd.addArgument(outputFile.toAbsolutePath().toString());
		cmd.addArgument(youtubeUrl);
		return cmd;
	}
	
	private CommandLine getFfmpegCutCommand(Path inputFile,UserDefinedChapter chapterData,boolean keepVideo,String albumTag)	{
		CommandLine cmd=new CommandLine(appConfig.ffmpegExe.toFile());
		cmd.addArgument("-i");
		cmd.addArgument(inputFile.toAbsolutePath().toString());
		cmd.addArgument("-ss");
		cmd.addArgument(Double.toString(chapterData.startTime));
		cmd.addArgument("-to");
		cmd.addArgument(Double.toString(chapterData.endTime));
		if (keepVideo) cmd.addArguments(FFMPEG_KEEP_VIDEO_ARGS);
		else cmd.addArguments(FFMPEG_NO_VIDEO_ARGS);
		// Info about the metadata tags: https://wiki.multimedia.cx/index.php/FFmpeg_Metadata#MP3 .
		cmd.addArgument(MD);
		cmd.addArgument("TIT2="+chapterData.track);
		cmd.addArgument(MD);
		cmd.addArgument("TPE1="+chapterData.artist);
		cmd.addArgument(MD);
		cmd.addArgument("TALB="+albumTag);
		cmd.addArgument(MD);
		cmd.addArgument("TRCK="+chapterData.trackNumber);
		cmd.addArgument("-y");
		cmd.addArgument(chapterData.file.toAbsolutePath().toString());
		return cmd;
	}
	
	private CommandLine getFfmpegSilenceDetectionCommand(Path mediaFile) {
		CommandLine cmd=new CommandLine(appConfig.ffmpegExe.toFile());
		cmd.addArgument("-i");
		cmd.addArgument(mediaFile.toAbsolutePath().toString());
		cmd.addArguments(FFMPEG_SILENCE_DETECTION);
		return cmd;
	}
	
	private static void runCommandShowingDialog(CommandLine command,JFrame parent,String additionalText,boolean useErrorStream) throws IOException	{
		ProgressDialog dialog=new ProgressDialog(parent,additionalText);
		dialog.doShow();
		OutputStream stream=new OutputStream()	{
			private StringBuilder currentString=new StringBuilder();
			@Override
			public void write(int b)	{
				if (b==13)	{
					dialog.updateText(currentString.toString());
					currentString=new StringBuilder();
				}	else if (b!='\r') currentString.append((char)b);
			}
		};
		runCommandCapturingOutput(command,stream,useErrorStream);
		dialog.setVisible(false);
		dialog.dispose();
	}
	
	private static void runCommandCapturingOutput(CommandLine command,OutputStream filePrinter,boolean useErrorStream) throws IOException	{
		DefaultExecutor executor=new DefaultExecutor();
		ExecuteStreamHandler streamHandler=useErrorStream?new PumpStreamHandler(System.out,filePrinter):new PumpStreamHandler(filePrinter,System.err);
		executor.setStreamHandler(streamHandler);
		executor.execute(command);
	}
	
	public ExternalExecutor(AppConfig appConfig)	{
		this.appConfig=appConfig;
	}
	
	public Path createTmpFile(String extension) throws IOException	{
		appConfig.tmpFolder.toFile().mkdirs();
		return Files.createTempFile(appConfig.tmpFolder,"capi_",extension);
	}

	public Path dumpJsonFile(String youtubeUrl) throws IOException	{
		Path tmpFile=createTmpFile(".json");
		try (OutputStream filePrinter=Files.newOutputStream(tmpFile))	{
			runCommandCapturingOutput(getDumpJsonCommand(youtubeUrl),filePrinter,false);
		}
		return tmpFile;
	}
	
	public Path downloadVideo(String youtubeUrl,JFrame parent) throws IOException	{
		Path tmpFile=createTmpFile(".mp4");
		Files.delete(tmpFile);	// Unintuitive, but necessary.
		CommandLine command=getYoutubeDlMainInvocation(tmpFile,youtubeUrl);
		runCommandShowingDialog(command,parent,"download finishes",false);
		return tmpFile;
	}
	
	public void cutFile(Path inputFile,UserDefinedChapter chapterData,boolean keepVideo,String albumTag,JFrame parent) throws IOException	{
		CommandLine command=getFfmpegCutCommand(inputFile,chapterData,keepVideo,albumTag);
		runCommandShowingDialog(command,parent,"file "+chapterData.file.getFileName().toString()+" is being cut",true);
	}
	
	public boolean containsSilences(Path mediaFile) throws IOException	{
		Path tmpFile=createTmpFile(".txt");
		try (OutputStream filePrinter=Files.newOutputStream(tmpFile))	{
			runCommandCapturingOutput(getFfmpegSilenceDetectionCommand(mediaFile),filePrinter,true);
		}
		boolean isSilence=false;
		for (String line:Files.readAllLines(tmpFile)) if (line.contains("silence_duration"))	{
			isSilence=true;
			break;
		}
		Files.delete(tmpFile);
		return isSilence;
	}
}
