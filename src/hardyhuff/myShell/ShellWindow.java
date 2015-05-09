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
			e.printStackTrace();
		}

		JFrame frame = new JFrame("Simple Java Shell");

		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);

		/* create the text area component (output) */
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
		
		/* create the input field */
		textfield = new JTextField(width);
		textfield.setFont(font);
		textfield.addActionListener(new TextFieldListener(textarea));

		/* Add the components to the frame, make it visible */
		frame.setComponentOrientation(java.awt.ComponentOrientation.RIGHT_TO_LEFT);

		frame.setResizable(false);
		frame.add(scroll, BorderLayout.PAGE_START);
		frame.add(textfield, BorderLayout.PAGE_END);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Listens for the TextField to get an Enter
	 * @author kevin
	 */
	private class TextFieldListener implements ActionListener {
		private JTextArea textarea;

		/**
		 * Creates the textfield listener object
		 * @param textarea
		 */
		public TextFieldListener(JTextArea textarea) {
			this.textarea = textarea;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			str = textfield.getText();
			textfield.setText("");
			
			/* Check if either the more thread exists, or handle the talk command, otherwise use the normal command handler in myShell */
			if (t != null && t.isAlive()) {
				synchronized (obj) {
					obj.notify();
				}
			} else if (str.split(" ")[0].matches("more") && str.split(" ").length >= 2) {
				t = new MoreThread(str.split(" ")[1], textarea.getColumns(), textarea.getRows());
				t.start();
			} else if (talkClient != null || talkServer != null) {
				/* if the talk client or talk server exist, change the message and signal the thread to run */
				synchronized (obj){
					message = str + "\n";
					obj.notify();
				}
			} else if (str.split(" ")[0].matches("talk") && str.split(" ").length >= 2 && str.split(" ")[1].split(":").length == 2) {
					/* make the talk client thread, or if that fails, make the server thread instead */
					try {
						talkClient = new TalkClient(str.split(" ")[1]);
						talkClient.createSocket();
					} catch (IOException e1) {
						textarea.append("\nCould not find server at: " + str.split(" ")[1] + "\n");
						talkServer = new TalkServer(str.split(" ")[1]);
						talkServer.createSocket();
					}
			} else {
				/* run a normal, non-threaded command and print its output */
				textarea.append(str);
				textarea.append(shell.runCommand(str));
			}

			/* make sure the text area is scrolled down after a command is run */
			textarea.setCaretPosition(textarea.getDocument().getLength());
		}

	}

	/* Main entry point */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ShellWindow();
			}
		});
	}

	/**
	 * Thread to run the "more" command so that the GUI isn't blocked
	 * @author kevin
	 *
	 */
	public class MoreThread extends Thread {
		String arg = "";
		int width = 80;
		int height = 10;
		
		
		/**
		 *  Instantiates MoreThread class
		 */
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
						/* wait until the thread is notified to update */
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
	
	/**
	 * Sets up the talk client threads for the talk command
	 * @author kevin (based on BIJOY's work at http://www.coderpanda.com/chat-application-in-java/)
	 */
	public class TalkClient {
	    private Socket socket = null;
	    private InputStream inStream = null;
	    private OutputStream outStream = null;
	    private String address;
	    private int port; 
	    
	    /**
	     * Sets the address and port for the talk client
	     * @param arg address:port
	     */
	    TalkClient(String arg) {
	    	String[] args = arg.split(":");
	    	address = args[0];
	    	port = Integer.valueOf(args[1]);
	    }

	    /**
	     * Tries to create the socket, throws an exception if it fails
	     * @throws UnknownHostException
	     * @throws IOException
	     */
	    public void createSocket() throws UnknownHostException, IOException {
	        socket = new Socket(address, port);
	        textarea.append("\nConnected\n");
	        inStream = socket.getInputStream();
	        outStream = socket.getOutputStream();
	        createReadThread();
	        createWriteThread();
	    }

	    /**
	     * Creates the read thread for the client
	     */
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

	    /**
	     * Creates the write thread for the client
	     */
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
	
	
	
	
	/**
	 * Sets up the talk server threads for the talk command
	 * @author kevin (based on BIJOY's work at http://www.coderpanda.com/chat-application-in-java/)
	 */
	public class TalkServer {
		private ServerSocket severSocket;
		private Socket socket;
		private InputStream inStream;
		private OutputStream outStream;
	    private int port; 
	    
	    /**
	     * Sets the port for the talk server
	     * @param arg address:port (address is ignored but it needs to be there to make processing simpler
	     */
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

		/**
		 * Creates the read thread for the server
		 */
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

		/**
		 * Creates the write thread for the server
		 */
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