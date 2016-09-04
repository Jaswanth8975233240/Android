package com.intelliq.appengine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public class ParserHelper {

    public static final int MODE_FIRST_FIRST = 0;
    public static final int MODE_FIRST_LAST = 1;
    public static final int MODE_LAST_FIRST = 2;
    public static final int MODE_LAST_LAST = 3;

    public static final int TOLERANCE_NONE = 0;
    public static final int TOLERANCE_LOW = 1;
    public static final int TOLERANCE_MEDIUM = 2;
    public static final int TOLERANCE_HIGH = 3;

    public static String getStringBetween(String startIdentifier, String endIdentifier, String sourceString) throws Exception {
        return getStringBetween(startIdentifier, endIdentifier, sourceString, MODE_FIRST_FIRST);
    }

    public static String getStringBetween(String startIdentifier, String endIdentifier, String sourceString, int mode) throws Exception {
        String result = sourceString;

        int startIndex;
        int endIndex;

        switch (mode) {
            case MODE_FIRST_FIRST: {
                startIndex = sourceString.indexOf(startIdentifier) + startIdentifier.length();
                sourceString = sourceString.substring(startIndex);
                endIndex = sourceString.indexOf(endIdentifier);
                break;
            }
            case MODE_FIRST_LAST: {
                startIndex = sourceString.indexOf(startIdentifier) + startIdentifier.length();
                sourceString = sourceString.substring(startIndex);
                endIndex = sourceString.lastIndexOf(endIdentifier);
                break;
            }
            case MODE_LAST_FIRST: {
                startIndex = sourceString.lastIndexOf(startIdentifier) + startIdentifier.length();
                sourceString = sourceString.substring(startIndex);
                endIndex = sourceString.indexOf(endIdentifier);
                break;
            }
            case MODE_LAST_LAST: {
                startIndex = sourceString.lastIndexOf(startIdentifier) + startIdentifier.length();
                sourceString = sourceString.substring(startIndex);
                endIndex = sourceString.lastIndexOf(endIdentifier);
                break;
            }
            default: {
                startIndex = sourceString.indexOf(startIdentifier) + startIdentifier.length();
                sourceString = sourceString.substring(startIndex);
                endIndex = sourceString.indexOf(endIdentifier);
                break;
            }
        }

        result = sourceString.substring(0, endIndex);

        return result;
    }

    public static String getStringAfter(String startIdentifier, String sourceString) throws Exception {
        return getStringAfter(startIdentifier, sourceString, MODE_FIRST_FIRST);
    }

    public static String getStringAfter(String startIdentifier, String sourceString, int mode) throws Exception {
        String result = sourceString;

        int startIndex;
        int endIndex;

        if (mode == MODE_FIRST_FIRST || mode == MODE_FIRST_LAST) {
            startIndex = sourceString.indexOf(startIdentifier) + startIdentifier.length();
        } else {
            startIndex = sourceString.lastIndexOf(startIdentifier) + startIdentifier.length();
        }

        result = sourceString.substring(startIndex);

        return result;
    }

    public static String getStringBefore(String startIdentifier, String sourceString) throws Exception {
        return getStringBefore(startIdentifier, sourceString, MODE_FIRST_FIRST);
    }

    public static String getStringBefore(String startIdentifier, String sourceString, int mode) throws Exception {
        String result = sourceString;

        int startIndex;
        int endIndex;

        if (mode == MODE_FIRST_FIRST || mode == MODE_FIRST_LAST) {
            startIndex = sourceString.indexOf(startIdentifier);
        } else {
            startIndex = sourceString.lastIndexOf(startIdentifier);
        }

        result = sourceString.substring(0, startIndex);

        return result;
    }

    public static String getStringInTag(String tagName, String sourceString) throws Exception {
        String result = sourceString;

        String startIdentifier = "<" + tagName + " ";
        String endIdentifier = "</" + tagName + ">";

        if (sourceString.contains(startIdentifier) && sourceString.contains(endIdentifier)) {
            result = getStringBetween(startIdentifier, endIdentifier, result, MODE_FIRST_FIRST);
            result = getStringAfter(">", result);
        }

        return result;
    }

    public static String getPageSource(String requestUrl) throws Exception {
        //URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();
        //HTTPResponse response = fetchService.fetch(new URL(requestUrl));

        URL url = new URL(requestUrl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        return sb.toString();
    }

    public static boolean isSameString(String value1, String value2, int tolerance) {
        value1 = value1.toLowerCase().trim();
        value2 = value2.toLowerCase().trim();

        if (tolerance > TOLERANCE_NONE) {
            value1 = removeSeperatorsFromString(value1);
            value2 = removeSeperatorsFromString(value2);
        }

        return value1.equals(value2);
    }

    public static boolean containsAnyValue(String value) {
        if (value != null && value.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String removeSeperatorsFromString(String value) {
        value = value.replace(" ", "");
        value = value.replace("_", "");
        value = value.replace("-", "");
        value = value.replace(".", "");
        return value;
    }

    public static String removeRegexInString(String sourceString, String regexString) {
        return replaceRegexInString(sourceString, "", regexString);
    }

    public static String replaceRegexInString(String sourceString, String replace, String regexString) {
        String result = sourceString;
        try {
            String foundPart = null;
            Pattern regex = Pattern.compile(regexString);
            Matcher regexMatcher = regex.matcher(sourceString);
            if (regexMatcher.find()) {
                foundPart = regexMatcher.group();
            }
            result = sourceString.replace(foundPart, replace);
        } catch (Exception ex) {
            // Syntax error in the regular expression
        }
        return result;
    }

    public static int countOccurrencesInString(String search, String source) {
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {
            lastIndex = source.indexOf(search, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += search.length();
            }
        }

        return count;
    }

    public static boolean containsSpecialCharacters(String source) {
        if (source.contains("<") || source.contains(">")) {
            return true;
        }
        return false;
    }

    public static boolean isValidUrl(String urlString) {
        if (!urlString.startsWith("http")) {
            return false;
        }
        try {
            URL url = new URL(urlString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
