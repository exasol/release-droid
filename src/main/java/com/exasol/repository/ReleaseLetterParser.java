package com.exasol.repository;

import static com.exasol.ReleaseRobotConstants.DATE_REGEX;
import static com.exasol.ReleaseRobotConstants.VERSION_REGEX;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exasol.repository.ReleaseLetter.Builder;

public final class ReleaseLetterParser {
    public static final String TICKET_NUMBER_REGEX = "#[1-9]\\d*\\b";

    private ReleaseLetterParser() {
        // prevent instantiation
    }

    /**
     * Create a new {@link ReleaseLetter} from a string.
     *
     * @param fileName name of the release letter file
     * @param content file's content as a string
     */
    public static ReleaseLetter parseReleaseLetterContent(final String fileName, final String content) {
        final Builder builder = ReleaseLetter.builder(fileName);
        if (content != null && content.length() > 1) {
            parseContent(builder, content);
        }
        return builder.build();
    }

    private static void parseContent(final Builder builder, final String content) {
        final List<String> contentParts = divideContent(content);
        if (contentParts.size() == 2) {
            parseHeaders(builder, contentParts.get(0));
            parseBody(builder, contentParts.get(1));
        }
    }

    private static List<String> divideContent(final String content) {
        final int divideIndex = content.indexOf("##");
        if (divideIndex != -1) {
            final String headersPart = content.substring(0, divideIndex);
            final String bodyPart = content.substring(divideIndex);
            return List.of(headersPart, bodyPart);
        } else {
            return Collections.emptyList();
        }
    }

    private static void parseHeaders(final Builder builder, final String content) {
        final String versionNUmber = parseVersionNumber(content);
        final LocalDate releaseDate = parseReleaseDate(content);
        final String header = parseHeader(content);
        builder.versionNumber(versionNUmber).releaseDate(releaseDate).header(header);
    }

    private static void parseBody(final Builder builder, final String content) {
        final List<Integer> ticketNumbers = parseTicketNumbers(content);
        builder.body(content).ticketNumbers(ticketNumbers);
    }

    private static String parseVersionNumber(final String content) {
        final String firstLine = getFirstLine(content);
        final List<String> versionNumberList = getExpressionsByRegex(firstLine, VERSION_REGEX);
        return (!versionNumberList.isEmpty()) ? versionNumberList.get(0) : null;
    }

    private static LocalDate parseReleaseDate(final String content) {
        final String firstLine = getFirstLine(content);
        final List<String> releaseDateList = getExpressionsByRegex(firstLine, DATE_REGEX);
        return (!releaseDateList.isEmpty()) ? LocalDate.parse(releaseDateList.get(0)) : null;
    }

    private static String getFirstLine(final String content) {
        final int firstLineEnd = content.indexOf('\n');
        return firstLineEnd == -1 ? content : content.substring(0, firstLineEnd);
    }

    private static String parseHeader(final String content) {
        final String targetTag = "code name:";
        final int startIndex = content.toLowerCase().indexOf(targetTag);
        return (startIndex != -1) ? content.substring(startIndex + targetTag.length()).strip() : null;
    }

    private static List<String> getExpressionsByRegex(final String content, final String regex) {
        final Pattern versionPattern = Pattern.compile(regex);
        final Matcher matcher = versionPattern.matcher(content);
        final List<String> found = new ArrayList<>();
        while (matcher.find()) {
            found.add(matcher.group());
        }
        return found;
    }

    private static List<Integer> parseTicketNumbers(final String body) {
        final List<String> hashtags = getExpressionsByRegex(body, TICKET_NUMBER_REGEX);
        final List<Integer> numbers = new ArrayList<>();
        for (final String hashtag : hashtags) {
            final int ticketNumber = Integer.parseInt(hashtag.substring(1));
            numbers.add(ticketNumber);
        }
        return numbers;
    }
}