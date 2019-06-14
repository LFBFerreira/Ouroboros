package luisf.ouroboros.analyzer;

import luisf.ouroboros.models.CodeModel;
import luisf.ouroboros.models.DeclarationModel;
import luisf.ouroboros.models.MethodModel;
import luisf.ouroboros.models.metrics.*;
import luisf.ouroboros.common.Handy;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static luisf.ouroboros.analyzer.RegexConstants.*;

public class Parse {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final static Character openBraceCharacter = '{';
    private final static Character closeBraceCharacter = '}';

    // ================================================================

    public static String getPackageName(String code) {
        return patternMatcher(code,
                anyChar + "package" + oneOrMoreWhitespaces + anythingCharGroup + anyWhitespaces + semicolon);
    }

    public static String getClassName(String code) {
        return patternMatcher(code,
                anyWord + oneOrMoreWhitespaces + "class" + oneOrMoreWhitespaces + "(\\w+)" + headerExtensions + openBracesGroup);
    }

    public static int getClassStartIndex(String code) {
        return patternMatcherStartIndex(code,
                anyWord + oneOrMoreWhitespaces + "class" + oneOrMoreWhitespaces + "(\\w+)" + headerExtensions + openBracesGroup,
                2);
    }

    public static String getInterfaceName(String code) {
        return patternMatcher(code,
                anyWord + oneOrMoreWhitespaces + "interface" + oneOrMoreWhitespaces + "(\\w+)" + headerExtensions + openBraces);
    }

    public static String getEnumName(String code) {
        return patternMatcher(code,
                anyWord + oneOrMoreWhitespaces + "enum" + oneOrMoreWhitespaces + "(\\w+)" + headerExtensions + openBraces);
    }


    /**
     * Extracts the method name from the method header, including open brace
     *
     * @param code
     * @return
     */
    public static String getMethodName(String code) {
        // regex taken from: https://stackoverflow.com/questions/68633/regex-that-will-match-a-java-method-declaration

        return patternMatcher(code, methodHeader + anyWhitespaces + "\\{", 4);
    }

    /**
     * Extracts the method name from the method header, including open brace
     *
     * @param code
     * @return
     */
    public static String getMethodReturnType(String code) {
        // regex taken from: https://stackoverflow.com/questions/68633/regex-that-will-match-a-java-method-declaration

        return patternMatcher(code, methodHeader + anyWhitespaces + "\\{", 3);
    }

    public static int getMethodDeclarationStart(String code) {
        // regex taken from: https://stackoverflow.com/questions/68633/regex-that-will-match-a-java-method-declaration

        return patternMatcherStartIndex(code, methodHeader + anyWhitespaces + "\\{", 0);
    }

    /**
     * Finds the open brace in a method declaration and returns its index
     *
     * @param code
     * @return
     */
    public static int getMethodStartBrace(String code) {
        // regex taken from: https://stackoverflow.com/questions/68633/regex-that-will-match-a-java-method-declaration

        Pattern pattern = Pattern.compile(methodHeader + anyWhitespaces + "(\\{)");
        Matcher matcher = pattern.matcher(code);

        if (matcher.find() && code.charAt(matcher.start(5)) == '{') {
            return matcher.start(5);
        } else {
            return -1;
        }
    }

