package luisf.ouroboros.analyzer.models.metrics;

public class LineEndMetric extends MetricBase {
    private static final String regex = "(;)";

    public LineEndMetric() {
        super(regex, "");
    }

    public LineEndMetric(String text) {
        super(regex, text);
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
