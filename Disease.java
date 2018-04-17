public class Disease {
    private String genome;
    private double metabolicPenalty;

    public Disease(String genome, double metabolicPenalty) {
        this.genome = genome;
        this.metabolicPenalty = metabolicPenalty;
    }

    public String getGenome() {
        return genome;
    }

    public double getMetabolicPenalty() {
        return metabolicPenalty;
    }
}
