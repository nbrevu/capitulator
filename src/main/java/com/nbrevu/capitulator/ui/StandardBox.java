package com.nbrevu.capitulator.ui;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.nbrevu.capitulator.deserialiseddata.AppConfig;
import com.nbrevu.capitulator.deserialiseddata.Chapter;

public class StandardBox extends JFrame	{
	private static final long serialVersionUID = -7891317911426722027L;
	
	public StandardBox(List<Chapter> chapters,AppConfig config)	{
		super("Partition youtube videos by chapter");
		StandardBox me=this;
		Box mainPane=Box.createVerticalBox();
		mainPane.add(Box.createVerticalStrut(5));
		// Controls to choose a directory to save the result files.
		Box mainDirBox=Box.createHorizontalBox();
		mainDirBox.add(Box.createHorizontalStrut(5));
		JLabel mainDirLabel=new JLabel("Path to save the files: ");
		mainDirBox.add(mainDirLabel);
		mainDirBox.add(Box.createHorizontalStrut(5));
		JTextField defaultDir=new JTextField(config.saveFolder.toString());
		mainDirBox.add(defaultDir);
		JButton defaultDirExamine=new JButton("Examine...");
		mainDirBox.add(defaultDirExamine);
		mainDirBox.add(Box.createHorizontalStrut(5));
		defaultDirExamine.addActionListener((ActionEvent a)->	{
			JFileChooser dirChooser=new JFileChooser(defaultDir.getText());
			dirChooser.setDialogTitle("Choose a path to save the resulting files");
			dirChooser.setDialogType(JFileChooser.SAVE_DIALOG);
			dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result=dirChooser.showSaveDialog(me);
			if (result==JFileChooser.APPROVE_OPTION) defaultDir.setText(dirChooser.getSelectedFile().toString());
		});
		mainPane.add(mainDirBox);
		// Other flags.
		Box otherFlagsBox=Box.createHorizontalBox();
		otherFlagsBox.add(Box.createHorizontalStrut(5));
		JCheckBox keepVideo=new JCheckBox("Keep video",false);
		otherFlagsBox.add(keepVideo);
		otherFlagsBox.add(Box.createHorizontalGlue());
		JCheckBox deleteEmptyFiles=new JCheckBox("Delete empty audio files",true);
		otherFlagsBox.add(deleteEmptyFiles);
		otherFlagsBox.add(Box.createHorizontalGlue());
		mainPane.add(otherFlagsBox);
		mainPane.add(Box.createVerticalStrut(5));
		// ZUTUN! TODO! TEHDÄ!!!!! Add boxes for the "Album" tag.
		// Chapter controls.
		mainPane.add(Box.createVerticalStrut(5));
		for (Chapter c:chapters)	{
			// ZUTUN! TODO! TEHDÄ!!!!! Get these inside a scroll box.
			// ZUTUN! TODO! TEHDÄ!!!!! Add text fields for the artist and album. Pre-fill?
			// ZUTUN! TODO! TEHDÄ!!!!! Get the video information (title, maybe) in the JSON.
			Box chapterBox=Box.createHorizontalBox();
			chapterBox.add(new JLabel(c.title));
			chapterBox.add(Box.createHorizontalGlue());
			mainPane.add(chapterBox);
			mainPane.add(Box.createVerticalStrut(5));
		}
		Box buttonBox=Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		JButton mainButton=new JButton("Go!");
		buttonBox.add(mainButton);
		buttonBox.add(Box.createHorizontalGlue());
		mainButton.addActionListener((ActionEvent a)->{
			// ZUTUN! TODO! TEHDÄ!!!!! The code and all that.
			me.dispose();
		});
		mainPane.add(buttonBox);
		mainPane.add(Box.createVerticalStrut(5));
		add(mainPane);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
