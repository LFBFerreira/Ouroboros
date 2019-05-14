package luisf.ouroboros.parser;

public class RegexConstants {

    public static String oneOrMoreSpaces = "\\s+";
    public static String anyChar = ".*";
    public static String anyWhitespaceChar = "\\s*";
    public static String anythingCharGroup = "(" + anyChar + ")";

    public static String openBraces = "\\{";
    public static String openBracesGroup = "(\\{)";
    public static String closeBraces = "\\}";
    public static String semicolon = "\\;";

    public static String methodVisibilityKeywords = "(public|private|static|protected|abstract|native|synchronized)";

    public static String atLeastOneSpace = "\\s+";

    public static String anyWord = "\\w*";

}
