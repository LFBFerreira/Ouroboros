package luisf.ouroboros.analyzer;

public class RegexConstants {

    public static String anyChar = ".*";

    public static String oneOrMoreWhitespaces = "\\s+";
    public static String anyWhitespaces = "\\s*";

    public static String anythingCharGroup = "(" + anyChar + ")";

    public static String openBraces = "\\{";
    public static String openBracesGroup = "(\\{)";

    public static String closeBraces = "\\}";

    public static String semicolon = "\\;";

    public static String modifierKeywordsGroup = "(public|private|static|protected|abstract|native|synchronized|final| )";

    public static String typeDeclarationGroup = "([\\w<>.?, \\[\\]]*)";

    public static String anyWord = "\\w*";
    public static String anyWordGroup = "(\\w*)";
    public static String atleastOneWordGroup = "(\\w+)";

}
