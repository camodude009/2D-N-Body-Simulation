import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
	
		public static String[] readFile(){
			//reads all lines
			List<String> lines = new ArrayList<String>();
			try {
				lines = Files.readAllLines(Paths.get("/test1.pl"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			//ignores lines that start with '//'
			ArrayList<String> commands =  new ArrayList<String>();
			for (String line : lines) {
				if(!line.startsWith("//")){
					commands.add(line);
				}
			}
			String[] r = (String[]) commands.toArray();
			return r;
		}
}