package luisf.ouroboros.parser;

import luisf.ouroboros.common.Handy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
                anyChar + "package" + oneOrMoreSpaces + anythingCharGroup + oneOrMoreSpaces + semicolon);
    }

    public static String getClassName(String content) {
        return patternMatcher(content,
                anyChar + "class" + oneOrMoreSpaces + anythingCharGroup + oneOrMoreSpaces + openBraces);
    }

    public static String getClassCode(String content) {
        return patternMatcher(content,
                anyChar + "class" + anyChar + openBraces + "((" + anyChar + anyWhitespaceChar + ")*)" + closeBraces);
    }

    public static String getMethodName(String code)
    {
        String uncommentedCode = removeComments(code).trim();

        return patternMatcher(code,
                methodVisibilityKeywords + atLeastOneSpace + "([a-zA-Z0-9<>._?, ]*)" + atLeastOneSpace + "(a-zA-Z0-9) *\\([a-zA-Z0-9<>\\[\\]._?, \\n]*\\) *([a-zA-Z0-9_ ,\\n]*) *\\{",
                3);
    }

    /**
     * Extracts a list of plain text methods from a given content
     * @param content
     * @return
     */
    public static List<String> getOuterScopes(String content) {

        Map<Integer, Character> occurrences = traceScopes(content);
        List<String> methods = new LinkedList<String>();

        if (occurrences.isEmpty()) {
            log.warning("No braces were found in the code");
            return methods;
        }

        int scopeDepth = 0;
        int lastMethodEndIndex = 0;

        Iterator<Map.Entry<Integer, Character>> iterator = occurrences.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            int characterIndex = (Integer) entry.getKey();
            Character brace = (Character) entry.getValue();

            // count scope depth
            if (brace.equals(openBraceCharater)) {
                scopeDepth++;
            } else if (brace.equals(closeBraceCharater)) {
                scopeDepth--;
            } else {
                log.warning("Unexpected character in scope tracing!");
            }

            if (scopeDepth == 0) {
                methods.add(content.substring(lastMethodEndIndex, characterIndex + 1).trim());
                lastMethodEndIndex = characterIndex + 1;
            } else if (scopeDepth < 0) {
                log.warning("Scope depth is lower then 0. That can't be good!");
            }
        }

        return methods;
    }

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
            lastCloseBraceIndex = content.indexOf('}', closeBraceSearchStartIndex);

            if (lastOpenBraceIndex < lastCloseBraceIndex && lastOpenBraceIndex != -1) {
                occurrences.put(lastOpenBraceIndex, openBraceCharater);
                openBraceSearchStartIndex = lastOpenBraceIndex + 1;
            } else if (lastCloseBraceIndex != -1) {
                occurrences.put(lastCloseBraceIndex, closeBraceCharater);
                closeBraceSearchStartIndex = lastCloseBraceIndex + 1;
            }
        }

        //occurrences.forEach((l, c) -> log.info(Handy.f("%s @ %d", c, l)));
        return occurrences;
    }

//    private int scopeCrawlerRecursive(String content, int scopeDepth, List<String> methods) {
//        int scopeBeginIndex = content.indexOf('{');
//        int scopeEndIndex = content.indexOf('}');
//
//
//        // stop condition, closing brace comes before the next
//        if (scopeEndIndex < scopeBeginIndex && scopeBeginIndex != -1)
//        {
//            return scopeEndIndex;
//        }
//
//        // call again
//        scopeCrawlerRecursive(content.substring(scopeBeginIndex + 1), scopeDepth + 1, methods);
//
//        return -1;
//    }


    public static void getFileListRecursive(String folderPath, List<File> files, String extension) {
        File[] topLevelFiles = new File(folderPath).listFiles();

        //log.info(Handy.f("Folder: %s (%d)", folderPath, topLevelFiles.length));

        if (topLevelFiles != null)
            for (File file : topLevelFiles) {
                if (file.isFile() && Handy.getFileExtension(file).equals(extension)) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    getFileListRecursive(file.getAbsolutePath(), files, extension);
                }
            }
    }

    public static String fileToString(File file) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.severe(Handy.f("Exception occurred while reading the file '%s'", file.toPath()));
            e.printStackTrace();
        }
        return content;
    }

    /**
     * Removes single line and multiline comments from the input text
     * from: https://stackoverflow.com/questions/1657066/java-regular-expression-finding-comments-in-code
     * @param content
     * @return
     */
    public static String removeComments(String content) {
        return content.replaceAll("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", "$1 ").trim();
    }

    private static String patternMatcher(String content, String patternText)
    {
        return patternMatcher(content, patternText, 1);
    }

    private static String patternMatcher(String content, String patternText, int groupIndex)
    {
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(groupIndex);
        } else {
            return "";
        }
    }
}
