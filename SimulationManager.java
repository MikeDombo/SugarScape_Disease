import squint.*;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.*;

class SimulationManager extends GUIManager {
    // default window width and height defined as constants
    private final int WINDOW_WIDTH = 500;
    private final int WINDOW_HEIGHT = 500;

    protected ArrayList<Agent> agentList;
    protected Landscape landscape;
    protected int gridSize;
    private AgentCanvas canvas;  // the canvas on which agents are drawn
    private Random rng;
    private int nextAgentID = 0;
    private PriorityQueue<Event> eventCalendar = new PriorityQueue<>();
    private double time;  // the simulation time

    private double uniform(int a, int b) {
        return (rng.nextDouble() * (b - a)) + a;
    }

    private double exponential(double lambda) {
        double ret = Math.log(1 - rng.nextDouble()) / (-lambda);
        return ret;
    }

    private int rand01(){
        return rng.nextInt(2);
    }

    private String rand01String(int length){
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < length; i++){
            s.append(rand01());
        }
        return s.toString();
    }

    //======================================================================
    //* public SimulationManager(int gridSize, int numAgents, int initialSeed)
    //======================================================================
    public SimulationManager(int gridSize, int numAgents, int initialSeed) {
        if (numAgents >= (gridSize * gridSize)) {
            System.err.println("Too many agents for the given gridSize!");
            System.exit(1);
        }

        rng = new Random(initialSeed);
        this.landscape = new Landscape(gridSize, rng);

        this.gridSize = gridSize;
        this.agentList = new ArrayList<>();

        this.time = 0;   // initialize the simulation clock

        for (int i = 0; i < numAgents; i++) {
            generateAgent();
        }

        this.createWindow();
        this.run();
    }

    public int[] getNewUnoccupiedCell(int gridSize) {
        int ret[] = new int[2];
        int row = rng.nextInt(gridSize); // an int in [0, gridSize-1]
        int col = rng.nextInt(gridSize); // an int in [0, gridSize-1]

        while (landscape.getCellAt(row, col).isOccupied()) {
            row = rng.nextInt(gridSize); // an int in [0, gridSize-1]
            col = rng.nextInt(gridSize); // an int in [0, gridSize-1]
        }

        ret[0] = row;
        ret[1] = col;
        return ret;
    }

    //======================================================================
    //* public void createWindow()
    //======================================================================
    public void createWindow() {
        this.createWindow(WINDOW_WIDTH, WINDOW_HEIGHT);
        contentPane.setLayout(new BorderLayout()); // java.awt.*

        canvas = new AgentCanvas(this);
        contentPane.add(new JScrollPane(canvas), BorderLayout.CENTER);
    }

    // simple accessor methods
    public int getGridSize() {
        return this.gridSize;
    }

    public double getTime() {
        return this.time;
    }

    private Agent generateAgent() {
        Agent a = new Agent("agent " + (nextAgentID++), rng.nextInt(6) + 1,
                uniform(1, 4), uniform(5, 25), uniform(60, 100), this.time);
        agentList.add(a);

        int[] nextUnoccupied = getNewUnoccupiedCell(gridSize);

        a.setRowCol(nextUnoccupied[0], nextUnoccupied[1]);
        landscape.getCellAt(nextUnoccupied[0], nextUnoccupied[1]).setOccupied(true);

        a.scheduleNewEvent(new Event(this.time + exponential(1), "move", a.getID()));
        eventCalendar.add(a.getNextEvent());

        return a;
    }

    //======================================================================
    //* public void run()
    //* This is where your main simulation event engine code should go...
    //======================================================================
    public void run() {
        while (!eventCalendar.isEmpty()) {
            Event next = eventCalendar.poll();
            assert next != null;
            if (next.getType().equals("move")) {
                this.time = next.getTime();
                for (Agent agent : agentList) {
                    if (agent.getID().equals(next.getTarget())) {
                        // Move
                        agent.move(landscape, this.time);
                        // Eat, deplete cell resources, compute wealth from resouces and metabolic rate
                        Cell currentCell = landscape.getCellAt(agent.getRow(), agent.getCol());
                        agent.collectResources(currentCell, this.time);

                        // Schedule Next Move
                        double tNext = this.time + exponential(1);

                        // Compute resources used and regrown to see if the agent will die due to lost wealth
                        double metabolicRate = agent.getMetabolicRate();
                        double regrowthRate = currentCell.getRegrowthRate();
                        double realRate = (regrowthRate - metabolicRate);
                        double wealthAtTNext = agent.getWealth() + (realRate * (tNext - this.time));
                        if (realRate < 0 && wealthAtTNext <= 0) {
                            // DIE
                            double deathTime = this.time - (agent.getWealth() / realRate);
                            agent.scheduleNewEvent(new Event(deathTime, "death", agent.getID()));
                        } else {
                            // If still living, schedule next move
                            agent.scheduleNewEvent(new Event(tNext, "move", agent.getID()));
                        }
                        // Add whatever the next event is to the global calendar
                        eventCalendar.add(agent.getNextEvent());
                        break;
                    }
                }
            } else if (next.getType().equals("death")) {
                String deathID = next.getTarget();
                for(Agent a : agentList){
                    if(a.getID().equals(deathID)){
                        Cell c = landscape.getCellAt(a.getRow(),a.getCol());
                        a.collectResources(c, this.time);
                        c.setOccupied(false);
                    }
                }
                agentList.removeIf(a -> a.getID().equals(deathID));
                eventCalendar.removeIf(e -> e.getTarget().equals(deathID));
                generateAgent();
            }

            canvas.repaint();

            try {
                Thread.sleep(5);
            } catch (Exception ignored) {
            }
        }
        System.out.println("No more events");
    }


    //======================================================================
    //* public static void main(String[] args)
    //* Just including main so that the simulation can be executed from the
    //* command prompt.  Note that main just creates a new instance of this
    //* class, which will start the GUI window and then we're off and
    //* running...
    //======================================================================
    public static void main(String[] args) {
        new SimulationManager(40, 400, 8675309);
    }
}
