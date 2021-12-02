package com.nbrevu.capitulator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.exec.ExecuteException;

import com.nbrevu.capitulator.deserialiseddata.AppConfig;
import com.nbrevu.capitulator.deserialiseddata.Chapter;

public class CapitulatorApp {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, UnsupportedLookAndFeelException	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		if (args.length<1)	{
			JOptionPane.showMessageDialog(null,"Please include a youtube URL as command line argument.","Argument missing",JOptionPane.ERROR_MESSAGE);
			return;
		}
		String youtubeUrl=args[0];
		try	{
			AppConfig config=IoFunctions.readConfig();
			ExternalExecutor executor=new ExternalExecutor(config);
			Path file=executor.dumpJsonFile(youtubeUrl);	// Might throw ExecuteException.
			List<Chapter> chapters=IoFunctions.readChapters(file);
			System.out.println(chapters.size());
		}	catch (ExecuteException exc)	{
			JOptionPane.showMessageDialog(null,"Error running youtube-dl. Please make sure that the URL is valid.","Can't run youtube-dl",JOptionPane.ERROR_MESSAGE);
		}	catch (IOException exc)	{
			JOptionPane.showMessageDialog(null,"Error during an I/O operation. Please check that the application configuration file is correct.","I/O Error",JOptionPane.ERROR_MESSAGE);
		}
	}
}
