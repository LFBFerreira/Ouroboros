package luisf.ouroboros.analyzer;

import luisf.ouroboros.analyzer.models.DeclarationModel;
import luisf.ouroboros.common.Handy;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static luisf.ouroboros.analyzer.RegexConstants.*;
import static luisf.ouroboros.analyzer.RegexConstants.closeBraces;

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
                anyWord + oneOrMoreWhitespaces + "class" + oneOrMoreWhitespaces + "(\\w+)" + "[\\w\\s,]+" + openBracesGroup);
    }

    public static int getClassStartIndex(String code) {
        return patternMatcherStartIndex(code,
                anyWord + oneOrMoreWhitespaces + "class" + oneOrMoreWhitespaces + "(\\w+)" + "[\\w\\s,]+" + openBracesGroup,
                2);
    }

    public static String getInterfaceName(String code) {
        return patternMatcher(code,
                anyWord + oneOrMoreWhitespaces + "interface" + oneOrMoreWhitespaces + "(\\w+)" + "[\\w\\s,]+" + openBraces);
    }

    public static String getEnumName(String code) {
        return patternMatcher(code,
                anyWord + oneOrMoreWhitespaces + "enum" + oneOrMoreWhitespaces + "(\\w+)" + "[\\w\\s,]+" + openBraces);
    }

    public static String getClassMethodsText(String code) {
        return patternMatcher(code,
                anyChar + "class" + anyChar + openBraces + "((" + anyChar + anyWhitespaces + ")*)" + closeBraces);
    }


    public static String getMethodName(String code) {
        // regex taken from: https://stackoverflow.com/questions/68633/regex-that-will-match-a-java-method-declaration

        return patternMatcher(code,
                modifierKeywordsGroup + anyWhitespaces + "([\\w<>.?, \\[\\]]*)" + oneOrMoreWhitespaces + "(\\w+)" + anyWhitespaces +
                        "\\([\\w<>\\[\\]._?, \\n]*\\)" + anyWhitespaces + "([\\w ,\\n]*)" + anyWhitespaces + "\\{",
                3);
    }

    public static int getMethodDeclarationStart(String code) {
        // regex taken from: https://stackoverflow.com/questions/68633/regex-that-will-match-a-java-method-declaration

        return patternMatcherStartIndex(code,
                modifierKeywordsGroup + anyWhitespaces + "([\\w<>.?, \\[\\]]*)" + oneOrMoreWhitespaces + "(\\w+)" + anyWhitespaces +
                        "\\([\\w<>\\[\\]._?, \\n]*\\)" + anyWhitespaces + "([\\w ,\\n]*)" + anyWhitespaces + "\\{",
                0);
    }

    /**
     * Finds the open brace in a method declaration and returns its index
     * @param code
     * @return
     */
    public static int getMethodStartBrace(String code) {
        // regex taken from: https://stackoverflow.com/questions/68633/regex-that-will-match-a-java-method-declaration

        Pattern pattern = Pattern.compile(modifierKeywordsGroup + " *" + "([\\w<>.?, \\[\\]]*)" + oneOrMoreWhitespaces + "(\\w+)" + anyWhitespaces +
                "\\([\\w<>\\[\\]._?, \\n]*\\)" + anyWhitespaces + "([\\w ,\\n]*)" + anyWhitespaces + "(\\{)");
        Matcher matcher = pattern.matcher(code);

        if (matcher.find() && code.charAt(matcher.start(5)) == '{') {
            return matcher.start(5);
        } else {
            return -1;
        }
    }

    /**
     * Removes static blocks from the code
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
     * @param content
     * @return
     */
    public static List<DeclarationModel> extractClassDeclarations(String content) {
        // copy the content, to be modified later
        String partialContent = content;

        int openBraceIndex = getMethodStartBrace(partialContent);
        int blockEndIndex = -1;

        while (openBraceIndex > -1) {
            blockEndIndex = findClosingBraceIndex(partialContent, openBraceIndex);

            if (blockEndIndex != -1) {
                int methodStartIndex = getMethodDeclarationStart(partialContent);

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

        return parseDeclarations(declarationsText);
    }


    /**
     * Parses a list of methods as text into a logical structure
     * @param fileContent
     * @return
     */
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

    /**
     * Gets a list of files from the current folder and sub folders, filtered by extension
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
     * @param declarationsText
     * @return
     */
    private static List<DeclarationModel> parseDeclarations(List<String> declarationsText) {
        Pattern pattern = Pattern.compile(anyWhitespaces + "(" + modifierKeywordsGroup + "*)" + typeDeclarationGroup + " +" + atleastOneWordGroup);

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
