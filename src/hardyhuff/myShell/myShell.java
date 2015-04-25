package hardyhuff.myShell;
import java.io.BufferedReader;
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
				System.out.println(arguments[0]);
				break;
			case "ls":
				System.out.println(arguments[0]);
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
	
	String cd(String arg) {
		return null;
	}
	String ls() {
		return null;
	}
	String cp(String arg1, String arg2) {
		return null;
	}
	String mv(String arg1, String arg2) {
		return null;
	}
	String rm(String arg) {
		return null;
	}
	String diff (String arg1, String arg2) {
		return null;
	}
	String more(String arg) {
		return null;
	}
	String wc(String arg) {
		return null;
	}
	String mkdir(String arg) {
		return null;
	}
	String grep(String[] args) {
		return null;
	}
	String ps() {
		return null;
	}
	String kill(String arg) {
		return null;
	}
	String whoami(String arg) {
		return null;
	}
	String env() {
		return null;
	}
}
