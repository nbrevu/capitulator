package com.nbrevu.capitulator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.exec.ExecuteException;

import com.nbrevu.capitulator.deserialiseddata.AppConfig;
import com.nbrevu.capitulator.deserialiseddata.Chapter;
import com.nbrevu.capitulator.ui.StandardBox;

public class CapitulatorApp {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, UnsupportedLookAndFeelException	{
		try	{
			AppConfig config=IoFunctions.readConfig();
			try	{
				UIManager.setLookAndFeel(config.preferredUiClass);
			}	catch (ClassNotFoundException|IllegalAccessException|InstantiationException|UnsupportedLookAndFeelException exc)	{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			if (args.length<1)	{
				JOptionPane.showMessageDialog(null,"Please include a youtube URL as command line argument.","Argument missing",JOptionPane.ERROR_MESSAGE);
				return;
			}
			String youtubeUrl=args[0];
			ExternalExecutor executor=new ExternalExecutor(config);
			Path file=executor.dumpJsonFile(youtubeUrl);
			List<Chapter> chapters=IoFunctions.readChapters(file);
			// ZUTUN! TODO! TEHDÄ!!!!! Manage the case where chapters can't be found. Use another JFrame.
			JFrame mainApp=new StandardBox(chapters,config);
			mainApp.setVisible(true);
		}	catch (ExecuteException exc)	{
			JOptionPane.showMessageDialog(null,"Error running youtube-dl. Please make sure that the URL is valid.","Can't run youtube-dl",JOptionPane.ERROR_MESSAGE);
		}	catch (IOException exc)	{
			JOptionPane.showMessageDialog(null,"Error during an I/O operation. Please check that the application configuration file is correct.","I/O Error",JOptionPane.ERROR_MESSAGE);
		}
	}
}
