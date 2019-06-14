package luisf.ouroboros.models.metrics;

import java.io.Serializable;
import java.util.logging.Logger;

public class Metric implements Serializable {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public final String regex;
    public final CodeMetricEnum metric;


    // ================================================================

    /**
     * Constructor
     *
     * @param metric
     */
    public Metric(CodeMetricEnum metric) {
        this.metric = metric;
        this.regex = codeMetricToPattern(metric);
    }


    // ================================================================

    // Helpers

    private String codeMetricToPattern(CodeMetricEnum metric) {
        String pattern = "";

        switch (metric) {
            case BRACE_OPEN:
                pattern = "(\\{)";
                break;
            case BRACE_CLOSE:
                pattern = "(\\})";
                break;
            case LINE_END:
                pattern = "(;)";
                break;
            case RETURN:
                pattern = "(return)";
                break;
            case DOT:
                pattern = "(\\.)";
                break;
            case OTHER_SYMBOL:
                pattern = "([<>,\\[\\]\\(\\))=!&])";
                break;
            case WORD:
                pattern = "(\\b\\w*\\b)";
                break;
            default:
                log.warning("Could not match the metric");
        }

        return pattern;
    }
}
