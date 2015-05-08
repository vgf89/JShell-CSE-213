package hardyhuff.myShell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.net.Socket;
import java.net.ServerSocket;

public class myShell {
	

	
	public String runCommand(String command) {
		String[] arguments;
		String output = command + "\n";
		arguments = command.split(" ");
		switch (arguments[0]) {
		case "":
			break;
		case "cd":
			output = cd(arguments[1]);
			break;
		case "ls":
			output += ls();
			break;
		case "cp":
			output += cp(arguments[1], arguments[2]);
			break;
		case "mv":
			output += mv(arguments[1], arguments[2]);
			break;
		case "rm":
			output += rm(arguments[1]);
			break;
		case "diff":
			output += diff(arguments[1], arguments[2]);
			break;
		case "more":
			//output += more(arguments[1], 80, 10);
			break;
		case "wc":
			output += wc(arguments[1]);
			break;
		case "mkdir":
			output += mkdir(arguments[1]);
			break;
		case "grep":
			grep(arguments);
			break;
		case "talk":
			talk(arguments[1]);
			break;
		case "ps":
			output += ps();
			break;
		case "kill":
			output += kill(arguments[1]);
			break;
		case "whoami":
			output += whoami();
			break;
		case "env":
			output += env();
			break;
		case "exit":
			System.exit(0);
		default:
			output += "Unknown Command" + "\n";
		}
		output += System.getProperty("user.dir") + "$ ";
		return output;
	}

