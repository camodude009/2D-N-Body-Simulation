import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileWriter {

	//writes to a file located in the same folder the jar is launched from
	public static void writeFile(String fileName, String[] lines){
		try {
			//getting the Path of the file
			File file = new File(".");
			Path path = Paths.get(file.getAbsolutePath(), fileName);
			//creating the PrintWriter
			OutputStream out = Files.newOutputStream(path);
			PrintWriter writer = new PrintWriter(out);
			//writing the file
			for(String s: lines){
				System.out.println(s);
				writer.println(s);
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("error writing file");
		}
	}
}
