package com.nbrevu.capitulator.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.nbrevu.capitulator.ExternalExecutor;
import com.nbrevu.capitulator.data.AppConfig;
import com.nbrevu.capitulator.data.RawChapterDefinition;
import com.nbrevu.capitulator.data.UserDefinedChapter;

public class StandardBox extends JFrame	{
	private final static long serialVersionUID = -7891317911426722027L;
	
	private final static char[] INVALID_CHARACTERS=new char[] {'\\','/',':','*','?','\"','<','>','|'};
	
	private static String sanitise(String in)	{
		for (char c:INVALID_CHARACTERS) in=in.replace(c,'_');
		return in;
	}
	
	private static void replaceSuffix(JTextField component,String expectedSuffix,String newSuffix)	{
		String text=component.getText();
		if (text.endsWith(expectedSuffix))	{
			text=text.substring(0,text.length()-expectedSuffix.length())+newSuffix;
			component.setText(text);
		}
	}
	
	private static class ChapterTextFields	{
		public final JCheckBox isActive;
		public final JTextField trackArtist;
		public final JTextField trackTitle;
		public final JTextField fileName;
		public ChapterTextFields(JCheckBox isActive,JTextField trackArtist,JTextField trackTitle,JTextField fileName)	{
			this.isActive=isActive;
			this.trackArtist=trackArtist;
			this.trackTitle=trackTitle;
			this.fileName=fileName;
		}
		public void reverseArtistAndTitle()	{
			String swap=trackArtist.getText();
			trackArtist.setText(trackTitle.getText());
			trackTitle.setText(swap);
		}
	}
	
	public StandardBox(String youtubeUrl,String videoTitle,List<RawChapterDefinition> chapters,AppConfig config,ExternalExecutor executor,ImageIcon icon)	{
		super("Partition youtube videos by chapter");
		StandardBox me=this;
		setIconImage(icon.getImage());
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
		// Album tag.
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
		for (RawChapterDefinition c:chapters)	{
			Box chapterBox=Box.createVerticalBox();
			Box labelBox=Box.createHorizontalBox();
			labelBox.add(Box.createHorizontalStrut(5));
			JCheckBox isActive=new JCheckBox(c.title,true);
			labelBox.add(isActive);
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
			userInputBox.add(Box.createHorizontalStrut(5));
			chapterBox.add(userInputBox);
			Box fileNameBox=Box.createHorizontalBox();
			fileNameBox.add(Box.createHorizontalStrut(5));
			fileNameBox.add(new JLabel("File name: "));
			JTextField fileName=new JTextField(sanitise(c.title)+".mp3");
			fileNameBox.add(fileName);
			fileNameBox.add(Box.createHorizontalStrut(5));
			fileNameBox.add(Box.createHorizontalGlue());
			chapterBox.add(fileNameBox);
			chapterBox.setBorder(new LineBorder(Color.BLACK));
			fullChaptersBox.add(chapterBox);
			fullChaptersBox.add(Box.createVerticalStrut(5));
			isActive.addActionListener((ActionEvent e)->	{
				boolean active=isActive.isSelected();
				artist.setEnabled(active);
				trackName.setEnabled(active);
				fileName.setEnabled(active);
			});
			chapterComponents.add(new ChapterTextFields(isActive,artist,trackName,fileName));
		}
		Box reverseUtilityBox=Box.createHorizontalBox();
		reverseUtilityBox.add(Box.createHorizontalGlue());
		JButton reverseButton=new JButton("Reverse artist and track fields");
		reverseUtilityBox.add(reverseButton);
		reverseUtilityBox.add(Box.createHorizontalGlue());
		fullChaptersBox.add(reverseUtilityBox);
		fullChaptersBox.add(Box.createVerticalGlue());
		reverseButton.addActionListener((ActionEvent a)->	{
			chapterComponents.forEach(ChapterTextFields::reverseArtistAndTitle);
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
		keepVideo.addActionListener((ActionEvent a)->	{
			if (keepVideo.isSelected()) for (ChapterTextFields fields:chapterComponents) replaceSuffix(fields.fileName,".mp3",".mp4");
			else for (ChapterTextFields fields:chapterComponents) replaceSuffix(fields.fileName,".mp4",".mp3");
		});
		mainButton.addActionListener((ActionEvent a)->{
			Path basePath=Paths.get(defaultDir.getText());
			basePath.toFile().mkdirs();
			List<UserDefinedChapter> processedChapters=new ArrayList<>();
			for (int i=0;i<chapters.size();++i)	{
				RawChapterDefinition data=chapters.get(i);
				ChapterTextFields fields=chapterComponents.get(i);
				if (fields.isActive.isSelected())	{
					Path chapterFile=basePath.resolve(fields.fileName.getText());
					double startTime=data.startMark;
					double endTime=data.endMark;
					String artist=fields.trackArtist.getText();
					String trackName=fields.trackTitle.getText();
					processedChapters.add(new UserDefinedChapter(i+1,chapterFile,startTime,endTime,artist,trackName));
				}
			}
			if (processedChapters.isEmpty())	{
				JOptionPane.showMessageDialog(me,"Please choose at least one chapter to cut.","You chose... poorly",JOptionPane.ERROR_MESSAGE);
				return;
			}
			boolean mustKeepVideo=keepVideo.isSelected();
			boolean mustDeleteEmptyFiles=deleteEmptyFiles.isSelected();
			String albumName=album.getText();
			new Thread(()->	{
				List<Path> deletedFiles=new ArrayList<>();
				try	{
					Path downloadedYoutubeMp4File=executor.downloadVideo(youtubeUrl,me);
					for (UserDefinedChapter chapter:processedChapters)	{
						executor.cutFile(downloadedYoutubeMp4File,chapter,mustKeepVideo,albumName,me);
						if (mustDeleteEmptyFiles&&executor.containsSilences(chapter.file))	{
							Files.delete(chapter.file);
							deletedFiles.add(chapter.file);
						}
					}
					Files.delete(downloadedYoutubeMp4File);
					if (deletedFiles.isEmpty()) JOptionPane.showMessageDialog(me,"Operation successful! All the files were successfully written.","ENDUT! HOCH HECH!",JOptionPane.INFORMATION_MESSAGE);
					else	{
						StringBuilder sb=new StringBuilder();
						sb.append("The following files were deleted because they were actually silent:");
						for (Path file:deletedFiles)	{
							sb.append(System.lineSeparator());
							sb.append(file.getFileName());
						}
						JOptionPane.showMessageDialog(me,sb.toString(),"Partial success",JOptionPane.WARNING_MESSAGE);
					}
				}	catch (IOException exc)	{
					JOptionPane.showMessageDialog(me,"An I/O error happened:\n"+exc.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
				}	finally	{
					me.dispose();
				}
			}).start();
		});
		mainPane.add(buttonBox);
		mainPane.add(Box.createVerticalStrut(5));
		add(mainPane);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
