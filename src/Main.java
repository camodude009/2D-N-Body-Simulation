


public class Main {
	
	private static Sim sim;
	
	//error strings
	private static final String ERROR_INVALID_COMMAND = "invalid command";
	private static final String ERROR_SIM_NOT_PAUSED = "please pause the simulator to make changes";
	
	public static void main(String[] args) {
		boolean debug = false;
		for(String s: args){
			switch (s){
				case "-d":
					debug = true;
					break;
			}
		}
		
	    sim = new Sim(debug);
	    sim.start();
	    
	    while(true){
	    	commands(UI.readLine());
	    }
	}
	
	public static void command(String cmdString){
		//cutting off the segment indicating the type of command
		String[] cmd = cmdString.split(" ");
		String[] args = new String[cmd.length-1];
		for(int i = 1; i < cmd.length; i++) args[i-1] = cmd[i];
		
		//acting on different commands
		if(cmd[0].equals("pause")){ //pause
			pause();
		}else if(cmd[0].equals("addplanet")){ //spawn planet
			addPlanet(args);
		}else if(cmd[0].equals("load")){ //load
			load(args);
		}else if(cmd[0].equals("save")){ //save
			save(args);
		}else if(cmd[0].equals("reset")){ //reset
			reset();
		}else if(cmd[0].equals("speed")){ //speed
			setSpeed(args);
		}else if(cmd[0].equals("stepsize")){ //speed
			setStepSize(args);
		}else if(cmd[0].equals("algo")){ //algorithm
			algorithm(args);
		}else if(cmd[0].equals("grav")){ //gravitational constant
			setG(args);
		}else if(cmd[0].equals("scale")){ //scale
			scale(args);
		}else{
			System.out.println(ERROR_INVALID_COMMAND);
		}
	}
	
	public static void commands(String[] cmds){
		//executing a list of commands
		if (cmds.length != 0) for(String cmd: cmds) command(cmd);
		else System.out.println(ERROR_INVALID_COMMAND);
	}
	
	//commands
	public static void pause(){
		if(sim.pause()){
			System.out.println("paused");
		}else{
			System.out.println("unpaused");
		}
	}
	public static void addPlanet(String[] args){
		if(sim.getPaused()){
			try{
				double x = Double.parseDouble(args[0]);
				double y = Double.parseDouble(args[1]);
				double vX = Double.parseDouble(args[2]);
				double vY = Double.parseDouble(args[3]);
				double m = Double.parseDouble(args[4]); //1000/Sim.G;
				double p = Double.parseDouble(args[5]);
				sim.addPlanet(x, y, vX, vY, m, p);
				System.out.println("planet created");
			}catch(NumberFormatException | NullPointerException | IndexOutOfBoundsException e){
				System.out.println(ERROR_INVALID_COMMAND);
			}
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void load(String[] args){
		if(sim.getPaused()){
			reset();
			if(args.length == 1) commands(FileReader.readFile(args[0]));
			else System.out.println(ERROR_INVALID_COMMAND);
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void save(String[] args){
		if(sim.getPaused()){
			if(args.length == 1) FileWriter.writeFile(args[0], sim.getPlanetsAsText());
			else System.out.println(ERROR_INVALID_COMMAND);
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void reset(){
		sim.reset();
	}
	public static void setSpeed(String[] args){
		if(sim.getPaused()){
			if(!sim.changeSpeed(args)) System.out.println(ERROR_INVALID_COMMAND);
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void setStepSize(String[] args){
		if(sim.getPaused()){
			if(!sim.changeStepSize(args)) System.out.println(ERROR_INVALID_COMMAND);
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void algorithm(String[] args){
		try{
			int a = Integer.parseInt(args[0]);
			sim.setAlgorithm(a);
		}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
			System.out.println(ERROR_INVALID_COMMAND);
		}
	}
	public static void setG(String[] args){
		try{
			if(args[0].equals("G")){
				sim.setG(Sim.G);
			}else{
				double g = Double.parseDouble(args[0]);
				sim.setG(g);
			}
		}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
			System.out.println(ERROR_INVALID_COMMAND);
		}
	}
	public static void scale(String[] args){
		try{
			double s = Double.parseDouble(args[0]);
			sim.setScale(s);
		}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
			System.out.println(ERROR_INVALID_COMMAND);
		}
	}
	
}