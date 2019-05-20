package luisf.ouroboros.analyzer.models.metrics;

public class BraceCloseMetric extends MetricBase{
    private static final String regex = "(\\})";

    public BraceCloseMetric() {
        super(regex, "");
    }

    public BraceCloseMetric(String text) {
        super(regex, text);
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
