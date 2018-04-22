import java.util.ArrayList;
import java.util.Random;

public class Landscape {
    private final ArrayList<ArrayList<Cell>> landscape;
    private final int size;
    private final Random rng;

    public Landscape(int gridSize, Random rng) {
        this.size = gridSize;
        this.rng = rng;
        landscape = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            ArrayList<Cell> cellList = new ArrayList<>();
            for (int j = 0; j < gridSize; j++) {
                Cell c = new Cell(1, makeResourceCapacity(i, j, gridSize, gridSize), i, j);
                cellList.add(c);
            }
            landscape.add(cellList);
        }

    }

    private double makeResourceCapacity(int i, int j, int bigX, int bigY) {
        return fx(i - bigX / 4, j - bigY / 4, bigX, bigY) + fx(i - (3 * bigX) / 4, j - (3 * bigY) / 4, bigX, bigY);
    }

    private double fx(int x, int y, int bigX, int bigY) {
        double psi = 4.0;
        double thetaX = .3 * bigX;
        double thetaY = 0.3 * bigY;
        return psi * Math.exp(-Math.pow((x / thetaX), 2) - Math.pow((y / thetaY), 2));
    }

    public Cell getCellAt(int row, int col) {
        // Wrap around bottom/top and sides
        int totalRows = this.landscape.size();
        if (row >= totalRows) {
            row = row % totalRows;
        } else if (row < 0) {
            row = (totalRows + row) % totalRows;
        }

        int totalColumns = this.landscape.get(0).size();
        if (col >= totalColumns) {
            col = col % totalColumns;
        } else if (col < 0) {
            col = (totalColumns + col) % totalColumns;
        }

        return this.landscape.get(row).get(col);
    }

    public int getGridSize() {
        return size;
    }

    public Random getRng() {
        return rng;
    }
}
