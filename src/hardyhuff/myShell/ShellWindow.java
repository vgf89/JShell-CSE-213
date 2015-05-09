package hardyhuff.myShell;
/**
 * @author David Huff
 * @author Kevin Hard
 * @date 5-8-2015
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.*;

public class ShellWindow {
	public static JTextArea textarea;
	private JTextField textfield;
	private myShell shell = new myShell();
	public static int width = 80;
	public static int height = 20;
	static String str;

	Thread t = null;
	public final static Object obj = new Object();
	
	TalkClient talkClient;
	TalkServer talkServer;
	public static String message = new String();

	/**
	 * Main Window
	 */
	public ShellWindow() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		textfield = new JTextField(width);
		textfield.setFont(font);
		textfield.addActionListener(new TextFieldListener(textarea));

		frame.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);

		frame.setResizable(false);
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
				synchronized (obj) {
					obj.notify();
				}
			} else if (str.split(" ")[0].matches("more") && str.split(" ").length >= 2) {
				t = new MoreThread(str.split(" ")[1], textarea.getColumns(), textarea.getRows());
				t.start();
			} else if (talkClient != null || talkServer != null) {
				synchronized (obj){
					message = str + "\n";
					obj.notify();
				}
			} else if (str.split(" ")[0].matches("talk") && str.split(" ").length >= 2 && str.split(" ")[1].split(":").length == 2) {
					try {
						talkClient = new TalkClient(str.split(" ")[1]);
						talkClient.createSocket();
					} catch (IOException e1) {
						textarea.append("\nCould not find server at: " + str.split(" ")[1] + "\n");
						talkServer = new TalkServer(str.split(" ")[1]);
						talkServer.createSocket();
					}
			} else {
				textarea.append(str);
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
					textarea.append("\nFile " + arg + " not found\n");
					textarea.append(System.getProperty("user.dir") + "$ ");
					return;
				}
			}
			String line;
			int lines = 0;
			String screen = "";

			try {
				while (filein.ready()) {
					screen = "\n";
					lines = 0;
					while (lines < textarea.getRows() && filein.ready()) {
						line = filein.readLine();
						while (line != null && line.length() > textarea.getColumns()) {
							screen += line.substring(0, textarea.getColumns()) + "\n";
							line = line.substring(textarea.getColumns() + 1);
							lines++;
						}
						if (lines + 1 > textarea.getRows() - 1)
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
	
	
	
	
	
	
	
	
	public class TalkClient {
	    private Socket socket = null;
	    private InputStream inStream = null;
	    private OutputStream outStream = null;
	    private String address;
	    private int port; 
	    
	    TalkClient(String arg) {
	    	String[] args = arg.split(":");
	    	address = args[0];
	    	port = Integer.valueOf(args[1]);
	    }

	    public void createSocket() throws UnknownHostException, IOException {
	        socket = new Socket(address, port);
	        textarea.append("\nConnected\n");
	        inStream = socket.getInputStream();
	        outStream = socket.getOutputStream();
	        createReadThread();
	        createWriteThread();
	    }

	    public void createReadThread() {
	        Thread readThread = new Thread() {
	            public void run() {
	            	while (socket.isConnected()) {
						try {
							byte[] readBuffer = new byte[200];
							int num = inStream.read(readBuffer);
							if (num > 0) {
								byte[] arrayBytes = new byte[num];
								System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
								String recvedMessage = new String(arrayBytes,
										"UTF-8");
								ShellWindow.textarea.append("Received message: "
										+ recvedMessage);
								sleep(100);
							}

						} catch (SocketException se) {
							System.exit(0);

						} catch (IOException i) {
							i.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						synchronized (socket){
							try {
								sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

					}
	            }
	        };
	        readThread.setPriority(Thread.MAX_PRIORITY);
	        readThread.start();
	    }

	    public void createWriteThread() {
	        Thread writeThread = new Thread() {
	            public void run() {
	            	while (socket.isConnected()) {
						try {
							synchronized (obj) {
								obj.wait();
							}
							
							if (message != null && message.length() > 0) {
								synchronized (socket) {
									outStream.write(message.getBytes("UTF-8"));
								}
							}
							
							textarea.append(message);

						} catch (IOException i) {
							i.printStackTrace();
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						}

					}
	            }
	        };
	        writeThread.setPriority(Thread.MAX_PRIORITY);
	        writeThread.start();
	    }
	}
	
	
	
	
	
	public class TalkServer {
		private ServerSocket severSocket;
		private Socket socket;
		private InputStream inStream;
		private OutputStream outStream;
	    private int port; 
	    
	    TalkServer(String arg) {
	    	String[] args = arg.split(":");
	    	port = Integer.valueOf(args[1]);
	    }

		public void createSocket() {
			Thread connectThread = new Thread() {
				public void run() {
					try {
						Thread.sleep(100);
						ServerSocket serverSocket = new ServerSocket(port);
						socket = serverSocket.accept();
						inStream = socket.getInputStream();
						outStream = socket.getOutputStream();
						textarea.append("Connected\n");
						createReadThread();
						createWriteThread();
					} catch (IOException io) {
						io.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			textarea.append("Creating server on port: " + String.valueOf(port) + "\n");
			textarea.append("Waiting for Client\n");
			connectThread.start();
			
		}

		public void createReadThread() {
			Thread readThread = new Thread() {
				public void run() {
					while (socket.isConnected()) {
						try {
							byte[] readBuffer = new byte[200];
							int num = inStream.read(readBuffer);
							if (num > 0) {
								byte[] arrayBytes = new byte[num];
								System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
								String recvedMessage = new String(arrayBytes,
										"UTF-8");
								ShellWindow.textarea.append("Received message: "
										+ recvedMessage);
								sleep(100);
							}

						} catch (SocketException se) {
							System.exit(0);

						} catch (IOException i) {
							i.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						synchronized (socket){
							try {
								sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

					}
				}
			};
			readThread.setPriority(Thread.MAX_PRIORITY);
			readThread.start();
		}

		public void createWriteThread() {
			Thread writeThread = new Thread() {
				public void run() {

					while (socket.isConnected()) {
						try {
							synchronized (obj) {
								obj.wait();
							}
							
							if (message != null && message.length() > 0) {
								synchronized (socket) {
									outStream.write(message.getBytes("UTF-8"));
								}
							}
							
							textarea.append(message);

						} catch (IOException i) {
							i.printStackTrace();
						} catch (InterruptedException ie) {
							ie.printStackTrace();
						}

					}
				}
			};
			writeThread.setPriority(Thread.MAX_PRIORITY);
			writeThread.start();

		}
	}

	
	
	
	
	
	
	
	
}