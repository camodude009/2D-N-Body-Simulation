import java.util.Arrays;





public class Main {
	public static void main(String[] args) {
		boolean debug = false;
		for(String s: args){
			switch (s){
				case "-d":
					debug = true;
					break;
			}
		}
		
	    Sim sim = new Sim(debug);
	    sim.start();
	    
	    Graph graph = new Graph(debug);
	    graph.start();
	    
	    sim.test();
	}
}
