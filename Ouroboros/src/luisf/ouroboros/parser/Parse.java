package luisf.ouroboros.parser;

import luisf.ouroboros.common.Handy;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static luisf.ouroboros.parser.RegexConstants.*;
import static luisf.ouroboros.parser.RegexConstants.closeBraces;

public class Parse {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final static Character openBraceCharater = '{';
    private final static Character closeBraceCharater = '}';

    public static String getPackageName(String content) {
        return patternMatcher(content,
                anyChar + "package" + oneOrMoreSpaces + anythingCharGroup + anyWhitespaceChar + semicolon);
    }


    public static String getClassName(String content) {
        return patternMatcher(content,
                anyWord + oneOrMoreSpaces + "class" + oneOrMoreSpaces + "(\\w+)" + "[\\w\\s,]+" + openBracesGroup);
    }

    public static int getClassStartIndex(String content) {
        return patternMatcherStartIndex(content,
                anyWord + oneOrMoreSpaces + "class" + oneOrMoreSpaces + "(\\w+)" + "[\\w\\s,]+" + openBracesGroup,
                2);
    }

    public static String getInterfaceName(String content) {
        return patternMatcher(content,
                anyWord + oneOrMoreSpaces + "interface" + oneOrMoreSpaces + "(\\w+)" + "[\\w\\s,]+" + openBraces);
    }


    public static String getClassMethodsText(String content) {
        return patternMatcher(content,
                anyChar + "class" + anyChar + openBraces + "((" + anyChar + anyWhitespaceChar + ")*)" + closeBraces);
    }


    public static String getMethodName(String code) {
        // regex taken from: https://stackoverflow.com/questions/68633/regex-that-will-match-a-java-method-declaration

        return patternMatcher(code,
                methodVisibilityKeywords + " *([\\w<>.?, \\[\\]]*)" + oneOrMoreSpaces + "(\\w+)" + anyWhitespaceChar +
                        "\\([\\w<>\\[\\]._?, \\n]*\\)" + anyWhitespaceChar + "([\\w ,\\n]*)" + anyWhitespaceChar + "\\{",
                3);
    }

    public static int getMethodStartBrace(String code) {
        // regex taken from: https://stackoverflow.com/questions/68633/regex-that-will-match-a-java-method-declaration

        Pattern pattern = Pattern.compile(methodVisibilityKeywords + " *([\\w<>.?, \\[\\]]*)" + oneOrMoreSpaces + "(\\w+)" + anyWhitespaceChar +
                "\\([\\w<>\\[\\]._?, \\n]*\\)" + anyWhitespaceChar + "([\\w ,\\n]*)" + anyWhitespaceChar + "(\\{)");
        Matcher matcher = pattern.matcher(code);

        if (matcher.find() && code.charAt(matcher.start(5)) == '{') {
            return matcher.start(5);
        } else {
            return -1;
        }
    }

    public static String removeStaticBlocks(String content) {

        String regex = "(static\\s*(\\{))";

        // copy the content, to be modified later
        String filteredContent = content;

        // find index of the start of the static block
        int blockStartIndex = patternMatcherStartIndex(filteredContent, regex, 1);
        int openBraceIndex = patternMatcherStartIndex(filteredContent, regex, 2);
        int blockEndIndex = -1;
        Boolean foundBlock = false;

        while (openBraceIndex > -1) {
            blockEndIndex = findClosingBraceIndex(filteredContent, openBraceIndex);

            if (blockEndIndex != -1) {
                filteredContent = Handy.removeSubString(blockStartIndex, blockEndIndex, filteredContent);
            } else {
                log.severe(Handy.f("Could not find a matching closing brace"));
                break;
            }

            // find new static block if it exists
            blockStartIndex = patternMatcherStartIndex(filteredContent, regex, 1);
            openBraceIndex = patternMatcherStartIndex(filteredContent, regex, 1);
        }

        return filteredContent;
    }

