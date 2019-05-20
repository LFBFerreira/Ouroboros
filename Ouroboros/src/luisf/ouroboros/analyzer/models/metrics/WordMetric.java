package luisf.ouroboros.analyzer.models.metrics;

public class WordMetric extends MetricBase {

    private static final String regex = "(\\b\\w*\\b)";

    public WordMetric() {
        super(regex, "");
    }


    public WordMetric(String text) {
        super(regex, text);
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
