package hardyhuff.myShell;
import javax.swing.*;

public class shellWindow {
	private static void buildWindow() {
		JFrame frame = new JFrame("HelloWorld!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextArea console = new JTextArea(20, 80);
		frame.getContentPane().add(console);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new Thread () {
			public void run() {
				buildWindow();
			}
		}.start();
	}
}