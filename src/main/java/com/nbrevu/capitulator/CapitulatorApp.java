package com.nbrevu.capitulator;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.exec.ExecuteException;

import com.nbrevu.capitulator.data.AppConfig;
import com.nbrevu.capitulator.data.VideoData;
import com.nbrevu.capitulator.ui.AuxiliaryBox;
import com.nbrevu.capitulator.ui.StandardBox;

public class CapitulatorApp {
	public static void main(String[] args) throws ClassNotFoundException,InstantiationException,IllegalAccessException,IOException,UnsupportedLookAndFeelException	{
		try	{
			AppConfig config=IoFunctions.readConfig();
			URL iconResource=CapitulatorApp.class.getClassLoader().getResource("Anote.jpg");
			ImageIcon icon=new ImageIcon(iconResource);
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
			Path tmpJsonFile=executor.dumpJsonFile(youtubeUrl);
			VideoData data=IoFunctions.readJsonVideoData(tmpJsonFile);
			Files.delete(tmpJsonFile);
			if (data.chapters.isEmpty())	{
				JFrame auxiliaryApp=new AuxiliaryBox(youtubeUrl,data.title,data.duration,config,executor,icon);
				auxiliaryApp.setVisible(true);
			}	else	{
				JFrame mainApp=new StandardBox(youtubeUrl,data.title,data.chapters,config,executor,icon);
				mainApp.setVisible(true);
			}
		}	catch (ExecuteException exc)	{
			JOptionPane.showMessageDialog(null,"Error running the underlying applications. Please make sure that the URL is valid.","Can't run youtube-dl",JOptionPane.ERROR_MESSAGE);
		}	catch (IOException exc)	{
			JOptionPane.showMessageDialog(null,"Error during an I/O operation. Please check that the application configuration file is correct.","I/O Error",JOptionPane.ERROR_MESSAGE);
		}
	}
}
