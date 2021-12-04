package com.nbrevu.capitulator.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import com.nbrevu.capitulator.IoFunctions;
import com.nbrevu.capitulator.data.AppConfig;

public class SetupBox extends JFrame {
	private static final long serialVersionUID = -7703152622518352820L;
	
	private void addFileListener(JTextField field,JButton button,String expectedFileName)	{
		SetupBox me=this;
		button.addActionListener((ActionEvent e)->	{
			JFileChooser fileChooser=new JFileChooser(field.getText());
			fileChooser.setDialogTitle("Choose the text file where the chapters are stored");
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setFileFilter(new FileFilter()	{
				@Override
				public boolean accept(File f) {
					return f.toPath().getFileName().toString().equals(expectedFileName);
				}
				@Override
				public String getDescription() {
					return expectedFileName;
				}
			});
			if (fileChooser.showOpenDialog(me)==JFileChooser.APPROVE_OPTION) field.setText(fileChooser.getSelectedFile().toString());
		});
	}
	
	private void addDirListener(JTextField field,JButton button)	{
		SetupBox me=this;
		button.addActionListener((ActionEvent e)->	{
			JFileChooser dirChooser=new JFileChooser(field.getText());
			dirChooser.setDialogTitle("Choose a path to save the resulting files");
			dirChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (dirChooser.showSaveDialog(me)==JFileChooser.APPROVE_OPTION) field.setText(dirChooser.getSelectedFile().toString());
		});
	}
	
