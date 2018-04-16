public class Cell implements Comparable{

    private double resourceCapacity;
    private double regrowthRate;
    private double lastTimeDepleted = 0;
    private boolean occupied;
    private int x;
    private int y;

    public Cell(double regrowthRate, double resourceCapacity, int x, int y){
        this.resourceCapacity = resourceCapacity;
        this.regrowthRate = regrowthRate;
        this.x = x;
        this.y = y;
    }

    public double getRegrowthRate() { return this.regrowthRate; }

    public int getRow() {
        return this.x;
    }

    public int getCol() {
        return this.y;
    }

    public double getCapacity(){
        return this.resourceCapacity;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public double getResourceLevel(double time) {
        return Math.min(this.resourceCapacity, (time-lastTimeDepleted) * this.regrowthRate);
    }

    public double removeResources(double time){
        double eaten = getResourceLevel(time);
        lastTimeDepleted = time;
        return eaten;
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof Cell)){
            return 0;
        }
        if(this.resourceCapacity == ((Cell) o).getCapacity()){
            return 0;
        }
        return (this.getCapacity() > ((Cell) o).getCapacity()) ? 1 : -1;
    }
}
