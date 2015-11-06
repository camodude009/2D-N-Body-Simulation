import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

	// reads a file located
	// in the same folder the jar is launched from
	public static String[] readFile(String fileName) {
		// array for lines
		List<String> lines = new ArrayList<String>();
		try {
			// getting the Path of the file
			File file = new File(".");
			Path path = Paths.get(file.getAbsolutePath(), fileName);
			System.out.println(path);
			// reading the file
			lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		} catch (NullPointerException | IOException e) {
			System.out.println("error reading file");
		}
		// ignores lines that start with '--'
		ArrayList<String> commands = new ArrayList<String>();
		for (String line : lines) {
			if (!line.startsWith("--")) {
				commands.add(line);
			}
		}
		String[] r = new String[commands.size()];
		for (int i = 0; i < commands.size(); i++) {
			r[i] = commands.get(i);
		}
		return r;
	}
}