	String cd(String arg) {
		File file = new File(arg);
		if (file.isDirectory() && file.exists()) {
			try {
				System.setProperty("user.dir", file.getCanonicalPath());
			} catch (IOException e) {

				e.printStackTrace();
			}
			return "" + "\n";
		}
		file = new File(System.getProperty("user.dir") + "/" + arg);
		if (file.isDirectory() && file.exists()) {
			try {
				System.setProperty("user.dir", file.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "" + "\n";
		}
		return "Directory " + arg + " does not exist";
	}

	String ls() {
		String s = "";
		int linelength = 0;
		File workdir = new File(System.getProperty("user.dir"));
		String[] fileList = workdir.list();
		for (String file : fileList) {
			if (s.length() == 0) {
				s += file;
				linelength += file.length();
			} else {
				if (linelength + file.length() + 1 <= ShellWindow.width) {
					s += " " + file;
					linelength += 1 + file.length();
				} else {
					s += "\n" + file;
					linelength = file.length();
				}
			}
		}

		return s + "\n";
	}

	String cp(String arg1, String arg2) {
		File in = new File(arg1);
		if (in.isFile()) {
			File out = new File(arg2);
			try {
				Files.copy(in.toPath(), out.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Failed to copy file " + arg1 + " to " + arg2 + "\n";
			}
			return "" + "\n";
		}

		in = new File(System.getProperty("user.dir") + "/" + arg1);
		if (in.isFile()) {
			File out = new File(arg2);
			try {
				Files.copy(in.toPath(), out.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Failed to copy file " + arg1 + " to " + arg2 + "\n";
			}
			return "" + "\n";
		}

		return "File " + arg1 + " not found" + "\n";
	}

	String mv(String arg1, String arg2) {
		File mv = new File(arg1);
		File des = new File(arg2);

		if (mv.isFile()) {
			try {
				Files.move(mv.toPath(), des.toPath(),
						StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException e) {

				return e.toString();
			}
			return "" + "\n";
		}

		mv = new File(System.getProperty("user.dir") + "/" + arg1);
		if (mv.isFile()) {
			try {
				Files.move(mv.toPath(), des.toPath(),
						StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException e) {

				return e.toString();
			}
			return "" + "\n";
		}
		return null;
	}

	String rm(String arg) {
		File file = new File(arg);
		if (file.isFile()) {
			try {
				Files.delete(file.toPath());
			} catch (IOException e) {

				e.printStackTrace();
				return "Failed to remove file " + arg + "\n";
			}
			return "";
		}

		file = new File(System.getProperty("user.dir") + "/" + arg);
		if (file.isFile()) {
			try {
				Files.delete(file.toPath());
			} catch (IOException e) {

				e.printStackTrace();
				return "Failed to remove file " + arg + "\n";
			}
			return "" + "\n";
		}

		return "Not such file " + arg + "\n";
	}

	String diff(String arg1, String arg2) {
		String output = "";

		String[] a = System.getProperty("os.name").split(" ");
		try {
			String line;
			Process p;
			switch (a[0]) {
			case "Windows":
				p = Runtime.getRuntime().exec(
						System.getenv("windir") + "\\system32\\" + "FC " + arg1
								+ " " + arg2);
				break;
			default:
				p = Runtime.getRuntime().exec("diff " + arg1 + " " + arg2);
				break;
			}
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = input.readLine()) != null) {
				output += line + "\n";
			}
			input.close();
		} catch (Exception err) {
			return err.toString();
		}

		return "" + "\n";

	}

	// TODO: figure this out for GUI
	

	String wc(String arg) {
		BufferedReader in = null;
		String a;
		String[] words;
		int cha = 0;
		int word = 0;
		int lines = 0;
		try {
			in = new BufferedReader(new FileReader(arg));

		} catch (IOException e) {
			try {
				in = new BufferedReader(new FileReader(
						System.getProperty("user.dir") + "/" + arg));
			} catch (FileNotFoundException e1) {
				return e1.toString();

			}
		}
		try {
			while ((a = in.readLine()) != null) {
				words = a.split(" ");
				word += words.length;
				lines++;
				for (int t = 0; t < words.length; t++) {
					a = words[t];
					cha += a.length();
				}

			}
		} catch (IOException e1) {
			return e1.toString();
		}
		try {
			in.close();
		} catch (IOException e) {

			return e.toString();
		}
		return "number of characters: " + cha + "\nnumber of words: " + word
				+ "\nnumber of lines: " + lines + "\n";
	}

	String mkdir(String arg) {
		if (!(new File(System.getProperty("user.dir") + "/" + arg).mkdir()))
			return "Failed to create directory";
		return "" + "\n";
	}

	String grep(String[] args) {
		String output = "";
		String a;
		BufferedReader in = null;
		for (int i = 2; i < args.length; i++) {
			try {
				in = new BufferedReader(new FileReader(args[i]));

			} catch (IOException e) {
				try {
					in = new BufferedReader(new FileReader(
							System.getProperty("user.dir") + "/" + args[i]));
				} catch (FileNotFoundException e1) {
					return e1.toString();

				}
			}
			try {
				while ((a = in.readLine()) != null) {
					if (a.contains(args[1])) {
						output += a + "\n";
					}

				}
			} catch (IOException e1) {
				return e1.toString();
			}
			try {
				in.close();
			} catch (IOException e) {

				return e.toString();
			}
		}

		return "" + "\n";
	}

	String ps() {
		String output = "";
		String[] a = System.getProperty("os.name").split(" ");
		try {
			String line;
			Process p;
			switch (a[0]) {
			case "Windows":
				p = Runtime.getRuntime().exec(
						System.getenv("windir") + "\\system32\\"
								+ "tasklist.exe");
				break;
			default:
				p = Runtime.getRuntime().exec("ps -e");
				break;
			}
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = input.readLine()) != null) {
				output += line + "\n"; // <-- Parse data here.
			}
			input.close();
		} catch (Exception err) {
			return err.toString();
		}

		return "" + "\n";
	}

	String kill(String arg) {
		try {
			Runtime r = Runtime.getRuntime();

			String[] OS = System.getProperty("os.name").split(" ");
			Process p;
			switch (OS[0]) {
			case "Windows":
				p = r.exec("tasklist " + arg);
				break;
			default:
				p = r.exec("kill " + arg);
				break;
			}

			p.waitFor();
			BufferedReader b = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String outputP = "";
			String out = "";
			while ((outputP = b.readLine()) != null)
				out += outputP + "\n";
			return out;
		} catch (IOException e) {
			return "Process could not be killed" + "\n";
		} catch (InterruptedException e) {
			return "Interrupted while killing process" + "\n";
		}
	}

	String whoami() {
		return System.getProperty("user.name") + "\n";
	}

	String env() {
		Map<String, String> env = System.getenv();
		String output = "";
		for (Map.Entry<String, String> entry : env.entrySet())
			output += entry.getKey() + "=" + entry.getValue() + "\n";
		return output;
	}

	int talk(String arg) {
		String output = "";
		String[] args = arg.split(":");
		if (args.length != 2) {
			output += "argument " + arg + " is not correct" + "\n";
			return -1;
		}

		ServerSocket listener = null;
		Socket echoSocket = null;
		Scanner in = null;
		PrintWriter out = null;
		Socket clientSocket;

		// client
		try {
			echoSocket = new Socket(args[0], Integer.parseInt(args[1]));
			in = new Scanner(echoSocket.getInputStream());
			// out = new PrintWriter(echoSocket.getOutputStream(), true);
			// in = new Scanner(echoSocket.getInputStream());
			// input = in;
			// output = out;
			return 0;
		} catch (Exception e) {
			output += "Could not connect to " + args[0] + ":" + args[1] + "\n";
			output += "Creating server" + "\n";
		}

		// server
		try {
			listener = new ServerSocket(Integer.parseInt(args[1]));
			clientSocket = listener.accept();
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			// in = new BufferedReader(new
			// InputStreamReader(clientSocket.getInputStream()));
			// output = out;

		} catch (Exception e) {
			output += "Could not connect to " + args[0] + ":" + args[1] + "\n";
			return -1;
		}
		return 0;
	}



}
