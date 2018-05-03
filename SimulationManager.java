import squint.*;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.*;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class SimulationManager extends GUIManager {
    public static int numHealthy = 0;
    private Random rng;

    /*
     * Parameters to control diseases and agent response to diseases
     */
    private static final int MAX_DISEASES = 12;
    private final IntSupplier nextDiseaseGenomeLength = () -> {
        return rng.nextInt(10) + 1;
    };
    private final DoubleSupplier nextDiseaseMetabolicPenalty = () -> {
        return rng.nextDouble() + 1;
    };
    private final IntSupplier nextAgentImmuneLength = () -> {
        return 50;
    };
    private final boolean SHOW_CURRENT_CELL_RESOURCE_LEVEL = true;

    private Event getNewMutate(double time, String target) {
        return new Event(time + uniform(3, 7), "mutate", target);
    }
    private Event getNewImmuneResponse(double time, String target) {
        return new Event(time + Math.abs(rng.nextGaussian()+1), "immuneResponse", target);
    }

    /*
     * Agent parameters
     */
    private final IntSupplier nextAgentVision = () -> rng.nextInt(6) + 1;
    private final DoubleSupplier nextAgentMetabolicRate = () -> uniform(1, 4);
    private final DoubleSupplier nextAgentInitialWealth = () -> uniform(5, 25);
    private final DoubleSupplier nextAgentMaxAge = () -> uniform(60, 100);

    final ArrayList<Agent> agentList;
    final Landscape landscape;
    final int gridSize;
    private AgentCanvas canvas;  // the canvas on which agents are drawn
    private int nextAgentID = 0;
    private final PriorityQueue<Event> eventCalendar = new PriorityQueue<>();
    private double time;  // the simulation time

    private final double maxTime;
    private final boolean showGraphics;
    private final int initialSeed;

    private double uniform(int a, int b) {
        return (rng.nextDouble() * (b - a)) + a;
    }

    private double exponential(double rate) {
        return Math.log(1 - rng.nextDouble()) / (-rate);
    }

    private int rand01() {
        return rng.nextInt(2);
    }

    private String rand01String(int length) {
        return IntStream.range(0, length)
                        .mapToObj(i -> String.valueOf(rand01()))
                        .collect(Collectors.joining());
    }

    //======================================================================
    //* public SimulationManager(int gridSize, int numAgents, int initialSeed)
    //======================================================================
    public SimulationManager(int gridSize, int numAgents, int initialSeed) {
        this(gridSize, numAgents, initialSeed, 0, true);
    }
    public SimulationManager(int gridSize, int numAgents, int initialSeed, double maxTime) {
        this(gridSize, numAgents, initialSeed, maxTime, true);
    }

    public SimulationManager(int gridSize, int numAgents, int initialSeed, double maxTime, boolean graphics) {
        this.maxTime = maxTime;
        this.showGraphics = graphics;

        if (numAgents >= (gridSize * gridSize)) {
            System.err.println("Too many agents for the given gridSize!");
            System.exit(1);
        }

        this.initialSeed = initialSeed;
        rng = new Random(initialSeed);
        this.landscape = new Landscape(gridSize, rng);

        this.gridSize = gridSize;
        this.agentList = new ArrayList<>();

        this.time = 0;   // initialize the simulation clock

        // Generate all the agents
        IntStream.range(0, numAgents).forEach(i -> generateAgent());

        // Generate all diseases that will exist
        ArrayList<Disease> diseaseList = IntStream.range(0, MAX_DISEASES)
                .mapToObj(i -> new Disease(rand01String(nextDiseaseGenomeLength.getAsInt()), nextDiseaseMetabolicPenalty.getAsDouble()))
                .collect(Collectors.toCollection(ArrayList::new));

        // Infect each agent with a random disease
        agentList.forEach(a -> a.infectWith(diseaseList.get(rng.nextInt(diseaseList.size()))));

        if(showGraphics) {
            this.createWindow();
        }

        eventCalendar.add(new Event(0, "repaint", "all"));
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
        // default window width and height defined as constants
        int WINDOW_WIDTH = 500;
        int WINDOW_HEIGHT = 500;
        this.createWindow(WINDOW_WIDTH, WINDOW_HEIGHT);
        contentPane.setLayout(new BorderLayout()); // java.awt.*

        canvas = new AgentCanvas(this, SHOW_CURRENT_CELL_RESOURCE_LEVEL);
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
        Agent a = new Agent("agent " + (nextAgentID++), nextAgentVision.getAsInt(),
                nextAgentMetabolicRate.getAsDouble(), nextAgentInitialWealth.getAsDouble(),
                nextAgentMaxAge.getAsDouble(), this.time, rand01String(nextAgentImmuneLength.getAsInt()));
        agentList.add(a);

        int[] nextUnoccupied = getNewUnoccupiedCell(gridSize);

        a.setRowCol(nextUnoccupied[0], nextUnoccupied[1]);
        landscape.getCellAt(nextUnoccupied[0], nextUnoccupied[1]).setOccupied(true);

        a.scheduleNewEvent(new Event(this.time + exponential(1), "move", a.getID()));
        a.scheduleNewEvent(getNewMutate(this.time, a.getID()));
        a.scheduleNewEvent(getNewImmuneResponse(this.time, a.getID()));
        eventCalendar.add(a.getNextEvent());

        return a;
    }

    //======================================================================
    //* public void run()
    //* This is where your main simulation event engine code should go...
    //======================================================================
    public void run() {
        while (!eventCalendar.isEmpty() && (maxTime == 0 || time < maxTime)) {
            Event next = eventCalendar.poll();
            assert next != null;
            switch (next.getType()) {
                case "move":
                    this.time = next.getTime();
                    for (Agent agent : agentList) {
                        if (agent.getID().equals(next.getTarget())) {
                            // Move
                            agent.move(landscape, agentList, rng, this.time);
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
                    break;
                case "death":
                    String deathID = next.getTarget();
                    agentList.stream()
                            .filter(a -> a.getID().equals(deathID))
                            .forEach(a -> {
                                Cell c = landscape.getCellAt(a.getRow(), a.getCol());
                                a.collectResources(c, this.time);
                                c.setOccupied(false);
                            });
                    agentList.removeIf(a -> a.getID().equals(deathID));
                    eventCalendar.removeIf(e -> e.getTarget().equals(deathID));
                    generateAgent();
                    break;
                case "mutate": {
                    String aID = next.getTarget();
                    for (Agent a : agentList) {
                        if (a.getID().equals(aID)) {
                            a.randomMutateImmuneSystem(rng);

                            a.scheduleNewEvent(getNewMutate(this.time, aID));
                            eventCalendar.add(a.getNextEvent());
                            break;
                        }
                    }
                    break;
                }
                case "immuneResponse": {
                    String aID = next.getTarget();
                    for (Agent a : agentList) {
                        if (a.getID().equals(aID)) {
                            a.immuneResponse(true);

                            a.scheduleNewEvent(getNewImmuneResponse(this.time, aID));
                            eventCalendar.add(a.getNextEvent());
                            break;
                        }
                    }
                    break;
                }
                case "repaint": {
                    if(showGraphics) {
                        canvas.repaint();
                        eventCalendar.add(new Event(this.time + 0.05, "repaint", "all"));
                        try {
                            Thread.sleep(100);
                        } catch (Exception ignored) {
                        }
                    }
                    break;
                }
            }
        }

        if(!showGraphics){
            System.out.println(agentList.stream().filter(a -> !a.isInfected()).count() + "," + agentList.stream().filter(Agent::isInfected).count());
            numHealthy += agentList.stream().filter(a -> !a.isInfected()).count();
        }
    }


    //======================================================================
    //* public static void main(String[] args)
    //* Just including main so that the simulation can be executed from the
    //* command prompt.  Note that main just creates a new instance of this
    //* class, which will start the GUI window and then we're off and
    //* running...
    //======================================================================
    public static void main(String[] args) {
        if(args.length == 0){
            new SimulationManager(40, 400, 8675309, 0, true);
        }
        else {
            Random tRand = new Random(8675309);
            IntStream.range(0, 50)
                    .forEach(i ->
                            new SimulationManager(40, 400, tRand.nextInt(Integer.MAX_VALUE), 100, false)
                    );
            System.out.println("Average Number of Healthy Agents: " + Math.round(numHealthy/50.0));
        }
    }
}
