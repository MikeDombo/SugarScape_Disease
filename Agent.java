import java.util.*;

public class Agent {
    private String immuneSystem;
    private HashSet<Disease> infectedWith = new HashSet<>();
    private HashSet<Disease> carrying = new HashSet<>();

    private double birthTime;
    private String id;   // identifier for the agent
    private int row;
    private int col;
    private int vision;
    private double metabolicRate;
    private double wealth;
    private double maxAge;
    private double lastCollectedResources;
    private PriorityQueue<Event> eventList = new PriorityQueue<>();

    public Agent(String id, int vision, double metabolicRate, double initialWealth, double maxAge, double birthTime, String immuneSystem) {
        this.id = id;
        this.vision = vision;
        this.metabolicRate = metabolicRate;
        this.wealth = 0;
        this.maxAge = maxAge;
        this.wealth = initialWealth;
        this.birthTime = birthTime;
        this.lastCollectedResources = birthTime;
        this.immuneSystem = immuneSystem;

        // Schedule death
        eventList.add(new Event(birthTime + maxAge, "death", id));
    }

    public void immuneResponse(){
        // Find minimum hamming distance and then change our immune system to attack the first disease
        // If we get the distance to 0, then move the disease to carrying and subtract the metabolic penalty
        // Check diseases that we are carrying and see if we are infected again since the immune system was changed
    }

    public void infectWith(Disease d){
        if(!carrying.contains(d) // || we are not immune
        ){
            infectedWith.add(d);
            metabolicRate += d.getMetabolicPenalty();
        }
    }

    public void scheduleNewEvent(Event e){
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

    public void collectResources(Cell c, double time){
        this.wealth = Math.max(0, this.wealth + c.removeResources(time) - (this.metabolicRate * (time - lastCollectedResources)));
        lastCollectedResources = time;
    }

    public void move(Landscape landscape, double time){
        ArrayList<Cell> lookNorth = new ArrayList<>();
        ArrayList<Cell> lookSouth = new ArrayList<>();
        ArrayList<Cell> lookEast = new ArrayList<>();
        ArrayList<Cell> lookWest = new ArrayList<>();

        for(int i = this.row; i < this.row + this.vision; i++){
            lookSouth.add(landscape.getCellAt(i, this.col));
        }
        for(int i = this.row; i > this.row - this.vision; i--){
            lookNorth.add(landscape.getCellAt(i, this.col));
        }
        for(int i = this.col; i < this.col + this.vision; i++){
            lookEast.add(landscape.getCellAt(this.row, i));
        }
        for(int i = this.col; i > this.col - this.vision; i--){
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
        if(allOptions.size() <= 0){
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
        if(allOptions.size() > 1){
            double minDist = Double.POSITIVE_INFINITY;
            ArrayList<Cell> minDistCells = new ArrayList<>();
            for(Cell c : allOptions){
                int dist = distance(c, landscape);
                if(dist == minDist){
                    minDistCells.add(c);
                }
                if(dist < minDist){
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
    }

    public int distance(Cell c, Landscape l){
        int size = l.getGridSize();
        int x = c.getRow();
        int y = c.getCol();

        if(x == this.row){
            return Math.min(Math.abs(this.col - y), Math.abs(this.col - y - size));
        }
        else if(y == this.col){
            return Math.min(Math.abs(this.row - x), Math.abs(this.row - x - size));
        }

        return -1;
    }

    public double getWealth() {
        return wealth;
    }

    public double getMaxAge() {
        return this.maxAge;
    }

    public double getMetabolicRate() { return this.metabolicRate; }

    public double getBirthTime() {
        return birthTime;
    }
}

