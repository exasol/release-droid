package com.exasol.releasedroid.output.guide;

import java.io.*;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;

/**
 * Parse latest file changes_*.md to retrieve summary of current release for proposed announcement in appropriate slack
 * channel.
 */
class ChangesFileParser {

    static final String changesFilePath(final String version) {
        return "doc/changes/changes_" + version + ".md";
    }

    private static final Pattern CODE_NAME = Pattern.compile("^Code name:\\s*(.*)");
    private static final Pattern SUMMARY = Pattern.compile("^##\\s*Summary\\s*");

    static boolean empty(final String line) {
        return line.trim().isEmpty();
    }

    static boolean heading(final String line) {
        return line.startsWith("#");
    }

    static boolean listItem(final String line) {
        return line.startsWith("*");
    }

    private final RepositoryGate repositoryGate;
    private final String version;

    ChangesFileParser(final RepositoryGate repositoryGate, final String version) {
        this.repositoryGate = repositoryGate;
        this.version = version;
    }

    String getSummary() {
        try {
            return getSummary(changesFilePath(this.version));
        } catch (final IOException | RepositoryException exception) {
            return ReleaseGuideProperties.error("Failed to read changes file: " + exception.getMessage());
        }
    }

    private String getSummary(final String filename) throws IOException, RepositoryException {
        final String content = this.repositoryGate.getSingleFileContentAsString(filename);
        final StringBuilder sb = new StringBuilder();
        try (final BufferedReader reader = new BufferedReader(new StringReader(content))) {
            Mode mode = Mode.SEARCH;
            String line;
            while (((line = reader.readLine()) != null) && (mode != Mode.DONE)) {
                mode = mode.next(line);
                if (mode == Mode.SEARCH) {
                    final Matcher matcher = CODE_NAME.matcher(line);
                    if (matcher.matches()) {
                        sb.append(matcher.group(1)).append(": ");
                    }
                }
                if (!mode.isCollect()) {
                    continue;
                }
                if (mode == Mode.BEGIN_UL) {
                    mode = Mode.LI;
                    linefeed(sb).append("<ul>");
                }
                if (!empty(line)) {
                    linefeed(sb).append(markdownToHtml(line));
                }
                if (mode == Mode.END_UL) {
                    mode = Mode.COLLECT;
                    linefeed(sb).append("</ul>");
                }
            }
        }

        return sb.toString();
    }

    private StringBuilder linefeed(final StringBuilder sb) {
        return (sb.length() > 0) ? sb.append("\n") : sb;
    }

    String markdownToHtml(final String line) {
        return line //
                .replaceFirst("^\\*\\s*?(\\S.*)$", "<li>$1</li>") //
                .replaceAll("`([^`]+)`", "<code>$1</code>");
    }

    private enum Mode {
        SEARCH, FOUND, COLLECT, BEGIN_UL, LI, END_UL, DONE;

        private static final Set<Mode> COLLECTING_MODES = Set.of(COLLECT, BEGIN_UL, LI, END_UL);

        Mode next(final String line) {
            switch (this) {
            case SEARCH:
                return SUMMARY.matcher(line).find() ? FOUND : this;
            case FOUND:
                if (empty(line)) {
                    return this;
                }
                if (heading(line)) {
                    return DONE;
                }
                return COLLECT;
            case COLLECT:
                if (heading(line)) {
                    return DONE;
                }
                if (listItem(line)) {
                    return BEGIN_UL;
                }
                return this;
            case LI:
                if (heading(line)) {
                    return DONE;
                }
                if (listItem(line)) {
                    return END_UL;
                }
                return this;
            default:
                return this;
            }
        }

        boolean isCollect() {
            return COLLECTING_MODES.contains(this);
        }
    }
}
