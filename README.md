# 2D-N-Body-Simulation
A 2D body simulation for testing different algorithms for simulating planetary motion.
Created by: Camodude009 @Felix Fischer
Contact: felix.ma.fischer@gmail.com
Git Repository: https://github.com/camodude009/2D-N-Body-Simulation


1. Starting
This program has to be started from the console (for input).
'java -jar filename.jar'
For easy access, a batch file ('start.bat') has been provided.
For a quick demonstration enter '/load figure8.pl' and '/pause' into the terminal.

2. Input
Commands in the terminal are prefaced with '/'.
Commands being read from a file are placed in separate lines with no preface. Lines starting with '--' will be ignored.
Arguments come after the command separated from the command itself and from each other with ' '.

3. Variables
All time is in s.
Distance in m.
Velocity in m/s.
Acceleration in m/s^2.
Mass in kg.
Density in kg/m^3.
Positive x-direction is to the right.
Positive y-direction is downwards.
Colors for the background and planets (Integer):
0 : black
1 : white
2 : orange
3 : red
4 : turquoise
5 : purple
6 : green
7 : pink
8 : gray-blue
Position, velocity, mass, etc. are stored as Double.
Doubles can be read from formats: '4.2', '32' or '4.7E-11'.
Integers can be read from formats: '46'.

4. Loading and saving
Files (UTF-8 Formatting) are loaded and saved from the place the program was opened

5. Commands
pause
	Pauses/unpauses the sim.
	
addplanet <double x> <double y> <double vX> <double vY> <double m> <double p> <int c>
	x position
	y position
	vX velocity in x direction
	vY velocity in y direction
	m mass in kilograms
	p density (not used for physical calculations, only for size modification)
	c color (see '3. Variables')
	
load <String filename>
	loads a file and executes commands in said file line by line
	
save <String filename>
	creates a file with the commands for replicating the current simulation
	
reset
	resets the whole simulation (including data)
	
speed <double s>
	seconds of in-game simulation/seconds real time
	
stepsize <double s>
	seconds delta t for updating positions
	
algo <int a>
	either '0', '1', or '2' corresponding to Euler, Verlet and RK4
	
grav <String g>/<double g>
	gravitational constant either 'G' for 6.67408E-11 or a custom double
	
scale <double s>
	scale representing the distance from the left edge of the screen to the right
	
realtime <int r>
	either '0' or '1'
	0: graphical output turned off, computer runs as fast as possible
	1: graphical output turned on, respecting 'speed'
	
targettime <double t>
	seconds of simulation time the simulation will run
	completion percentage will be output to the console
	if set to 0, simulation will run endlessly
	
data <int d>
	every step current time, momentum, eKin, ePot, and total energy will be saved internally
	either '0' or '1'
	0: data collection turned off
	1: data collection turned on
	
savedata <String filename>
	saves data collected to a file with the format:
	time(0) momentum(0) eKin(0) ePot(0) energy(0)
	time(1) momentum(1) eKin(1) ePot(1) energy(1)
	...
	
datadetail <int d>
	changes how often data is recorded (to preserve memory)
	saves every d ticks
	
resetdata
	resets internally collected data
	
bg <int c>
	changes background color (see '3. Variables')

historydetail <int d>
	changes how often planet positions are added to the history (to preserve memory)
	saves every d ticks
	
history <int h>
	seconds of in-game simulation to be kept in history (for drawing planet paths)
	0 will turn this off
	-1 will set the program to infinitely save planet positions

historygradient <int g>
	gradient drawing of planet paths from planet color to background color (very time intensive!)
	either '0' or '1'
	0: gradient turned off
	1: gradient turned on
	
6. Example file (Figure-8, 3-Body Choreography)
--this is a comment and will be ignored
realtime 1
targettime 0.0
algo 1
grav 1.0
scale 3.0
speed 1.0
stepsize 0.01
history 3.0
historydetail 1
historygradient 1
bg 0
addplanet 0.97000436 -0.24308753 0.466203685 0.43236573 1.0 0.0005 2
addplanet -0.97000436 0.24308753 0.466203685 0.43236573 1.0 0.0005 3
addplanet 0.0 0.0 -0.93240737 -0.86473146 1.0 0.0005 4
