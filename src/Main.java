import javax.swing.JFrame;


public class Main {
	public static void main(String[] args) {
	    
	    Game game = new Game(args);    
	    game.setPreferredSize(Game.DIMENSIONS);
	    game.setMinimumSize(Game.DIMENSIONS);
	    game.setMaximumSize(Game.DIMENSIONS);
	    game.setSize(Game.DIMENSIONS);
	    
	    //creating the game window
	    game.frame = new JFrame(game.NAME);
	    game.frame.setResizable(false);
	    game.frame.add(game); 
	    game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.frame.pack();
	    game.frame.setLocationRelativeTo(null);
	    game.frame.setVisible(true);
	    
	    //starting the game
	    game.start();
	  }
}
