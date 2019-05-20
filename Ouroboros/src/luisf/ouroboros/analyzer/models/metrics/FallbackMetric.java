package luisf.ouroboros.analyzer.models.metrics;

public class FallbackMetric extends MetricBase{
    private static final String regex = "";

    public FallbackMetric() {
        super(regex, "");
    }

    public FallbackMetric(String text) {
        super(regex, text);
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
