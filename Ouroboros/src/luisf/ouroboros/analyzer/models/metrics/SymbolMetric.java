package luisf.ouroboros.analyzer.models.metrics;

public class SymbolMetric extends MetricBase {
    private static final String regex = "([<>,\\[\\]\\(\\)).=!&])";

    public SymbolMetric() {
        super(regex, "");
    }

    public SymbolMetric(String text) {
        super(regex, text);
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
