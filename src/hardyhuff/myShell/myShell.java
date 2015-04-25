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
		String s;
		//String command;
		String[] arguments;
		
		while(true) {
			System.out.print(System.getProperty("user.dir") + "$ ");
			s = input.readLine();
			arguments = s.split(" ");
			switch(arguments[0])
			{
			case "cd":
				System.out.println(cd(arguments[1]));
				break;
			case "ls":
				System.out.println(ls());
				break;
			case "cp":
				System.out.println(arguments[0]);
				break;
			case "mv":
				mv(arguments[1],arguments[2]);
				System.out.println(arguments[0]);
				break;
			case "rm":
				System.out.println(arguments[0]);
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
			System.out.println("1");
			return "";
		}
		file = new File(System.getProperty("user.dir") + "/" + arg);
		if(file.isDirectory() && file.exists()) {
			try {
				System.setProperty("user.dir", file.getCanonicalPath());
			} catch (IOException e) {
					e.printStackTrace();
			}
			System.out.println("2");
			return "";
		}
		return "Directory " + arg + " does not exist";
	}
	static String ls() {
		String s="";
		File workdir =new File(System.getProperty("user.dir"));
		File[] fileList=workdir.listFiles();
		for(File file : fileList){
			if(file.isFile())
					s= file.getName();
				else
					s= s+" "+file.getName();
		}
		
		return s;
	}
	static String cp(String arg1, String arg2) {
		return null;
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
		return "";
	}
	static String diff (String arg1, String arg2) {
		return null;
	}
	static String more(String arg) {
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
