package com.nbrevu.capitulator;

import java.io.IOException;

public class ExternalExecution	{
	private final static Runtime RUNTIME=Runtime.getRuntime();
	
	public static int runCommand(String[] command) throws InterruptedException,IOException	{
		Process proc=RUNTIME.exec(command);
		return proc.waitFor();
	}
}
