import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class UI {
	
	private static BufferedReader in;
	
	public UI(){
		in = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public static String[] readLine(){
		String line = "";
		try {
			line = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line.split(" ");
	}
}
