package com.exasol.releasedroid.repository;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.*;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exasol.releasedroid.repository.ReleaseLetter.Builder;

public final class ReleaseLetterParser {
    public static final String TICKET_NUMBER_REGEX = "\\* #[1-9]\\d*\\b";
    private final Builder builder;
    private final String content;

    /**
     * Create a new {@link ReleaseLetterParser}.
     *
     * @param fileName name of the release letter file
     * @param content  file's content as a string
     */
    public ReleaseLetterParser(final String fileName, final String content) {
        this.builder = ReleaseLetter.builder(fileName);
        this.content = content;
    }

    /**
     * Parse a new {@link ReleaseLetter} from a string.
     */
    public ReleaseLetter parse() {
        if ((this.content != null) && (this.content.length() > 1)) {
            parseContent();
        }
        return this.builder.build();
    }

    private void parseContent() {
        final List<String> contentParts = divideContent();
        if (contentParts.size() == 2) {
            parseHeaders(contentParts.get(0));
            parseBody(contentParts.get(1));
        }
    }

    private List<String> divideContent() {
        final int divideIndex = this.content.indexOf("##");
        if (divideIndex != -1) {
            final String headersPart = this.content.substring(0, divideIndex);
            final String bodyPart = this.content.substring(divideIndex);
            return List.of(headersPart, bodyPart);
        } else {
            return Collections.emptyList();
        }
    }

    private void parseHeaders(final String headerPart) {
        final String versionNumber = parseVersionNumber(headerPart);
        final LocalDate releaseDate = parseReleaseDate(headerPart);
        final String header = parseHeader(headerPart);
        this.builder.versionNumber(versionNumber).releaseDate(releaseDate).header(header);
    }

    private void parseBody(final String bodyPart) {
        final List<Integer> ticketNumbers = parseTicketNumbers(bodyPart);
        this.builder.body(bodyPart).ticketNumbers(ticketNumbers);
    }

    private String parseVersionNumber(final String headerPart) {
        final String firstLine = getFirstLine(headerPart);
        final List<String> versionNumberList = getExpressionsByRegex(firstLine, VERSION_REGEX);
        return (!versionNumberList.isEmpty()) ? versionNumberList.get(0) : null;
    }

    private LocalDate parseReleaseDate(final String headerPart) {
        final String firstLine = getFirstLine(headerPart);
        final List<String> releaseDateList = getExpressionsByRegex(firstLine, DATE_REGEX);
        return (!releaseDateList.isEmpty()) ? LocalDate.parse(releaseDateList.get(0)) : null;
    }

    private String getFirstLine(final String headerPart) {
        final int firstLineEnd = headerPart.indexOf(LINE_SEPARATOR);
        return firstLineEnd == -1 ? headerPart : headerPart.substring(0, firstLineEnd);
    }

    private String parseHeader(final String headerPart) {
        final String targetTag = "code name:";
        final int startIndex = headerPart.toLowerCase().indexOf(targetTag);
        if (startIndex != -1) {
            final String header = headerPart.substring(startIndex + targetTag.length()).strip();
            return header.isEmpty() ? null : header;
        } else {
            return null;
        }
    }

    private List<String> getExpressionsByRegex(final String content, final String regex) {
        final Pattern versionPattern = Pattern.compile(regex);
        final Matcher matcher = versionPattern.matcher(content);
        final List<String> found = new ArrayList<>();
        while (matcher.find()) {
            found.add(matcher.group());
        }
        return found;
    }

    private List<Integer> parseTicketNumbers(final String body) {
        final List<String> hashtags = getExpressionsByRegex(body, TICKET_NUMBER_REGEX);
        final List<Integer> numbers = new ArrayList<>();
        for (final String hashtag : hashtags) {
            final int ticketNumber = Integer.parseInt(hashtag.substring(3));
            numbers.add(ticketNumber);
        }
        return numbers;
    }
}