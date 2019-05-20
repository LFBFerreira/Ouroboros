package luisf.ouroboros.analyzer.models.metrics;

public class ReturnMetric extends MetricBase {
    private static final String regex = "(return)";

    public ReturnMetric() {
        super(regex, "");
    }

    public ReturnMetric(String text) {
        super(regex, text);
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
