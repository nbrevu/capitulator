package com.nbrevu.capitulator;

import java.io.OutputStream;
import java.util.function.Consumer;

public class CustomOutputStream extends OutputStream	{
	private final Consumer<String> writer;
	private StringBuilder currentString;
	
	public CustomOutputStream(Consumer<String> writer)	{
		this.writer=writer;
	}

	@Override
	public void write(int b)	{
		if (b==13)	{
			writer.accept(currentString.toString());
			currentString=new StringBuilder();
		}	else if (b!='\r') currentString.append((char)b);
	}	
}