    /**
     * @param content
     * @param openBraceIndex
     * @return
     */
    public static int findClosingBraceIndex(String content, int openBraceIndex) {

        if (openBraceIndex >= content.length() || content.charAt(openBraceIndex) != '{') {
            log.severe("The index of the open brace is incorrect, " + openBraceIndex);
            return -1;
        }

        Boolean isInsideString = false;
        int scopeLevel = 0;

        // analyze character by character
        for (int i = 0; i < content.length(); i++) {
            char charAt = content.charAt(i);

            if (charAt == '"') {
                isInsideString = !isInsideString;
            }

            if (charAt == '\'' && !isInsideString) {
                // skip single quotes if they are not inside a string
                i+=2;
                continue;
            }

            // increase or decrease the scope accordingly, ignoring string occurrences
            if (i >= openBraceIndex && !isInsideString) {
                if (charAt == '{') {
                    scopeLevel++;
                    //log.info(content.substring(i, content.length()));
                } else if (charAt == '}') {
                    scopeLevel--;
                    //log.info(content.substring(i, content.length()));
                    if (scopeLevel == 0) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    /**
     * Extracts all the text inside of the outermost scopes
     *
     * @param content
     * @return
     */
//    public static List<String> getOuterScopesContent(String content) {
//
//        Map<Integer, Character> occurrences = traceScopes(content);
//
//        List<String> methods = new LinkedList<String>();
//
//        if (occurrences.isEmpty()) {
//            log.warning("No braces were found in the code");
//            return methods;
//        }
//
//        int scopeDepth = 0;
//        int lastMethodEndIndex = 0;
//
//        Iterator<Map.Entry<Integer, Character>> iterator = occurrences.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry entry = iterator.next();
//            int characterIndex = (Integer) entry.getKey();
//            Character brace = (Character) entry.getValue();
//
//            // count scope depth
//            if (brace.equals(openBraceCharater)) {
//                scopeDepth++;
//            } else if (brace.equals(closeBraceCharater)) {
//                scopeDepth--;
//            } else {
//                log.warning("Unexpected character in scope tracing!");
//            }
//
//            if (scopeDepth == 0) {
//                methods.add(content.substring(lastMethodEndIndex, characterIndex + 1).trim());
//                lastMethodEndIndex = characterIndex + 1;
//            } else if (scopeDepth < 0) {
//                log.warning("Scope depth is lower then 0. That can't be good!");
//            }
//        }
//
//        return methods;
//    }

    /**
     * Creates an ordered map of the occurrences of opening and closing braces
     *
     * @param content
     * @return
     */
    public static Map<Integer, Character> traceScopes(String content) {
        Map<Integer, Character> occurrences = new LinkedHashMap<Integer, Character>();

        int lastOpenBraceIndex = 0;
        int lastCloseBraceIndex = 0;

        int openBraceSearchStartIndex = 0;
        int closeBraceSearchStartIndex = 0;

        // repeat until there are no more braces in the content
        while (lastOpenBraceIndex != -1 || lastCloseBraceIndex != -1) {
            lastOpenBraceIndex = content.indexOf(openBraceCharater, openBraceSearchStartIndex);
            lastCloseBraceIndex = content.indexOf(closeBraceCharater, closeBraceSearchStartIndex);

            if (lastOpenBraceIndex < lastCloseBraceIndex && lastOpenBraceIndex != -1) {
                occurrences.put(lastOpenBraceIndex, openBraceCharater);
                openBraceSearchStartIndex = lastOpenBraceIndex + 1;
            } else if (lastCloseBraceIndex != -1) {
                occurrences.put(lastCloseBraceIndex, closeBraceCharater);
                closeBraceSearchStartIndex = lastCloseBraceIndex + 1;
            }
        }

        return occurrences;
    }

    /**
     * Extracts the text between the classe's open and closing braces
     *
     * @param content
     * @return
     */
    public static String extractClassContent(String content) {

        int openBraceIndex = getClassStartIndex(content);

        if (openBraceIndex == -1) {
            log.warning("The class starting brace could not be found");
            log.warning(content);
            return "";
        }

        if (content.contains("public class Parse"))
        {
            log.info("This!");
        }

        int closeBraceIndex = findClosingBraceIndex(content, openBraceIndex);

        // increment once, so the open brace is not included in the result
        openBraceIndex++;

        // validate the open and close braces index
        if (openBraceIndex < closeBraceIndex &&
                openBraceIndex > -1 &&
                closeBraceIndex > -1 &&
                openBraceIndex < content.length() &&
                closeBraceIndex < content.length()) {

            return content.substring(openBraceIndex, closeBraceIndex);
        } else {
            log.warning(Handy.f("The open or close brace index is invalid %d - %d", openBraceIndex, closeBraceIndex));
            return content;
        }
    }

    public static Map<String, String> parseMethods(String fileContent) {
        Map<String, String> methodsInfo = new HashMap<String, String>();

        // copy the content, to be modified later
        String partialContent = fileContent;

        String methodName = getMethodName(partialContent);

        if (Handy.isNullOrEmpty(methodName)) {
            return methodsInfo;
        }

        int openBraceIndex = getMethodStartBrace(partialContent);
        int blockEndIndex = -1;

        while (openBraceIndex > -1) {
            blockEndIndex = findClosingBraceIndex(partialContent, openBraceIndex);

            if (blockEndIndex != -1) {
                // get next method name
                methodName = getMethodName(partialContent);

                if (Handy.isNullOrEmpty(methodName)) {
                    return methodsInfo;
                }

                // add into to the map
                methodsInfo.put(methodName, partialContent.substring(openBraceIndex + 1, blockEndIndex));

                // remove analyzed content
                partialContent = Handy.removeSubString(0, blockEndIndex, partialContent);
            } else {
                log.severe(Handy.f("Could not find a matching closing brace"));
                return methodsInfo;
            }

            // find next method if it exists
            openBraceIndex = getMethodStartBrace(partialContent);
        }

        return methodsInfo;
    }

    public static void getFileListRecursive(String folderPath, List<File> files, String extension) {
        File[] topLevelFiles = new File(folderPath).listFiles();

        if (topLevelFiles != null)
            for (File file : topLevelFiles) {
                if (file.isFile() && Handy.getFileExtension(file).equals(extension)) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    getFileListRecursive(file.getAbsolutePath(), files, extension);
                }
            }
    }

    /**
     * Removes single line and multiline comments from the input text
     * from: https://stackoverflow.com/questions/1657066/java-regular-expression-finding-comments-in-code
     *
     * @param content
     * @return
     */
    public static String removeComments(String content) {
        return content.replaceAll("\\/\\*[\\s\\S]*?\\*\\/|([^:]|^)\\/\\/.*", " ").trim();
    }

    /**
     * Finds a match in the input content, using a regular expression.
     * Returns the result of the first group by default
     *
     * @param content
     * @param patternText
     * @return
     */
    private static String patternMatcher(String content, String patternText) {
        return patternMatcher(content, patternText, 1);
    }

    /**
     * Finds a match in the input content, using a regular expression.
     * Returns the result of the selected group
     *
     * @param content
     * @param patternText
     * @param groupIndex
     * @return
     */
    private static String patternMatcher(String content, String patternText, int groupIndex) {
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(groupIndex);
        } else {
            return "";
        }
    }

    private static int patternMatcherStartIndex(String content, String patternText, int groupIndex) {
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.start(groupIndex);
        } else {
            return -1;
        }
    }


}
