package com.nbrevu.capitulator;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import com.nbrevu.capitulator.data.AppConfig;
import com.nbrevu.capitulator.ui.SetupBox;

public class SetupApp {
	public static void main(String[] args)	{
		AppConfig config;
		URL iconResource=CapitulatorApp.class.getClassLoader().getResource("Anote.jpg");
		ImageIcon icon=new ImageIcon(iconResource);
		try	{
			config=IoFunctions.readConfig();
		}	catch (IOException exc)	{
			config=new AppConfig();
		}
		try	{
			UIManager.setLookAndFeel(config.preferredUiClass);
		}	catch (ClassNotFoundException|IllegalAccessException|InstantiationException|UnsupportedLookAndFeelException exc)	{}
		LookAndFeelInfo[] availableLookAndFeels=UIManager.getInstalledLookAndFeels();
		String[] availableLookAndFeelClassNames=Arrays.stream(availableLookAndFeels).map(LookAndFeelInfo::getClassName).toArray(String[]::new);
		SetupBox ui=new SetupBox(config,icon,availableLookAndFeelClassNames);
		ui.setVisible(true);
	}
}
