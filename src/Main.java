import javax.swing.JFrame;


public class Main {
	public static void main(String[] args) {
	    
	    Sim sim = new Sim(args);    
	    sim.setPreferredSize(Sim.DIMENSIONS);
	    sim.setMinimumSize(Sim.DIMENSIONS);
	    sim.setMaximumSize(Sim.DIMENSIONS);
	    sim.setSize(Sim.DIMENSIONS);
	    
	    //creating the game window
	    sim.frame = new JFrame(sim.NAME);
	    sim.frame.setResizable(false);
	    sim.frame.add(sim); 
	    sim.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    sim.frame.pack();
	    sim.frame.setLocationRelativeTo(null);
	    sim.frame.setVisible(true);
	    
	    //starting the game
	    sim.start();
	  }
}