	private SetupBox(String youtubeDlValue,String ffmpegValue,String tmpDirValue,String saveDirValue,String lookAndFeelValue,ImageIcon icon,String[] availableLookAndFeels)	{
		super("Setting up");
		SetupBox me=this;
		Box mainBox=Box.createVerticalBox();
		mainBox.add(Box.createVerticalStrut(5));
		Box youtubeDlBox=Box.createHorizontalBox();
		youtubeDlBox.add(Box.createHorizontalStrut(5));
		youtubeDlBox.add(new JLabel("Location of youtube-dl.exe:"));
		youtubeDlBox.add(Box.createHorizontalStrut(5));
		JTextField youtubeDl=new JTextField(youtubeDlValue);
		youtubeDlBox.add(youtubeDl);
		JButton youtubeDlButton=new JButton("Examine...");
		youtubeDlBox.add(youtubeDlButton);
		youtubeDlBox.add(Box.createHorizontalStrut(5));
		mainBox.add(youtubeDlBox);
		mainBox.add(Box.createVerticalStrut(5));
		addFileListener(youtubeDl,youtubeDlButton,"youtube-dl.exe");
		Box ffmpegBox=Box.createHorizontalBox();
		ffmpegBox.add(Box.createHorizontalStrut(5));
		ffmpegBox.add(new JLabel("Location of ffmpeg.exe:"));
		ffmpegBox.add(Box.createHorizontalStrut(5));
		JTextField ffmpeg=new JTextField(ffmpegValue);
		ffmpegBox.add(ffmpeg);
		JButton ffmpegButton=new JButton("Examine...");
		ffmpegBox.add(ffmpegButton);
		ffmpegBox.add(Box.createHorizontalStrut(5));
		mainBox.add(ffmpegBox);
		mainBox.add(Box.createVerticalStrut(5));
		addFileListener(ffmpeg,ffmpegButton,"ffmpeg.exe");
		Box tmpDirBox=Box.createHorizontalBox();
		tmpDirBox.add(Box.createHorizontalStrut(5));
		tmpDirBox.add(new JLabel("Location of the temporary folder:"));
		tmpDirBox.add(Box.createHorizontalStrut(5));
		JTextField tmpDir=new JTextField(tmpDirValue);
		tmpDirBox.add(tmpDir);
		JButton tmpDirButton=new JButton("Examine...");
		tmpDirBox.add(tmpDirButton);
		tmpDirBox.add(Box.createHorizontalStrut(5));
		mainBox.add(tmpDirBox);
		mainBox.add(Box.createVerticalStrut(5));
		addDirListener(tmpDir,tmpDirButton);
		Box saveDirBox=Box.createHorizontalBox();
		saveDirBox.add(Box.createHorizontalStrut(5));
		saveDirBox.add(new JLabel("Location of the default save folder:"));
		saveDirBox.add(Box.createHorizontalStrut(5));
		JTextField saveDir=new JTextField(saveDirValue);
		saveDirBox.add(saveDir);
		JButton saveDirButton=new JButton("Examine...");
		saveDirBox.add(saveDirButton);
		saveDirBox.add(Box.createHorizontalStrut(5));
		mainBox.add(saveDirBox);
		mainBox.add(Box.createVerticalStrut(5));
		addDirListener(saveDir,saveDirButton);
		Box styleBox=Box.createHorizontalBox();
		styleBox.add(Box.createHorizontalStrut(5));
		styleBox.add(new JLabel("Window style: "));
		JComboBox<String> combo=new JComboBox<>(availableLookAndFeels);
		combo.setEditable(false);
		combo.setSelectedItem(lookAndFeelValue);
		styleBox.add(combo);
		JButton tryCombo=new JButton("Try style");
		tryCombo.setEnabled(false);
		styleBox.add(tryCombo);
		styleBox.add(Box.createHorizontalStrut(5));
		combo.addActionListener((ActionEvent e)->	{
			String newSelection=availableLookAndFeels[combo.getSelectedIndex()];
			tryCombo.setEnabled(!newSelection.equals(lookAndFeelValue));
		});
		tryCombo.addActionListener((ActionEvent e)->	{
			String newSelection=availableLookAndFeels[combo.getSelectedIndex()];
			try	{
				setVisible(false);
				UIManager.setLookAndFeel(newSelection);
				SetupBox child=new SetupBox(youtubeDl.getText(),ffmpeg.getText(),tmpDir.getText(),saveDir.getText(),availableLookAndFeels[combo.getSelectedIndex()],icon,availableLookAndFeels);
				dispose();
				child.setVisible(true);
			}	catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException exc)	{
				setVisible(true);
				JOptionPane.showMessageDialog(me,"An error happened while trying to change the look and feel: "+exc,"UI Error!",JOptionPane.ERROR_MESSAGE);
				return;
			}
		});
		mainBox.add(styleBox);
		mainBox.add(Box.createVerticalStrut(5));
		Box buttonBox=Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		JButton mainButton=new JButton("Save");
		buttonBox.add(mainButton);
		buttonBox.add(Box.createHorizontalGlue());
		mainBox.add(buttonBox);
		mainBox.add(Box.createVerticalStrut(5));
		mainButton.addActionListener((ActionEvent e)->	{
			try	{
				Path youtubeDlPath=Paths.get(youtubeDl.getText());
				Path ffmpegPath=Paths.get(ffmpeg.getText());
				Path tmpDirPath=Paths.get(tmpDir.getText());
				Path saveDirPath=Paths.get(saveDir.getText());
				String uiClass=availableLookAndFeels[combo.getSelectedIndex()];
				if (!youtubeDlPath.toFile().exists()) JOptionPane.showMessageDialog(me,"The path specified for youtube-dl doesn't exist.","Path error",JOptionPane.ERROR_MESSAGE);
				if (!ffmpegPath.toFile().exists()) JOptionPane.showMessageDialog(me,"The path specified for ffmpeg doesn't exist.","Path error",JOptionPane.ERROR_MESSAGE);
				Properties properties=new Properties();
				properties.setProperty(AppConfig.YOUTUBE_DL_EXE,youtubeDlPath.toAbsolutePath().toString());
				properties.setProperty(AppConfig.FFMPEG_EXE,ffmpegPath.toAbsolutePath().toString());
				properties.setProperty(AppConfig.TMP_FOLDER,tmpDirPath.toAbsolutePath().toString());
				properties.setProperty(AppConfig.DEFAULT_SAVE_FOLDER,saveDirPath.toAbsolutePath().toString());
				properties.setProperty(AppConfig.PREFERRED_UI_CLASS,uiClass);
				IoFunctions.writeConfig(properties);
			}	catch (RuntimeException exc)	{
				JOptionPane.showMessageDialog(me,"Please check that the paths are valid: "+exc,"Path error",JOptionPane.ERROR_MESSAGE);
			}	catch (IOException|URISyntaxException exc)	{
				JOptionPane.showMessageDialog(me,"Error writing the configuration file: "+exc,"I/O error",JOptionPane.ERROR_MESSAGE);
			}
		});
		add(mainBox);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public SetupBox(AppConfig config,ImageIcon icon,String[] availableLookAndFeels)	{
		this(config.youtubeDlExe.toAbsolutePath().toString(),config.ffmpegExe.toAbsolutePath().toString(),config.tmpFolder.toAbsolutePath().toString(),config.saveFolder.toAbsolutePath().toString(),config.preferredUiClass,icon,availableLookAndFeels);
	}
}
