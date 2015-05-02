package hardyhuff.myShell;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

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
				System.out.println(wc(arguments[1]));
				break;
			case "mkdir":
				System.out.println(mkdir(arguments[1]));
				break;
			case "grep":
				grep(arguments);
				break;
			case "talk":
				System.out.println(arguments[0]);
				break;
			case "ps":
				System.out.println(ps());
				break;
			case "kill":
				System.out.println(kill(arguments[1]));
				break;
			case "whoami":
				System.out.println(whoami());
				break;
			case "env":
				System.out.println(env());
				break;
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
		BufferedReader in = null;
		String line;
		int lines = 0;
		String screen = null;
		
		try {
			in = new BufferedReader(new FileReader(arg));
		} catch (IOException e) {
			return "Could no open file " + arg;
		}
		
		int i = 0;
		//TODO: look for \n in stdin to continue loop
		while (lines < height){
			try {
				while ((line = in.readLine()) != null) {
					for (char c : line.toCharArray()) {
						if (i >= width && c != '\n') {
							screen += '\n' + c;
							i = 0;
							lines++;
						} else if (c == '\n') {
							screen += c;
							i = 0;
							lines++;
						} else {
							screen += c;
							i++;
						}
					}
				}
			} catch (IOException e) {
				return "Failed to read file " + arg;
			}
		}
		return "";
	}
	
	static String wc(String arg) {
		BufferedReader in = null;
		String a;
		String[] words;
		int cha=0;
		int word=0;
		int lines=0;
		try{
			in = new BufferedReader(new FileReader(arg));
			
		}catch (IOException e) {
			try {
				in = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/" + arg));
			} catch (FileNotFoundException e1) {
				return e1.toString();
				
			}
		}
		try {
			while((a=in.readLine()) != null){
				words=a.split(" ");
				word+=words.length;
				lines++;
				for(int t=0;t<words.length;t++){
					a=words[t];
					cha+=a.length();
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
		return ("number of characters: "+cha+"\nnumber of words: "+word+"\nnumber of lines: "+lines);
	}
	
	static String mkdir(String arg) {
		File file = new File(arg);
		return null;
	}
	
	static String grep(String[] args) {
			String a;
			BufferedReader in = null;
			for(int i=2;i<args.length;i++){
				try{
					in = new BufferedReader(new FileReader(args[i]));
					
				}catch (IOException e) {
					try {
						in = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/" + args[i]));
					} catch (FileNotFoundException e1) {
						return e1.toString();
						
					}
				}
				try {
					while((a = in.readLine()) != null){
						if( a.contains( args[1] )){
							System.out.println(a);
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
		return "";
	}
	
	static String ps() {
		String[] a=System.getProperty("os.name").split(" ");
		try {
	        String line;
	        Process p;
			switch (a[0]){
			case "Windows":
				p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
				break;
			default:
				p = Runtime.getRuntime().exec("ps -e");
				break;
			}
	        BufferedReader input =
	                new BufferedReader(new InputStreamReader(p.getInputStream()));
	        while ((line = input.readLine()) != null) {
	            System.out.println(line); //<-- Parse data here.
	        }
	        input.close();
	    } catch (Exception err) {
	        return err.toString();
	    }
		
		
		return "";
	}
	
	static String kill(String arg) {
			try {
				Runtime r = Runtime.getRuntime();
				
				String[] OS = System.getProperty("os.name").split(" ");
				Process p;
				switch (OS[0]){
				case "Windows":
					p = r.exec("tasklist " + arg);
					break;
				default:
					p = r.exec("kill " + arg);
					break;
				}
				
				p.waitFor();
				BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
				String outputP = "";
				String out = "";
				while ((outputP = b.readLine()) != null) 
					out += outputP + "\n";
				return out;
			} catch (IOException e) {
				return "Process could not be killed";
			} catch (InterruptedException e) {
				return "Interrupted while killing process";
			}
	}
	
	static String whoami() {
		return System.getProperty("user.name");
	}
	
	static String env() {
		Map<String, String> env = System.getenv();
		String output = "";
		for (Map.Entry<String, String> entry : env.entrySet())
			output += entry.getKey() + "=" + entry.getValue() + "\n";
		return output;
	}
}
