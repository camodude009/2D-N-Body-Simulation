import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UI {

	private static BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in));

	public static String[] readLine() {
		// reading input from console
		String input = "";
		try {
			input = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// splitting it into segments at each '-'
		String[] commands;
		commands = input.split("/");
		// removing the first entry ("") in the Array
		// (if the Array has one)
		if (commands.length > 0) {
			String[] r = new String[commands.length - 1];
			for (int i = 1; i < commands.length; i++) {
				r[i - 1] = commands[i];
			}
			return r;
		} else {
			return new String[0];
		}
	}
}