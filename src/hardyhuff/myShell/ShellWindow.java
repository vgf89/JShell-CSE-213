package hardyhuff.myShell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.*;

public class ShellWindow {
	private JTextArea textarea;
	private JTextField textfield;
	private myShell shell = new myShell();
	public static int width = 80;
	public static int height = 20;
	static String str;

	Thread t = null;
	
	public final static Object obj = new Object();



	public ShellWindow() {

		JFrame frame = new JFrame("Simple Java Shell");

		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);

		textarea = new JTextArea(System.getProperty("user.dir") + "$ ", height,
				width);
		textarea.setFont(font);;
		textarea.setFocusable(false);
		textarea.setEditable(false);
		textarea.setForeground(Color.white);
		textarea.setBackground(Color.black);
		JScrollPane scroll = new JScrollPane(textarea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		textfield = new JTextField(width);
		textfield.setFont(font);
		textfield.addActionListener(new TextFieldListener(textarea));
		textfield.setForeground(Color.white);
		textfield.setBackground(Color.black);

		frame.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);

		frame.add(scroll, BorderLayout.PAGE_START);
		frame.add(textfield, BorderLayout.PAGE_END);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

	private class TextFieldListener implements ActionListener {
		private JTextArea textarea;

		public TextFieldListener(JTextArea textarea) {
			this.textarea = textarea;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			str = textfield.getText();
			textfield.setText("");
			
			if (t != null && t.isAlive()) {
				synchronized(obj) {
					obj.notify();
				}
			} else if (str.split(" ")[0].matches("more") && str.split(" ").length >= 2) {
				t = new MoreThread(str.split(" ")[1], width, height);
				t.start();
			} else {
				textarea.append(shell.runCommand(str));
			}

			textarea.setCaretPosition(textarea.getDocument().getLength());
		}

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ShellWindow();
			}
		});
	}

	
	public class MoreThread extends Thread {
		String arg = "";
		int width = 80;
		int height = 10;
		
		
		public MoreThread(String arg, int width, int height) {
			this.arg = arg;
			this.width = width;
			this.height = height;
		}

		@Override
		public void run() {
			String output = "";
			BufferedReader stdin = new BufferedReader(new InputStreamReader(
					System.in));
			BufferedReader filein = null;
			try {
				filein = new BufferedReader(new FileReader(arg));
			} catch (FileNotFoundException e1) {
				try {
					filein = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/" + arg));
				} catch (FileNotFoundException e2) {
					textarea.append("File " + arg + " not found");
				}
			}
			String line;
			int lines = 0;
			String screen = "";

			try {
				while (filein.ready()) {
					screen = "\n";
					lines = 0;
					while (lines < height && filein.ready()) {
						line = filein.readLine();
						while (line != null && line.length() > width) {
							screen += line.substring(0, width) + "\n";
							line = line.substring(width + 1);
							lines++;
						}
						if (lines + 1 > height - 1)
							screen += line;
						else
							screen += line + "\n";
							
						lines++;
					}
					textarea.append(screen);
					textarea.setCaretPosition(textarea.getDocument().getLength());
					
					if (filein.ready()) {
						synchronized(obj) {
							obj.wait();
						}
					}
					
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			textarea.append(System.getProperty("user.dir") + "$ ");
		}

	}
}