    /**
     * Removes static blocks from the code
     *
     * @param code
     * @return
     */
    public static String removeStaticBlocks(String code) {

        String regex = "(static\\s*(\\{))";

        // copy the code, to be modified later
        String filteredContent = code;

        // find index of the start of the static block
        int blockStartIndex = patternMatcherStartIndex(filteredContent, regex, 1);
        int openBraceIndex = patternMatcherStartIndex(filteredContent, regex, 2);
        int blockEndIndex = -1;

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
     * Finds the matching closing brace for the requested open brace
     *
     * @param code
     * @param openBraceIndex
     * @return
     */
    public static int findClosingBraceIndex(String code, int openBraceIndex) {

        if (openBraceIndex >= code.length() || code.charAt(openBraceIndex) != '{') {
            log.severe("The index of the open brace is incorrect, " + openBraceIndex);
            return -1;
        }

        Boolean isInsideString = false;
        int scopeLevel = 0;

        // analyze character by character
        for (int i = 0; i < code.length(); i++) {
            char charAt = code.charAt(i);

            if (charAt == '"') {
                isInsideString = !isInsideString;
            }

            if (charAt == '\'' && !isInsideString) {
                // skip single quotes if they are not inside a string
                i += 2;
                continue;
            }

            // increase or decrease the scope accordingly, ignoring string occurrences
            if (i >= openBraceIndex && !isInsideString) {
                if (charAt == '{') {
                    scopeLevel++;
                    //log.info(code.substring(i, code.length()));
                } else if (charAt == '}') {
                    scopeLevel--;
                    //log.info(code.substring(i, code.length()));
                    if (scopeLevel == 0) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    /**
     * Creates an ordered map of the occurrences of opening and closing braces
     *
     * @param code
     * @return
     */
    public static Map<Integer, Character> traceScopes(String code) {
        Map<Integer, Character> occurrences = new LinkedHashMap<Integer, Character>();

        int lastOpenBraceIndex = 0;
        int lastCloseBraceIndex = 0;

        int openBraceSearchStartIndex = 0;
        int closeBraceSearchStartIndex = 0;

        // repeat until there are no more braces in the code
        while (lastOpenBraceIndex != -1 || lastCloseBraceIndex != -1) {
            lastOpenBraceIndex = code.indexOf(openBraceCharacter, openBraceSearchStartIndex);
            lastCloseBraceIndex = code.indexOf(closeBraceCharacter, closeBraceSearchStartIndex);

            if (lastOpenBraceIndex < lastCloseBraceIndex && lastOpenBraceIndex != -1) {
                occurrences.put(lastOpenBraceIndex, openBraceCharacter);
                openBraceSearchStartIndex = lastOpenBraceIndex + 1;
            } else if (lastCloseBraceIndex != -1) {
                occurrences.put(lastCloseBraceIndex, closeBraceCharacter);
                closeBraceSearchStartIndex = lastCloseBraceIndex + 1;
            }
        }

        return occurrences;
    }

    /**
     * Extracts the text between the classe's open and closing braces
     *
     * @param code
     * @return
     */
    public static String extractClassContent(String code) {
        int openBraceIndex = getClassStartIndex(code);

        if (openBraceIndex == -1) {
            log.warning("The class starting brace could not be found");
            log.warning(code);
            return "";
        }

        int closeBraceIndex = findClosingBraceIndex(code, openBraceIndex);

        // increment once, so the open brace is not included in the result
        openBraceIndex++;

        // validate the open and close braces index
        if (openBraceIndex < closeBraceIndex &&
                openBraceIndex > -1 &&
                closeBraceIndex > -1 &&
                openBraceIndex < code.length() &&
                closeBraceIndex < code.length()) {

            return code.substring(openBraceIndex, closeBraceIndex);
        } else {
            log.warning(Handy.f("The open or close brace index is invalid %d - %d", openBraceIndex, closeBraceIndex));
            return code;
        }
    }

    /**
     * Extracts the variables declared inside a class but outside of methods
     *
     * @param content
     * @return text without class declarations
     */
    public static List<DeclarationModel> parseClassDeclarations(String content) {

        List<DeclarationModel> declarationModels = new LinkedList<>();

        // copy the content, to be modified later
        String partialContent = content;
        StringBuffer contentWithoutDeclarations = new StringBuffer();

        int openBraceIndex = getMethodStartBrace(partialContent);
        int blockEndIndex = -1;

        // create string only with declarations and one without any declarations
        while (openBraceIndex > -1) {
            blockEndIndex = findClosingBraceIndex(partialContent, openBraceIndex);

            if (blockEndIndex != -1) {
                int methodStartIndex = getMethodDeclarationStart(partialContent);

                // save all of the removed content for later
                contentWithoutDeclarations.append(partialContent.substring(methodStartIndex, blockEndIndex + 1));

                // remove analyzed content
                partialContent = Handy.removeSubString(methodStartIndex, blockEndIndex, partialContent);
            } else {
                log.severe(Handy.f("Could not find a matching closing brace"));
                break;
            }

            // find next method if it exists
            openBraceIndex = getMethodStartBrace(partialContent);
        }

        List<String> declarationsText = splitDeclarations(partialContent);

        return parseClassDeclarations(declarationsText);
    }

    /**
     * Removes the all the classes's text except for methods
     *
     * @param content
     * @return
     */
    public static String removeClassDeclarations(String content) {

        // copy the content, to be modified later
        String partialContent = content;
        StringBuffer contentWithoutDeclarations = new StringBuffer();

        int openBraceIndex = getMethodStartBrace(partialContent);
        int blockEndIndex = -1;

        while (openBraceIndex > -1) {
            blockEndIndex = findClosingBraceIndex(partialContent, openBraceIndex);

            if (blockEndIndex != -1) {
                int methodStartIndex = getMethodDeclarationStart(partialContent);

                // save all of the removed content for later
                contentWithoutDeclarations.append(partialContent.substring(methodStartIndex, blockEndIndex + 1));

                // remove analyzed content
                partialContent = Handy.removeSubString(methodStartIndex, blockEndIndex, partialContent);
            } else {
                log.severe(Handy.f("Could not find a matching closing brace"));
                break;
            }

            // find next method if it exists
            openBraceIndex = getMethodStartBrace(partialContent);
        }

        return contentWithoutDeclarations.toString();
    }

    /**
     * Parses the information in the received methods text
     *
     * @param fileContent
     * @return
     */
    public static List<MethodModel> parseMethods(String fileContent) {
        List<MethodModel> methods = new LinkedList<>();

        // copy the content, to be modified later
        String partialContent = fileContent;

        // get the name of the first method
        String methodName = getMethodName(partialContent);

        if (Handy.isNullOrEmpty(methodName)) {
            return methods;
        }

        // get open brace of the first method
        int openBraceIndex = getMethodStartBrace(partialContent);
        int blockEndIndex = -1;

        // repeat the process till all methods have been processed
        while (openBraceIndex > -1) {
            blockEndIndex = findClosingBraceIndex(partialContent, openBraceIndex);

            if (blockEndIndex != -1) {

                String methodHeader = partialContent.substring(0, openBraceIndex + 1);
                String methodContent = partialContent.substring(openBraceIndex + 1, blockEndIndex);

                // get next method name
                methodName = getMethodName(methodHeader);

                // if the method name cannot be parsed, return
//                if (Handy.isNullOrEmpty(methodName)) {
//                    break;
//                }

                List<ModifierEnum> modifiers = parseModifiers(methodHeader);

                String returnType = getMethodReturnType(methodHeader);

                // add into to the map
                methods.add(new MethodModel(methodName,
                        methodContent,
                        modifiers,
                        returnType,
                        parseMetrics(methodContent)));

                // remove analyzed content
                partialContent = Handy.removeSubString(0, blockEndIndex, partialContent);
            } else {
                log.severe(Handy.f("Could not find a matching closing brace"));
                // if the end of the method body could not be found, return
                break;
            }

            // find next method if it exists
            openBraceIndex = getMethodStartBrace(partialContent);
        }

        return methods;
    }


    private static final Metric[] codeMetrics = new Metric[]
            {
                    new Metric(CodeMetricEnum.BRACE_OPEN),
                    new Metric(CodeMetricEnum.BRACE_CLOSE),
                    new Metric(CodeMetricEnum.LINE_END),
                    new Metric(CodeMetricEnum.RETURN),
                    new Metric(CodeMetricEnum.DOT),
                    new Metric(CodeMetricEnum.OTHER_SYMBOL),
                    new Metric(CodeMetricEnum.WORD),    // WordMetric needs to be the last because its the most general
            };

    /**
     * @param content
     * @return
     */
    private static List<CodeModel> parseMetrics(String content) {

        List<CodeModel> metrics = new LinkedList<>();

        if (Handy.isNullOrEmpty(content)) {
            log.info("The string is empty or null");
            return metrics;
        }


        // create the regex as a concatenation of all the metrics
        StringBuilder regex = new StringBuilder();
        Arrays.stream(codeMetrics).forEach(m -> regex.append(m.regex + "|"));

        Pattern pattern = Pattern.compile(regex.substring(0, regex.length() - 1));
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            // skip index 0 because group0 is the whole match
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String match = matcher.group(i);

                if (!Handy.isNullOrEmpty(match)) {
                    if (i - 1 < codeMetrics.length) {
                        metrics.add(new CodeModel(codeMetrics[i - 1].metric, match));
                        break;
                    } else {
                        log.severe("There is a mismatch between the matched group number and the metrics");
                    }
                }
            }
        }

        return metrics;
    }

    /**
     * Gets a list of files from the current folder and sub folders, filtered by extension
     *
     * @param folderPath
     * @param files
     * @param extension
     */
    public static void getFileListRecursive(String folderPath, List<File> files, String extension) {
        File[] topLevelFiles = new File(folderPath).listFiles();

        // remove dots from extension
        extension = extension.replace(".", "");

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
     *
     * @param content
     * @return
     */
    public static String removeComments(String content) {
        // from: https://stackoverflow.com/questions/1657066/java-regular-expression-finding-comments-in-code
        return content.replaceAll("\\/\\*[\\s\\S]*?\\*\\/|([^:]|^)\\/\\/.*", " ").trim();
    }


    // ================================================================

    // Helpers

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

    /**
     * Searchs the code for the index of the first char matched by the specified group
     *
     * @param code
     * @param patternText
     * @param groupIndex
     * @return
     */
    private static int patternMatcherStartIndex(String code, String patternText, int groupIndex) {
        Pattern pattern = Pattern.compile(patternText);
        Matcher matcher = pattern.matcher(code);

        if (matcher.find()) {
            return matcher.start(groupIndex);
        } else {
            return -1;
        }
    }

    /**
     * Splits variable declarations by the semicolon into a list of strings
     *
     * @param content
     * @return
     */
    private static List<String> splitDeclarations(String content) {
        List<String> declarations = new ArrayList<>();
        String partialContent = content;

        int splitterIndex = partialContent.indexOf(';');
        while (splitterIndex != -1) {
            declarations.add(partialContent.substring(0, splitterIndex));
            partialContent = Handy.removeSubString(0, splitterIndex, partialContent);
            splitterIndex = partialContent.indexOf(';');
        }

        return declarations;
    }


    /**
     * Parse a list of text declarations into a logic structure
     *
     * @param declarationsText
     * @return
     */
    private static List<DeclarationModel> parseClassDeclarations(List<String> declarationsText) {
        Pattern pattern = Pattern.compile(modifierKeywordsGroup + typeDeclarationGroup + oneOrMoreWhitespaces + atleastOneWordGroup);

        List<DeclarationModel> models = new ArrayList<>();

        for (String declaration : declarationsText) {
            Matcher matcher = pattern.matcher(declaration);

            // go to the next declaration if a pattern could not be found
            if (!matcher.find()) {
                continue;
            }

            models.add(new DeclarationModel(matcher.group(4),
                    matcher.group(3),
                    parseModifiers(matcher.group(1))));
        }

        return models;
    }

    /**
     * Parses the received modifiers in text form to a list of ModifierEnum
     *
     * @param text
     * @return
     */
    private static List<ModifierEnum> parseModifiers(String text) {
        List<ModifierEnum> modifiers = new ArrayList<>();

        if (Handy.isNullOrEmpty(text)) {
            // no modifiers to parse
            return modifiers;
        }

        for (String modifierText : text.trim().split("\\s")) {
            try {
                ModifierEnum modifier = ModifierEnum.valueOf(modifierText.trim().toUpperCase());

                if (!modifiers.contains(modifier) && modifier != ModifierEnum.NONE) {
                    modifiers.add(modifier);
                }
            } catch (IllegalArgumentException e) {
                continue;
            }
        }

        return modifiers;
    }
}
