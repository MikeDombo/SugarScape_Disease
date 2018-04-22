public class Event implements Comparable {
    private final double time;
    private final String type;
    private final String target;

    public Event(double time, String type, String target) {
        this.time = time;
        this.type = type;
        this.target = target;
    }


    public String getType() {
        return type;
    }

    public double getTime() {
        return time;
    }

    @Override
    public int compareTo(Object o) {
        assert o instanceof Event;
        return Double.compare(this.time, ((Event) o).time);
    }

    public String getTarget() {
        return target;
    }
}
