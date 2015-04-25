package hardyhuff.myShell;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
		System.setProperty("user.dir", arg);
		return null;
	}
	static String ls() {
		String s="";
		File workdir =new File(System.getProperty("user.dir"));
		File[] fileList=workdir.listFiles();
		for(File file : fileList){
			if(file.isFile())
				if(s.length()==0)
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
		
		return null;
	}
	static String rm(String arg) {
		return null;
	}
	static String diff (String arg1, String arg2) {
		return null;
	}
	static String more(String arg) {
		return null;
	}
	static String wc(String arg) {
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
