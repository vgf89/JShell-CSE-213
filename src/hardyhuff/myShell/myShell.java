package hardyhuff.myShell;
/**
 * @author David Huff
 * @author Kevin Hard
 * @date 5-8-2015
 * @kill may or may not work
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class myShell {
	
	/**
	 * command evaluation
	 * @param command
	 * @return returns output to the Window 
	 */
	
	public String runCommand(String command) {
		String[] arguments;

		String output = "\n";//command + "\n";
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
		case "wc":
			output += wc(arguments[1]);
			break;
		case "mkdir":
			output += mkdir(arguments[1]);
			break;
		case "grep":
			output += grep(arguments);
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
			output += "Unknown Command or incorrect arguments" + "\n";
		}
		output += System.getProperty("user.dir") + "$ ";
		return output;
	}
	/**
	 * cd command
	 * @param directory 
	 * @return output
	 */
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
		return "\nDirectory " + arg + " does not exist\n";
	}
/**
 * ls command list the files in the directory
 * @return output
 */
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
				if (linelength + file.length() + 1 <= ShellWindow.textarea.getColumns()) {
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
/**
 * copy command
 * @param taret file
 * @param destination
 * @return output
 */
	String cp(String arg1, String arg2) {
		File in = new File(arg1);
		if (in.isFile()) {
			File out = new File(arg2);
			try {
				Files.copy(in.toPath(), out.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				//e.printStackTrace();
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
				//e.printStackTrace();
				return "Failed to copy file " + arg1 + " to " + arg2 + "\n";
			}
			return "" + "\n";
		}

		return "File " + arg1 + " not found" + "\n";
	}
	/**
	 * move command
	 * @param target
	 * @param destination
	 * @return output
	 */
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
		return "";
	}
	/**
	 *  remove command
	 * @param file 
	 * @return output
	 */
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


	/**
	 * 	diff compares 
	 * @param file 1
	 * @param file 2 
	 * @return output if any
	 */
	String diff(String arg1, String arg2) {
		String output = "";
		

		String[] a = System.getProperty("os.name").split(" ");
		try {
			String line;
			Process p;
			switch (a[0]) {
			case "Windows":
				p = Runtime.getRuntime().exec(
						 "C:\\Windows\\system32\\" + "FC " + arg1
								+ " " + arg2);
				break;
			default:
				p = Runtime.getRuntime().exec("/usr/bin/diff " + System.getProperty("user.dir") + "/" + arg1 + " " + System.getProperty("user.dir") + "/" + arg2);
				break;
			}
			ShellWindow.textarea.append("Test4");
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			
			while ((line = input.readLine()) != null) {
				output += line + "\n";
			}
			p.waitFor();
			input.close();
		} catch (Exception err) {
			return err.toString();
		}

		return output;

	}
/**
 * word count command 
 * @param arg file 1
 * @return output
 */
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
	/**
	 * make directory
	 * @param name of the new directory
	 * @return output
	 */
	String mkdir(String arg) {
		if (!(new File(System.getProperty("user.dir") + "/" + arg).mkdir()))
			return "Failed to create directory";
		return "";
	}
	/**
	 * searches files given for matching string 
	 * @param first args is the matching string the rest are file names
	 * @return output
	 */
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
					return e1.toString() + "\n";

				}
			}
			try {
				while ((a = in.readLine()) != null) {
					if (a.contains(args[1])) {
						output += a + "\n";
					}

				}
			} catch (IOException e1) {
				try {
					in.close();
				} catch (IOException e) {
					return e.toString();
				}
				return e1.toString();
			}
			try {
				in.close();
			} catch (IOException e) {

				return e.toString();
			}
		}

		return output;
	}
	/**
	 * list running processes
	 * @return output
	 */
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
				p = Runtime.getRuntime().exec("/usr/bin/ps -e");
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

		return output;
	}
	/**
	 * kill command
	 * @bugs kill might not work for windows
	 * @param process
	 * @return output
	 */
	String kill(String arg) {
		try {
			Runtime r = Runtime.getRuntime();

			String[] OS = System.getProperty("os.name").split(" ");
			Process p;
			switch (OS[0]) {
			case "Windows":
				p = r.exec("taskkill /pid " + arg);
				break;
			default:
				p = r.exec("kill" + " -9 " + arg);
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
	/**
	 * gets the user name
	 * @return output
	 */
	String whoami() {
		return System.getProperty("user.name") + "\n";
	}
	/**
	 * gets environment information
	 * @return output
	 */
	String env() {
		Map<String, String> env = System.getenv();
		String output = "";
		for (Map.Entry<String, String> entry : env.entrySet())
			output += entry.getKey() + "=" + entry.getValue() + "\n";
		return output;
	}
}
