package com.nbrevu.capitulator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;

import com.nbrevu.capitulator.deserialiseddata.AppConfig;

public class ExternalExecutor	{
	private final static String[] YTDL_DUMP_JSON=new String[] {"--dump-json"};
	private final static String[] YTDL_MAIN_INVOCATION=new String[] {"--format","mp4","--output"};
	private final static String[] FFMPEG_NO_VIDEO_ARGS=new String[] {"-vn","-acodec","-mp3","-c:a","libmp3lame"};
	private final static String[] FFMPEG_KEEP_VIDEO_ARGS=new String[] {"-c","copy"};
	
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
	
	private CommandLine getFfmpegCutCommand(Path inputFile,double startTime,double endTime,Path outputFile,boolean keepVideo)	{
		CommandLine cmd=new CommandLine(appConfig.ffmpegExe.toFile());
		cmd.addArgument("-i");
		cmd.addArgument(inputFile.toAbsolutePath().toString());
		cmd.addArgument("-ss");
		cmd.addArgument(Double.toString(startTime));
		cmd.addArgument("-to");
		cmd.addArgument(Double.toString(endTime));
		if (keepVideo) cmd.addArguments(FFMPEG_KEEP_VIDEO_ARGS);
		else cmd.addArguments(FFMPEG_NO_VIDEO_ARGS);
		cmd.addArgument(outputFile.toString());
		return cmd;
	}
	
	private static int runCommandCapturingOutput(CommandLine command,OutputStream filePrinter) throws IOException	{
		DefaultExecutor executor=new DefaultExecutor();
		ExecuteStreamHandler streamHandler=new PumpStreamHandler(filePrinter,System.err);
		executor.setStreamHandler(streamHandler);
		return executor.execute(command);
	}
	
	private static int runCommand(Path mainCommand,Path outputFile,String[] fixedArgs,String... additionalArgs) throws IOException	{
		CommandLine cmd=new CommandLine(mainCommand.toFile());
		cmd.addArguments(fixedArgs);
		cmd.addArguments(additionalArgs);
		DefaultExecutor executor=new DefaultExecutor();
		try (OutputStream filePrinter=Files.newOutputStream(outputFile))	{
			ExecuteStreamHandler streamHandler=new PumpStreamHandler(filePrinter,System.err);
			executor.setStreamHandler(streamHandler);
			return executor.execute(cmd);
		}
	}

	public ExternalExecutor(AppConfig appConfig)	{
		this.appConfig=appConfig;
	}
	
	public Path createTmpFile() throws IOException	{
		appConfig.tmpFolder.toFile().mkdirs();
		return Files.createTempFile(appConfig.tmpFolder,"capi_","_yt");
	}

	public Path dumpJsonFile(String youtubeUrl) throws IOException	{
		Path tmpFile=createTmpFile();
		try (OutputStream filePrinter=Files.newOutputStream(tmpFile))	{
			runCommandCapturingOutput(getDumpJsonCommand(youtubeUrl),filePrinter);
		}
		return tmpFile;
	}
	
	public Path downloadVideo(String youtubeUrl) throws IOException	{
		Path tmpFile=createTmpFile();
		Path logFile=createTmpFile();
		runCommand(appConfig.youtubeDlExe,logFile,YTDL_MAIN_INVOCATION,tmpFile.toAbsolutePath().toString());
		return tmpFile;
	}
}
