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
	
	private final AppConfig appConfig;
	
	private static int runCommand(Path mainCommand,String[] args,String mutableArg,Path outputFile) throws IOException	{
		CommandLine cmd=new CommandLine(mainCommand.toFile());
		cmd.addArguments(args);
		cmd.addArgument(mutableArg);
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
		runCommand(appConfig.youtubeDlExe,YTDL_DUMP_JSON,youtubeUrl,tmpFile);
		return tmpFile;
	}
}
