package com.nbrevu.capitulator.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import com.google.common.base.Joiner;
import com.nbrevu.capitulator.ExternalExecutor;
import com.nbrevu.capitulator.data.AppConfig;
import com.nbrevu.capitulator.data.ChapterTextFormat;
import com.nbrevu.capitulator.data.ChapterTextFormat.ParseResult;
import com.nbrevu.capitulator.data.RawChapterDefinition;

public class AuxiliaryBox extends JFrame	{
	private final static long serialVersionUID = -8133605883428059905L;

	public AuxiliaryBox(String youtubeUrl,String videoTitle,double videoDuration,AppConfig config,ExternalExecutor executor,ImageIcon icon)	{
		super("No chapters detected!");
		JFrame me=this;
		setIconImage(icon.getImage());
		Box mainBox=Box.createVerticalBox();
		mainBox.add(Box.createVerticalStrut(5));
		Box labelBox1=Box.createHorizontalBox();
		labelBox1.add(Box.createHorizontalStrut(5));
		labelBox1.add(new JLabel("No chapters found! It seems that Youtube has not divided this video in chapters."));
		labelBox1.add(Box.createHorizontalStrut(5));
		labelBox1.add(Box.createHorizontalGlue());
		mainBox.add(labelBox1);
		mainBox.add(Box.createVerticalStrut(5));
		Box labelBox2=Box.createHorizontalBox();
		labelBox2.add(Box.createHorizontalStrut(5));
		labelBox2.add(new JLabel("You can choose between any of the following options:"));
		labelBox2.add(Box.createHorizontalStrut(5));
		labelBox2.add(Box.createHorizontalGlue());
		mainBox.add(labelBox2);
		mainBox.add(Box.createVerticalStrut(5));
		ButtonGroup radios=new ButtonGroup();
		Box firstRadioBox=Box.createHorizontalBox();
		firstRadioBox.add(Box.createHorizontalStrut(5));
		JRadioButton singleOption=new JRadioButton("Treat the whole video as a single chapter.");
		firstRadioBox.add(singleOption);
		radios.add(singleOption);
		firstRadioBox.add(Box.createHorizontalStrut(5));
		firstRadioBox.add(Box.createHorizontalGlue());
		mainBox.add(firstRadioBox);
		mainBox.add(Box.createVerticalStrut(5));
		Box secondRadioBox=Box.createHorizontalBox();
		secondRadioBox.add(Box.createHorizontalStrut(5));
		JRadioButton manualOption=new JRadioButton("Introduce chapters manually:");
		secondRadioBox.add(manualOption);
		radios.add(manualOption);
		singleOption.setSelected(false);
		manualOption.setSelected(true);
		secondRadioBox.add(Box.createHorizontalStrut(5));
		secondRadioBox.add(Box.createHorizontalGlue());
		JButton loadFromFile=new JButton("Load from file...");
		secondRadioBox.add(loadFromFile);
		secondRadioBox.add(Box.createHorizontalStrut(5));
		mainBox.add(secondRadioBox);
		mainBox.add(Box.createVerticalStrut(5));
		Box areaBox=Box.createHorizontalBox();
		JTextArea area=new JTextArea();
		areaBox.add(Box.createHorizontalStrut(5));
		JScrollPane areaScroll=new JScrollPane(area);
		areaScroll.setBorder(new LineBorder(Color.BLACK));
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		int preferredHeight=(screenSize.height*2)/5;
		int preferredWidth=screenSize.width/2;
		areaScroll.setPreferredSize(new Dimension(preferredWidth,preferredHeight));
		areaBox.add(areaScroll);
		areaBox.add(Box.createHorizontalStrut(5));
		mainBox.add(areaBox);
		mainBox.add(Box.createVerticalStrut(5));
		Box buttonBox=Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		JButton mainButton=new JButton("Next step");
		buttonBox.add(mainButton);
		buttonBox.add(Box.createHorizontalGlue());
		mainBox.add(buttonBox);
		mainBox.add(Box.createVerticalGlue());
		ActionListener radioActionListener=(ActionEvent e)->	{
			boolean isManualOption=manualOption.isSelected();
			loadFromFile.setEnabled(isManualOption);
			area.setEnabled(isManualOption);
		};
		singleOption.addActionListener(radioActionListener);
		manualOption.addActionListener(radioActionListener);
		loadFromFile.addActionListener((ActionEvent e)->	{
			JFileChooser fileChooser=new JFileChooser(config.saveFolder.toFile());
			fileChooser.setDialogTitle("Choose the text file where the chapters are stored");
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fileChooser.showOpenDialog(me)==JFileChooser.APPROVE_OPTION)	{
				Path textFile=fileChooser.getSelectedFile().toPath();
				try	{
					area.setText(Joiner.on(System.lineSeparator()).join(Files.readAllLines(textFile)));
				}	catch (IOException exc)	{
					JOptionPane.showMessageDialog(me,"Error opening file "+textFile.toAbsolutePath().toString()+": "+exc.getMessage()+".","Error opening file.",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		Consumer<List<RawChapterDefinition>> goToMain=(List<RawChapterDefinition> chapters)->	{
			StandardBox mainDialog=new StandardBox(youtubeUrl,videoTitle,chapters,config,executor,icon);
			me.setVisible(false);
			me.dispose();
			mainDialog.setVisible(true);
		};
		mainButton.addActionListener((ActionEvent e)->	{
			if (singleOption.isSelected())	{
				RawChapterDefinition singleChapter=new RawChapterDefinition(videoTitle,0,videoDuration);
				goToMain.accept(List.of(singleChapter));
			}	else	{
				String chapterText=area.getText();
				ParseResult parsed=ChapterTextFormat.parse(chapterText,videoDuration);
				if (parsed.chapters.isEmpty())	{
					StringBuilder message=new StringBuilder();
					message.append("No chapter data was found in the provided text. Valid regex formats are:");
					for (String pattern:ChapterTextFormat.getPatterns())	{
						message.append(System.lineSeparator());
						message.append(pattern);
					}
					JOptionPane.showMessageDialog(me,"No chapters found",message.toString(),JOptionPane.ERROR_MESSAGE);
				}	else if (!parsed.unparsedStrings.isEmpty())	{
					StringBuilder message=new StringBuilder();
					message.append("Some lines have not been parsed correctly, like \"");
					message.append(parsed.unparsedStrings.get(0));
					message.append("\". In total, there are ");
					message.append(parsed.chapters.size());
					message.append(" successfully parsed chapters and ");
					message.append(parsed.unparsedStrings.size());
					message.append(" unparsed lines.");
					message.append(System.lineSeparator().repeat(2));
					message.append("Do you wish to use this data? Choose No if you want to review the text and try again.");
					if (JOptionPane.showConfirmDialog(me,message.toString(),"Potentially invalid info",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) goToMain.accept(parsed.chapters);
				}	else goToMain.accept(parsed.chapters);
			}
		});
		add(mainBox);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
