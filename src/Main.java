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
		
		UI ui = new UI();
		
	    Sim sim = new Sim(debug);
	    sim.start();
	    sim.test();
	    
	    Graph graph = new Graph(debug);
	    graph.start();
	    
	    //debugging
	    String[][] t = ui.readLine();
	    
	    for (int i = 0; i < t.length; i++){
	    	for (int j = 0; j < t[i].length; j++){
		    	System.out.println(t[i][j]);
		    }
	    	System.out.println();
	    	System.out.println();
	    }
	  }
}
