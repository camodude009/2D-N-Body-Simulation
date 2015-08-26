import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
	
		//reads files located in bin
		public static String[] readFile(String fileName){
			//array for lines
			List<String> lines = new ArrayList<String>();
			try {
				//converting the relative file name to an absolute URL and then to a Path
				URL url = FileReader.class.getClassLoader().getResource(fileName);
				Path path = Paths.get(url.toURI());
				//reading the file
				lines = Files.readAllLines(path);
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
			//ignores lines that start with '//'
			ArrayList<String> commands =  new ArrayList<String>();
			for (String line : lines) {
				if(!line.startsWith("//")){
					commands.add(line);
				}
			}
			
			String[] r = new String[commands.size()];
			for(int i = 0; i < commands.size(); i++){
				r[i] = commands.get(i);
			}
			return r;
		}
}