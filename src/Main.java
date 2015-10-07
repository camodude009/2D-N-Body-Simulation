import java.awt.Color;




public class Main {
	
	private static Sim sim;
	
	//error strings
	private static final String ERROR_INVALID_COMMAND = "invalid command";
	private static final String ERROR_SIM_NOT_PAUSED = "please pause the simulator to make changes";
	private static final Color[] colors = {	new Color(0,0,0),		//0 : black
											new Color(255,255,255),	//1 : white
											new Color(240,85,12),	//2 : orange
											new Color(239,16,16),	//3 : red
											new Color(37,181,188),	//4 : turquoise
											new Color(162,83,196),	//5 : purple
											new Color(80,206,81),	//6 : green
											new Color(189,64,170),	//7 : pink
											new Color(80,80,153)	//8 : gray-blue
	};
	
	public static void main(String[] args) {
		boolean debug = false;
		for(String s: args){
			switch (s){
				case "-d":
					debug = true;
					break;
			}
		}
		
	    sim = new Sim(debug, false);
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
		}else if(cmd[0].equals("realtime")){ //realtime screen output
			setRealTime(args);
		}else if(cmd[0].equals("targettime")){ //realtime screen output
			setTargetTime(args);
		}else if(cmd[0].equals("data")){ //collect data (momentum, eKin, ePot, energy)
			setCollectData(args);
		}else if(cmd[0].equals("savedata")){ //save data
			saveData(args);
		}else if(cmd[0].equals("resetdata")){ //reset data
			resetData();
		}else if(cmd[0].equals("datadetail")){ //data collection detail (every 'd' tick -> 1 = every tick)
			setDataDetail(args);
		}else if(cmd[0].equals("bg")){ //bg color
			setBGColor(args);
		}else if(cmd[0].equals("history")){ //planet history (path) length
			setHistoryLength(args);
		}else if(cmd[0].equals("historydetail")){ //planet history (path) detail (every 'd' tick -> 1 = every tick)
			setHistoryDetail(args);
		}else if(cmd[0].equals("historygradient")){ //planet history gradient
			setHistoryGradient(args);
		}else if(cmd[0].equals("step")){ //planet history gradient
			step();
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
				double m = Double.parseDouble(args[4]);
				double p = Double.parseDouble(args[5]);
				int c = Integer.parseInt(args[6]);
				sim.addPlanet(x, y, vX, vY, m, p, colors[c]);
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
			if(args.length == 1){
				commands(FileReader.readFile(args[0]));
				sim.render();
			}
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
		if(sim.getPaused()){
			try{
				int a = Integer.parseInt(args[0]);
				sim.setAlgorithm(a);
			}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
				System.out.println(ERROR_INVALID_COMMAND);
			}
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void setG(String[] args){
		if(sim.getPaused()){
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
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
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
	public static void setRealTime(String[] args){
		if(sim.getPaused()){
			try{
				int i = Integer.parseInt(args[0]);
				if(i == 0) sim.setRealTime(false);
				else if(i == 1) sim.setRealTime(true);
				else System.out.println(ERROR_INVALID_COMMAND);
			}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
				System.out.println(ERROR_INVALID_COMMAND);
			}
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void setTargetTime(String[] args){
		if(sim.getPaused()){
			try{
				double t = Double.parseDouble(args[0]);
				sim.setTargetTime(t);
			}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
				System.out.println(ERROR_INVALID_COMMAND);
			}
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void setCollectData(String[] args){
		if(sim.getPaused()){
			try{
				int i = Integer.parseInt(args[0]);
				if(i == 0) sim.setCollectData(false);
				else if(i == 1) sim.setCollectData(true);
				else System.out.println(ERROR_INVALID_COMMAND);
			}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
				System.out.println(ERROR_INVALID_COMMAND);
			}
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void resetData(){
		sim.resetData();
	}
	public static void saveData(String[] args){
		if(args.length == 1){
			if(sim.getPaused()){
				sim.saveData(args[0]);
			}else{
				System.out.println(ERROR_SIM_NOT_PAUSED);
			}
		}else{
			System.out.println(ERROR_INVALID_COMMAND);
		}
	}
	public static void setDataDetail(String[] args){
		if(sim.getPaused()){
			try{
				int d = Integer.parseInt(args[0]);
				sim.setDataDetail(d);
			}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
				System.out.println(ERROR_INVALID_COMMAND);
			}
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void setBGColor(String[] args){
		if(sim.getPaused()){
			try{
				int c = Integer.parseInt(args[0]);
				sim.setBGColor(colors[c]);
				System.out.println("background color set to " + c);
			}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
				System.out.println(ERROR_INVALID_COMMAND);
			}
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void setHistoryLength(String[] args){
		if(sim.getPaused()){
			try{
				double l = Double.parseDouble(args[0]);
				sim.setHistoryLength(l);
			}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
				System.out.println(ERROR_INVALID_COMMAND);
			}
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void setHistoryDetail(String[] args){
		if(sim.getPaused()){
			try{
				int d = Integer.parseInt(args[0]);
				sim.setHistoryDetail(d);
			}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
				System.out.println(ERROR_INVALID_COMMAND);
			}
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void setHistoryGradient(String[] args){
		if(sim.getPaused()){
			try{
				int i = Integer.parseInt(args[0]);
				if(i == 0) sim.setHistoryGradient(false);
				else if(i == 1) sim.setHistoryGradient(true);
				else System.out.println(ERROR_INVALID_COMMAND);
			}catch(ArrayIndexOutOfBoundsException | NumberFormatException | NullPointerException e){
				System.out.println(ERROR_INVALID_COMMAND);
			}
		}else{
			System.out.println(ERROR_SIM_NOT_PAUSED);
		}
	}
	public static void step(){
		sim.step();
	}
	
}