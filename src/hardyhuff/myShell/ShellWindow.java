package hardyhuff.myShell;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ShellWindow {
	private JTextArea textarea;
	private JTextField textfield;
	private myShell shell = new myShell();
	private int width = 50;
	private int height = 20;

	public ShellWindow() {

		JFrame frame = new JFrame("Simple Java Shell");

		textarea = new JTextArea(System.getProperty("user.dir") + "$ ", height,
				width);
		textarea.setFocusable(false);
		textarea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textarea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		textfield = new JTextField(width);
		textfield.addActionListener(new TextFieldListener(textarea));

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
			String str = textfield.getText();
			textfield.setText("");
			textarea.append(shell.runCommand(str));
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

}