package hardyhuff.myShell;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class myShell {
	public static void main(String[] args) throws IOException
	{
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String[] arguments;
		
		while(true) {
			System.out.print(System.getProperty("user.dir") + "$ ");
			arguments = input.readLine().split(" ");
			switch(arguments[0])
			{
			case "cd":
				System.out.println(cd(arguments[1]));
				break;
			case "ls":
				System.out.println(ls());
				break;
			case "cp":
				System.out.println(cp(arguments[1], arguments[2]));
				break;
			case "mv":
				mv(arguments[1],arguments[2]);
				System.out.println(arguments[0]);
				break;
			case "rm":
				System.out.println(rm(arguments[1]));
				break;
			case "diff":
				System.out.println(arguments[0]);
				break;
			case "more":
				System.out.println(arguments[0]);
				break;
			case "wc":
				System.out.println(arguments[0]);
				break;
			case "mkdir":
				System.out.println(arguments[0]);
				break;
			case "grep":
				System.out.println(arguments[0]);
				break;
			case "talk":
				System.out.println(arguments[0]);
				break;
			case "ps":
				System.out.println(arguments[0]);
				break;
			case "kill":
				System.out.println(arguments[0]);
				break;
			case "whoami":
				System.out.println(arguments[0]);
				break;
			case "env":
				System.out.println(System.getenv());
			case "exit":
				return;
			default:
				System.out.println("Unknown Command!");
			}
		}
	}
	
	static String cd(String arg) {
		File file = new File(arg);
		if(file.isDirectory() && file.exists()) {
			try {
				System.setProperty("user.dir", file.getCanonicalPath());
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			return "";
		}
		file = new File(System.getProperty("user.dir") + "/" + arg);
		if(file.isDirectory() && file.exists()) {
			try {
				System.setProperty("user.dir", file.getCanonicalPath());
			} catch (IOException e) {
					e.printStackTrace();
			}
			return "";
		}
		return "Directory " + arg + " does not exist";
	}
	
	static String ls() {
		String s = "";
		File workdir = new File(System.getProperty("user.dir"));
		String[] fileList = workdir.list();
		for (String file : fileList) {
			if (s.length() == 0)
				s += file;
			else
				s += " " + file;
		}
		
		return s;
	}
	
	static String cp(String arg1, String arg2) {
		File in = new File(arg1);
		if (in.isFile()) {
			File out = new File(arg2);
			try {
				Files.copy(in.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Failed to copy file " + arg1 + " to " + arg2;
			}
			return "";
		}
		
		in = new File(System.getProperty("user.dir") + "/" + arg1);
		if (in.isFile()) {
			File out = new File(arg2);
			try {
				Files.copy(in.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Failed to copy file " + arg1 + " to " + arg2;
			}
			return "";
		}
		
		return "File " + arg1 + " not found";
	}
	
	static String mv(String arg1, String arg2) {
		File mv=new File(arg1);
		File des=new File(arg2);
		System.out.println("made it");
		if(mv.isFile()){
			try {
				Files.move(mv.toPath(),des.toPath(),StandardCopyOption.REPLACE_EXISTING);
				
			} catch (IOException e) {
				
				return e.toString();
			}
			return "";
		}
		
		mv=new File(System.getProperty("user.dir") + "/" + arg1);
		if(mv.isFile()){
			try {
				Files.move(mv.toPath(),des.toPath(),StandardCopyOption.REPLACE_EXISTING);
				
			} catch (IOException e) {
				
				return e.toString();
			}
			return "";
		}
		return null;
	}
	
	static String rm(String arg) {
		File file = new File(arg);
		if (file.isFile()) {
			try {
				Files.delete(file.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Failed to remove file " + arg;
			}
			return "";
		}
		
		file = new File(System.getProperty("user.dir") + "/" + arg);
		if (file.isFile()) {
			try {
				Files.delete(file.toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Failed to remove file " + arg;
			}
			return "";
		}
		
		return "Not such file " + arg;
	}
	
	static String diff (String arg1, String arg2) {
		return null;
	}
	
	//TODO: figure this out for GUI
	static String more(String arg, int width, int height) {
		return null;
	}
	
	static String wc(String arg) {
		BufferedReader in = null;
		String a;
		try{
			in = new BufferedReader(new FileReader(arg));
			
		}catch (IOException e) {
				e.printStackTrace();
			}
		
		
		try {
			in.close();
		} catch (IOException e) {
			
			return e.toString();
		}
		return null;
	}
	
	static String mkdir(String arg) {
		File file = new File(arg);
		return null;
	}
	
	static String grep(String[] args) {
		return null;
	}
	
	static String ps() {
		return null;
	}
	
	static String kill(String arg) {
		return null;
	}
	
	static String whoami(String arg) {
		return null;
	}
	
	static String env() {
		return null;
	}
}
