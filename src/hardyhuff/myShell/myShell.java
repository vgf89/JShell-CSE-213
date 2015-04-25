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
			System.out.println(System.getProperty("user.dir") + "$ ");
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
}
