package luisf.ouroboros.analyzer.models.metrics;

import java.io.Serializable;

public abstract class MetricBase implements Serializable {
    public String text = "";
    public String regex = "";


    public MetricBase(String regex, String text) {
        this.text = text;
        this.regex = regex;
    }

    public abstract String getRegex();

}
