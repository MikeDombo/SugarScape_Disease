import java.util.*;

public class Agent {
    private String immuneSystem;
    private final HashSet<Disease> infectedWith = new HashSet<>();
    private final HashSet<Disease> carrying = new HashSet<>();

    private final String id;   // identifier for the agent
    private int row;
    private int col;
    private final int vision;
    private double metabolicRate;
    private double wealth;
    private double lastCollectedResources;
    private final PriorityQueue<Event> eventList = new PriorityQueue<>();

    public Agent(String id, int vision, double metabolicRate, double initialWealth, double maxAge, double birthTime, String immuneSystem) {
        this.id = id;
        this.vision = vision;
        this.metabolicRate = metabolicRate;
        this.wealth = 0;
        this.wealth = initialWealth;
        this.lastCollectedResources = birthTime;
        this.immuneSystem = immuneSystem;

        // Schedule death
        eventList.add(new Event(birthTime + maxAge, "death", id));
    }

    public boolean isInfected(){
        return !this.infectedWith.isEmpty();
    }

    private boolean immuneTo(Disease d) {
        return HammingDistance.getMinHammingDistance(immuneSystem, d.getGenome())[0] == 0;
    }

    public void immuneResponse(boolean change) {
        if (change && infectedWith.size() > 0) {
            immuneSystem = HammingDistance.closerByOne(immuneSystem, ((Disease) infectedWith.toArray()[0]).getGenome());
        }
        ArrayList<Disease> toRemove = new ArrayList<>();
        infectedWith.stream().filter(this::immuneTo).forEach(d -> {
            metabolicRate -= d.getMetabolicPenalty();
            carrying.add(d);
            toRemove.add(d);
        });
        toRemove.forEach(infectedWith::remove);
    }

    public void randomMutateImmuneSystem(Random rng) {
        int i = rng.nextInt(immuneSystem.length());
        if (immuneSystem.charAt(i) == '0') {
            immuneSystem = replaceAt(immuneSystem, i, '1');
        } else {
            immuneSystem = replaceAt(immuneSystem, i, '0');
        }

        immuneResponse(false);
    }

    private String replaceAt(String a, int location, char replacement) {
        String s = a.substring(0, location);
        s += replacement;
        s += a.substring(location + 1);
        return s;
    }

    public void infectWith(Disease d) {
        if (d != null && !immuneTo(d)) {
            infectedWith.add(d);
            metabolicRate += d.getMetabolicPenalty();
            carrying.remove(d);
        }
    }

    public void scheduleNewEvent(Event e) {
        eventList.add(e);
    }

    public Event getNextEvent() {
        return eventList.poll();
    }

    // simple accessor methods below
    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public String getID() {
        return this.id;
    }

    // simple mutator methods below
    public void setRowCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void collectResources(Cell c, double time) {
        this.wealth = Math.max(0, this.wealth + c.removeResources(time) - (this.metabolicRate * (time - lastCollectedResources)));
        lastCollectedResources = time;
    }

    public void move(Landscape landscape, ArrayList<Agent> agentList, Random rng, double time) {
        ArrayList<Cell> lookNorth = new ArrayList<>();
        ArrayList<Cell> lookSouth = new ArrayList<>();
        ArrayList<Cell> lookEast = new ArrayList<>();
        ArrayList<Cell> lookWest = new ArrayList<>();

        for (int i = this.row; i < this.row + this.vision; i++) {
            lookSouth.add(landscape.getCellAt(i, this.col));
        }
        for (int i = this.row; i > this.row - this.vision; i--) {
            lookNorth.add(landscape.getCellAt(i, this.col));
        }
        for (int i = this.col; i < this.col + this.vision; i++) {
            lookEast.add(landscape.getCellAt(this.row, i));
        }
        for (int i = this.col; i > this.col - this.vision; i--) {
            lookWest.add(landscape.getCellAt(this.row, i));
        }

        lookNorth.removeIf(Cell::isOccupied);
        lookSouth.removeIf(Cell::isOccupied);
        lookEast.removeIf(Cell::isOccupied);
        lookWest.removeIf(Cell::isOccupied);

        ArrayList<Cell> allOptions = new ArrayList<>();
        allOptions.addAll(lookNorth);
        allOptions.addAll(lookSouth);
        allOptions.addAll(lookEast);
        allOptions.addAll(lookWest);

        // Nowhere to move, just stay put
        if (allOptions.size() <= 0) {
            return;
        }

        // Sort by resource level
        Collections.sort(allOptions);
        // Reverse so that the highest resource level is first
        Collections.reverse(allOptions);
        // Keep duplicate highest resource levels, but remove any cells that are less than the highest
        double maxResourceLevel = allOptions.get(0).getResourceLevel(time);
        allOptions.removeIf(c -> c.getResourceLevel(time) < maxResourceLevel);

        Cell selectedCell = allOptions.get(0);
        if (allOptions.size() > 1) {
            double minDist = Double.POSITIVE_INFINITY;
            ArrayList<Cell> minDistCells = new ArrayList<>();
            for (Cell c : allOptions) {
                int dist = distance(c, landscape);
                if (dist == minDist) {
                    minDistCells.add(c);
                }
                if (dist < minDist) {
                    minDist = dist;
                    minDistCells.clear();
                    minDistCells.add(c);
                }
            }

            // Randomly choose from the options if there is more than 1
            selectedCell = minDistCells.get(landscape.getRng().nextInt(minDistCells.size()));
        }

        // Make the move
        landscape.getCellAt(this.row, this.col).setOccupied(false);
        selectedCell.setOccupied(true);
        this.setRowCol(selectedCell.getRow(), selectedCell.getCol());

        ArrayList<Cell> neighbors = new ArrayList<>();
        neighbors.add(landscape.getCellAt(this.row + 1, this.col));
        neighbors.add(landscape.getCellAt(this.row - 1, this.col));
        neighbors.add(landscape.getCellAt(this.row, this.col + 1));
        neighbors.add(landscape.getCellAt(this.row, this.col - 1));

        ArrayList<Disease> newDiseases = new ArrayList<>();

        agentList.forEach(neighbor ->
                neighbors.stream()
                        .filter(c -> neighbor.getCol() == c.getCol() && neighbor.getRow() == c.getRow())
                        .forEach(c -> {
                            neighbor.infectWith(getRandomDisease());
                            newDiseases.add(neighbor.getRandomDisease());
                        })
        );

        if (newDiseases.size() > 0) {
            infectWith(newDiseases.get(rng.nextInt(newDiseases.size())));
        }
    }

    private Disease getRandomDisease() {
        ArrayList<Disease> myDiseases = new ArrayList<>();
        myDiseases.addAll(infectedWith);
        myDiseases.addAll(carrying);
        if (myDiseases.size() <= 0) {
            return null;
        }
        Random rng = new Random();
        return myDiseases.get(rng.nextInt(myDiseases.size()));
    }

    public int distance(Cell c, Landscape l) {
        int size = l.getGridSize();
        int x = c.getRow();
        int y = c.getCol();

        if (x == this.row) {
            return Math.min(Math.abs(this.col - y), Math.abs(this.col - y - size));
        } else if (y == this.col) {
            return Math.min(Math.abs(this.row - x), Math.abs(this.row - x - size));
        }

        return -1;
    }

    public double getWealth() {
        return wealth;
    }

    public double getMetabolicRate() {
        return this.metabolicRate;
    }
}

