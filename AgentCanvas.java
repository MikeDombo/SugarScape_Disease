import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.stream.IntStream;


//======================================================================
//* This class implements the agent canvas for our agent-based simulation,
//* specifically drawing the grid and (eventually) drawing the agents
//* that will run rampant thereupon.
//======================================================================
class AgentCanvas extends JPanel {
    private int viewportX;  // where in the viewport to start drawing image/grid
    private int viewportY;  // (we want the image centered)

    private int gridWidth;   // width of grid in cells
    private int gridHeight;  // height of grid in cells
    private double maxCapacity; // max resource capacity of any cell

    private static final int agentGUISize = 10;

    private final SimulationManager simulation; // a reference to the simulation object

    //======================================================================
    //* public AgentCanvas()
    //* Constructor for the agent canvas.  Needs a reference to the
    //* simulation manager.
    //======================================================================
    public AgentCanvas(SimulationManager theSimulation) {
        simulation = theSimulation;
        updateGrid();
    }

    //======================================================================
    //* public int getGridWidth()
    //* public int getGridHeight()
    //* Simple accessor methods.
    //======================================================================
    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    //======================================================================
    //* public void changeBackground()
    //* I used to allow the user to select an image background.  Dropped it,
    //* but if we ever go back to it, we should take any existing agents and
    //* reposition at random b/c the image is likely a different size.
    //======================================================================
    public void updateGrid() {
        gridWidth = simulation.getGridSize();
        gridHeight = simulation.getGridSize();

        // may or may not need to have a reset() method in the simulation mgr
        // simulation.reset();  // remove all agents, etc.

        // determine grid's max capacity for scale colors
        this.maxCapacity = 0.0;
        for (int r = 0; r < simulation.gridSize; r++) {
            for (int c = 0; c < simulation.gridSize; c++) {
                double capacity = simulation.landscape.getCellAt(r, c).getCapacity();
                if (capacity > maxCapacity) {
                    this.maxCapacity = capacity;
                }
            }
        }

        // call repaint to redisplay the new background, then agents & grid
        repaint();
    }

    //======================================================================
    //* public void paintComponent(Graphics g)
    //* What happens whenever the agent canvas is (re)drawn.
    //======================================================================
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // safest to create a copy of the graphics component -- one must
        // ensure that no changes are made to the original
        Graphics2D graphics = (Graphics2D) g.create();

        JViewport viewport;
        JScrollPane scrollPane;
        Insets borders;

        int viewportWidth;
        int viewportHeight;
        int agentSize = AgentCanvas.agentGUISize;
        int imageWidth = gridWidth * agentSize;
        int imageHeight = gridHeight * agentSize;

        // make sure that we're grabbing onto the viewport of the scroll pane
        Component ancestor = getParent();
        if (!(ancestor instanceof JViewport)) {
            //Exception e = new Exception(
            //    "AgentCanvas instance must be within JScrollPane instance");
            //e.printStackTrace();
            //return;

            viewportWidth = imageWidth;
            viewportHeight = imageHeight;

            borders = new Insets(0, 0, 0, 0);
        } else {
            // presumably we have the viewport of scroll pane containing 'this'
            viewport = (JViewport) ancestor;

            viewportWidth = viewport.getWidth();
            viewportHeight = viewport.getHeight();

            scrollPane = (JScrollPane) viewport.getParent();
            borders = scrollPane.getInsets();
        }

        // Note that drawImage automatically scales the image to fit that
        // rectangle.
        int renderWidth = gridWidth * agentSize;
        int renderHeight = gridHeight * agentSize;

        // determine the starting (x,y) in the viewport where the image
        // will be drawn
        viewportX = Math.max((viewportWidth - renderWidth) / 2, 0);
        viewportY = Math.max((viewportHeight - renderHeight) / 2, 0);

        // in case there was a previous image, clear things out
        graphics.clearRect(0, 0, viewportWidth, viewportHeight);

        // draw the resource capacities
        drawCapacity(graphics, viewportX, viewportY, renderWidth, renderHeight);

