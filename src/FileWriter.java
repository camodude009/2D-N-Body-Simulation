import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileWriter {

	//writes to a file located in the same folder as the jar
	public static void writeFile(String fileName, String[] lines){
		try {
			//converting the relative file name to an absolute URL and then to a Path
			URL url = FileReader.class.getProtectionDomain().getCodeSource().getLocation();
			//path of the directory
			Path dirPath = Paths.get(url.toURI());
			//path of the file we are trying to create
			Path path = Paths.get(dirPath.toString(), fileName);
			OutputStream out = Files.newOutputStream(path);
			PrintWriter writer = new PrintWriter(out);
			//writing the file
			for(String s: lines){
				System.out.println(s);
				writer.println(s);
			}
			writer.close();
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}
}
