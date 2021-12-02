package com.nbrevu.capitulator.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.nbrevu.capitulator.deserialiseddata.AppConfig;
import com.nbrevu.capitulator.deserialiseddata.Chapter;

public class StandardBox extends JFrame	{
	private static final long serialVersionUID = -7891317911426722027L;
	
	private static class ChapterTextFields	{
		public final JTextField trackArtist;
		public final JTextField trackTitle;
		public ChapterTextFields(JTextField trackArtist,JTextField trackTitle)	{
			this.trackArtist=trackArtist;
			this.trackTitle=trackTitle;
		}
		public void reverse()	{
			String swap=trackArtist.getText();
			trackArtist.setText(trackTitle.getText());
			trackTitle.setText(swap);
		}
	}
	
	public StandardBox(String videoTitle,List<Chapter> chapters,AppConfig config)	{
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
		mainPane.add(Box.createVerticalStrut(5));
		Box otherFlagsBox=Box.createHorizontalBox();
		otherFlagsBox.add(Box.createHorizontalStrut(5));
		JCheckBox keepVideo=new JCheckBox("Keep video",false);
		otherFlagsBox.add(keepVideo);
		otherFlagsBox.add(Box.createHorizontalGlue());
		JCheckBox deleteEmptyFiles=new JCheckBox("Delete empty audio files",true);
		otherFlagsBox.add(deleteEmptyFiles);
		otherFlagsBox.add(Box.createHorizontalGlue());
		mainPane.add(otherFlagsBox);
		// Album tag
		mainPane.add(Box.createVerticalStrut(5));
		Box albumBox=Box.createHorizontalBox();
		albumBox.add(Box.createHorizontalStrut(5));
		albumBox.add(new JLabel("Album name for the MP3 tags:"));
		albumBox.add(Box.createHorizontalStrut(5));
		JTextField album=new JTextField(videoTitle);
		albumBox.add(album);
		albumBox.add(Box.createHorizontalStrut(5));
		mainPane.add(albumBox);
		// Chapter controls.
		mainPane.add(Box.createVerticalStrut(5));
		Box fullChaptersBox=Box.createVerticalBox();
		fullChaptersBox.add(Box.createVerticalStrut(5));
		List<ChapterTextFields> chapterComponents=new ArrayList<>();
		for (Chapter c:chapters)	{
			Box chapterBox=Box.createVerticalBox();
			Box labelBox=Box.createHorizontalBox();
			labelBox.add(Box.createHorizontalStrut(5));
			labelBox.add(new JLabel(c.title));
			labelBox.add(Box.createHorizontalGlue());
			chapterBox.add(labelBox);
			String artistGuess;
			String trackNameGuess;
			int hyphen=c.title.indexOf('-');
			if (hyphen>0)	{
				artistGuess=c.title.substring(0,hyphen).trim();
				trackNameGuess=c.title.substring(hyphen+1).trim();
			}	else	{
				artistGuess=videoTitle;
				trackNameGuess=c.title;
			}
			Box userInputBox=Box.createHorizontalBox();
			userInputBox.add(Box.createHorizontalStrut(5));
			userInputBox.add(new JLabel("Artist: "));
			userInputBox.add(Box.createHorizontalStrut(5));
			JTextField artist=new JTextField(artistGuess);
			userInputBox.add(artist);
			userInputBox.add(Box.createHorizontalStrut(5));
			userInputBox.add(new JLabel("Title: "));
			JTextField trackName=new JTextField(trackNameGuess);
			userInputBox.add(trackName);
			chapterComponents.add(new ChapterTextFields(artist,trackName));
			userInputBox.add(Box.createHorizontalStrut(5));
			chapterBox.add(userInputBox);
			chapterBox.setBorder(new LineBorder(Color.BLACK));
			fullChaptersBox.add(chapterBox);
			fullChaptersBox.add(Box.createVerticalStrut(5));
		}
		Box reverseUtilityBox=Box.createHorizontalBox();
		reverseUtilityBox.add(Box.createHorizontalGlue());
		JButton reverseButton=new JButton("Reverse artist and track fields");
		reverseUtilityBox.add(reverseButton);
		reverseUtilityBox.add(Box.createHorizontalGlue());
		fullChaptersBox.add(reverseUtilityBox);
		reverseButton.addActionListener((ActionEvent a)->	{
			chapterComponents.forEach(ChapterTextFields::reverse);
		});
		// Scroll for the buttons pane, with maximum size.
		JScrollPane chaptersScroll=new JScrollPane(fullChaptersBox);
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		int preferredHeight=(screenSize.height*3)/5;
		int preferredWidth=screenSize.width/2;
		chaptersScroll.setPreferredSize(new Dimension(preferredWidth,preferredHeight));
		chaptersScroll.setBorder(new TitledBorder("Chapters"));
		mainPane.add(chaptersScroll);
		// Final button box.
		mainPane.add(Box.createVerticalStrut(5));
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