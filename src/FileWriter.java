import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileWriter {

	// writes to a file
	// in the same folder the jar is launched from
	public static void writeFile(String fileName, String[] lines) {
		try {
			// getting the Path of the file
			File file = new File(".");
			Path path = Paths.get(file.getAbsolutePath(), fileName);
			// creating the PrintWriter
			OutputStream out = Files.newOutputStream(path);
			PrintWriter writer = new PrintWriter(out);
			// writing the file
			int completion = 0;
			for (int i = 0; i < lines.length; i++) {
				int newCompletion = (int) (100.0 * ((double) i / (double) lines.length));
				if (newCompletion != completion) {
					completion = newCompletion;
					System.out.println(completion + "%");
				}
				// System.out.println(lines[i]);
				writer.println(lines[i]);
			}
			writer.close();
			System.out.println("finished writing to file");
		} catch (IOException e) {
			System.out.println("error writing file");
		}
	}
}
