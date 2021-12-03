package com.nbrevu.capitulator.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ProgressDialog extends JDialog {
	private final static long serialVersionUID = -2912617383156992364L;
	private final static String DEFAULT_TEXT=" ".repeat(200);
	
	private final JLabel updatableText;
	
	public ProgressDialog(JFrame parent,String fixedLabel)	{
		super(parent,"Ongoing operation",true);
		Box mainBox=Box.createVerticalBox();
		mainBox.add(Box.createVerticalStrut(5));
		Box messageBox=Box.createHorizontalBox();
		messageBox.add(Box.createHorizontalStrut(5));
		messageBox.add(new JLabel("Please wait until the "+fixedLabel+"..."));
		messageBox.add(Box.createHorizontalGlue());
		mainBox.add(messageBox);
		mainBox.add(Box.createVerticalStrut(20));
		Box mutableBox=Box.createHorizontalBox();
		mutableBox.add(Box.createHorizontalGlue());
		updatableText=new JLabel(DEFAULT_TEXT,SwingConstants.CENTER);
		mutableBox.add(updatableText);
		mutableBox.add(Box.createHorizontalGlue());
		mainBox.add(mutableBox);
		mainBox.add(Box.createVerticalStrut(5));
		add(mainBox);
		//setUndecorated(true);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		addWindowListener(new WindowAdapter()	{
			@Override
			public void windowClosing(WindowEvent e)	{}
		});
	}
	
	public void doShow()	{
		SwingUtilities.invokeLater(()->setVisible(true));
	}
	
	public void updateText(String text)	{
		System.out.println(text);
		updatableText.setText(text);
	}
}