        // now draw the agents; note that the list of agents should be a
        // _protected_ (not private) instance variable within the simulation
        for (Agent a : simulation.agentList) {
            if ((a.getRow() >= 0) && (a.getCol() >= 0) &&
                    ((a.getRow() * agentSize) + agentSize <= renderHeight) &&
                    ((a.getCol() * agentSize) + agentSize <= renderWidth)) {
                int guiX = viewportX + (a.getCol() * agentSize);
                int guiY = viewportY + (a.getRow() * agentSize);

                if (a.isInfected()) {
                    graphics.setPaint(Color.red);
                } else {
                    graphics.setPaint(Color.blue);
                }

                graphics.fillOval(guiX, guiY, agentSize, agentSize);
            }
        }

        // draw the grid last so that it will overlay the agent squares
        drawGrid(graphics, viewportX, viewportY, renderWidth, renderHeight);

        // show the simulation time, number of agents
        drawSimulationInfo(graphics, viewportX, viewportY,
                renderWidth, renderHeight, borders);

        revalidate();

        // get rid of the graphics copy
        graphics.dispose();
    }

    //======================================================================
    //* private void drawCapacity(Graphics2D graphics)
    //======================================================================
    private void drawCapacity(Graphics2D graphics, int x, int y,
                              int width, int height) {


        int cellSize = AgentCanvas.agentGUISize;
        for (int r = 0; r < simulation.gridSize; r++) {
            for (int c = 0; c < simulation.gridSize; c++) {
                int guiX = viewportX + (c * cellSize);
                int guiY = viewportY + (r * cellSize);

                // set the color we'll use to draw the agent -- green scaled relative
                // to maximum landscape capacity
                double capacity = simulation.landscape.getCellAt(r, c).getCapacity();
                Color color = new Color(0, (int) (255 * capacity / this.maxCapacity), 0);
                graphics.setPaint(color);

                graphics.fillRect(guiX, guiY, cellSize, cellSize);
            }
        }

    }


    //======================================================================
    //* private void drawGrid(Graphics2D graphics)
    //* Draw a grid on top of the background and agents.
    //======================================================================
    private void drawGrid(Graphics2D graphics, int x, int y,
                          int width, int height) {
        graphics.setPaint(Color.black);

        // the columns
        int agentSize = AgentCanvas.agentGUISize;
        IntStream.range(0, width / agentSize)
            .forEach(row ->
                graphics.drawLine(x + (row * agentSize), y,
                x + (row * agentSize), y + height - 1)
            );

        // the rows
        IntStream.range(0, height / agentSize)
            .forEach(col ->
                graphics.drawLine(x, y + (col * agentSize),
                x + width - 1, y + (col * agentSize)));

        // the border
        graphics.drawLine(x, y, x, y + height);
        graphics.drawLine(x, y, x + width, y);
        graphics.drawLine(x + width, y, x + width, y + height);
        graphics.drawLine(x, y + height, x + width, y + height);
    }

    //======================================================================
    //* private void drawSimulationInfo(Graphics2D graphics, int x, int y,
    //======================================================================
    private void drawSimulationInfo(Graphics2D graphics, int x, int y,
                                    int width, int height, Insets borders) {
        final int verticalSpaceBeforeText = 20;

        DecimalFormat df = new DecimalFormat("0.00");
        String info = "Time: " + df.format(simulation.getTime()) + "  " +
                "Agents: " + simulation.agentList.size();

        // Find the size of string in the font being used by the current
        // Graphics2D context.
        FontMetrics font = graphics.getFontMetrics();
        Rectangle2D rect = font.getStringBounds(info, graphics);

        int textWidth = (int) (rect.getWidth());
        int textHeight = (int) (rect.getHeight());
        int startStringAt = (width - textWidth) / 2;

        // center text horizontally (max sure left side at least draws w/in
        // the viewport window -- i.e., x at least 0)
        graphics.drawString(info, Math.max(x + startStringAt, 0),
                y + height + verticalSpaceBeforeText);

        // Make sure the image plus text (which may be a new one loaded in) is
        // visible in the scroll pane.  If this isn't somewhere, scrollbars
        // won't work in the main screen's encompassing JScrollPane.
        setPreferredSize(
                new Dimension(
                        Math.max(width + borders.left + borders.right, textWidth),
                        height + borders.top + borders.bottom
                                + verticalSpaceBeforeText + textHeight));

    }

}
