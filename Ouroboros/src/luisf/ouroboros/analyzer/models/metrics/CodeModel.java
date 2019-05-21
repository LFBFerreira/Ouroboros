package luisf.ouroboros.analyzer.models.metrics;

import java.io.Serializable;

public class CodeModel implements Serializable {
    public final CodeMetricEnum metric;
    public final String text;

    public CodeModel(CodeMetricEnum metric, String text) {
        this.metric = metric;
        this.text = text;
    }
}
