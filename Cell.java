public class Cell implements Comparable<Cell> {

    private final double resourceCapacity;
    private final double regrowthRate;
    private double lastTimeDepleted;
    private boolean occupied;
    private final int x;
    private final int y;

    public Cell(double regrowthRate, double resourceCapacity, int x, int y) {
        this.resourceCapacity = resourceCapacity;
        this.regrowthRate = regrowthRate;
        this.x = x;
        this.y = y;
        this.lastTimeDepleted = 0;
    }

    public double getRegrowthRate() {
        return this.regrowthRate;
    }

    public int getRow() {
        return this.x;
    }

    public int getCol() {
        return this.y;
    }

    public double getCapacity() {
        return this.resourceCapacity;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public double getResourceLevel(double time) {
        if(lastTimeDepleted == 0){
            return this.resourceCapacity;
        }
        return Math.min(this.resourceCapacity, (time - lastTimeDepleted) * this.regrowthRate);
    }

    public double removeResources(double time) {
        double eaten = getResourceLevel(time);
        lastTimeDepleted = time;
        return eaten;
    }

    @Override
    public int compareTo(Cell o) {
        if (this.resourceCapacity == ((Cell) o).getCapacity()) {
            return 0;
        }
        return (this.getCapacity() > ((Cell) o).getCapacity()) ? 1 : -1;
    }
}
