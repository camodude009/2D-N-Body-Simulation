import java.awt.event.WindowEvent;

import javax.swing.JFrame;


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
		
		UI ui = new UI();
		
	    Sim sim = new Sim(debug);
	    sim.start();
	    sim.test();
	    
	    Graph graph = new Graph(debug);
	    graph.start();
	  }
}
