package com.exasol.repository;

import static com.exasol.ReleaseRobotConstants.*;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a changes file's content.
 */
public class ReleaseChangesLetter {
    private final String fileName;
    private String header = null;
    private String body = null;
    private String versionNumber = null;
    private LocalDate releaseDate = null;
    private final List<Integer> ticketNumbers = new ArrayList<>();

    /**
     * Create a new {@link ReleaseChangesLetter} from a string.
     * 
     * @param fileName name of the changes file
     * @param content file's content as a string
     */
    public ReleaseChangesLetter(final String fileName, final String content) {
        this.fileName = fileName;
        if (content != null && content.length() > 1) {
            parseContent(content);
        }
    }

    /**
     * Get a name of the changes file.
     * 
     * @return name of the changes file
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Get a version number.
     * 
     * @return version number as a string
     */
    public Optional<String> getVersionNumber() {
        return this.versionNumber == null ? Optional.empty() : Optional.of(this.versionNumber);
    }

    /**
     * Get a release date.
     * 
     * @return release date
     */
    public Optional<LocalDate> getReleaseDate() {
        return this.releaseDate == null ? Optional.empty() : Optional.of(this.releaseDate);
    }

    /**
     * Get a release letter header.
     * 
     * @return header as a string
     */
    public Optional<String> getHeader() {
        return this.header == null ? Optional.empty() : Optional.of(this.header);
    }

    /**
     * Get a release letter body.
     * 
     * @return body as a atring
     */
    public Optional<String> getBody() {
        return this.body == null ? Optional.empty() : Optional.of(this.body);
    }

    /**
     * Get mentioned tickets' numbers.
     * 
     * @return list of ticket numbers
     */
    public List<Integer> getTicketNumbers() {
        return this.ticketNumbers;
    }

    private void parseContent(final String content) {
        final List<String> contentParts = divideContent(content);
        if (contentParts.size() == 2) {
            parseHeaders(contentParts.get(0));
            parseBody(contentParts.get(1));
        }
    }

    private List<String> divideContent(final String content) {
        final int divideIndex = content.indexOf("##");
        if (divideIndex != -1) {
            final String headersPart = content.substring(0, divideIndex);
            final String bodyPart = content.substring(divideIndex);
            return List.of(headersPart, bodyPart);
        } else {
            return Collections.emptyList();
        }
    }

    private void parseHeaders(final String content) {
        parseVersionNumber(content);
        parseReleaseDate(content);
        parseHeader(content);
    }

    private void parseBody(final String content) {
        this.body = content;
        parseTicketNumbers(content);
    }

    private void parseVersionNumber(final String content) {
        final String firstLine = getFirstLine(content);
        final List<String> versionNumberList = getExpressionsByRegex(firstLine, VERSION_REGEX);
        if (!versionNumberList.isEmpty()) {
            this.versionNumber = versionNumberList.get(0);
        }
    }

    private void parseReleaseDate(final String content) {
        final String firstLine = getFirstLine(content);
        final List<String> releaseDateList = getExpressionsByRegex(firstLine, DATE_REGEX);
        if (!releaseDateList.isEmpty()) {
            this.releaseDate = LocalDate.parse(releaseDateList.get(0));
        }
    }

    private String getFirstLine(final String content) {
        final int firstLineEnd = content.indexOf('\n');
        return firstLineEnd == -1 ? content : content.substring(0, firstLineEnd);
    }

    private void parseHeader(final String content) {
        final String targetTag = "code name:";
        final int startIndex = content.toLowerCase().indexOf(targetTag);
        if (startIndex != -1) {
            this.header = content.substring(startIndex + targetTag.length()).strip();
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

    private void parseTicketNumbers(final String body) {
        final List<String> hashtags = getExpressionsByRegex(body, TICKET_NUMBER_REGEX);
        for (final String hashtag : hashtags) {
            final int ticketNumber = Integer.parseInt(hashtag.substring(1));
            this.ticketNumbers.add(ticketNumber);
        }
    }
}