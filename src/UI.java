import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class UI {
	
	private static BufferedReader in;
	
	public UI(){
		in = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static String[][] readLine(){
		//reading input from console
		String input = " ";
		try {
			input = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//splitting it into segments at each '-'
		String[] commands;
		commands = input.split("-");
		
		//splitting each segment into further segments at each ' '
		String[][] arguments = new String[commands.length-1][];
		for (int i = 1; i < commands.length; i++){
			String s = commands[i];
			arguments[i-1] = s.split(" ");
		}
		return arguments;
	}
}
