package luisf.ouroboros.analyzer.models.metrics;

public class BraceOpenMetric extends MetricBase {
    private static final String regex = "(\\{)";

    public BraceOpenMetric() {
        super(regex, "");
    }

    public BraceOpenMetric(String text) {
        super(regex, text);
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
