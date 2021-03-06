package com.exasol.releasedroid.usecases.repository;

import java.time.LocalDate;
import java.util.*;

/**
 * This class represents a changes file's content.
 */
public class ReleaseLetter {
    private final String fileName;
    private final String header;
    private final String body;
    private final String versionNumber;
    private final LocalDate releaseDate;
    private final List<Integer> ticketNumbers;

    private ReleaseLetter(final Builder builder) {
        this.fileName = builder.fileName;
        this.header = builder.header;
        this.body = builder.body;
        this.versionNumber = builder.versionNumber;
        this.releaseDate = builder.releaseDate;
        this.ticketNumbers = builder.ticketNumbers;
    }

    /**
     * Get a {@link ReleaseLetter} builder.
     *
     * @return builder instance
     * @param fileName name of the release letter file
     */
    public static Builder builder(final String fileName) {
        return new Builder(fileName);
    }

    /**
     * Get the name of the changes file.
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
        return Optional.ofNullable(this.versionNumber);
    }

    /**
     * Get a release date.
     * 
     * @return release date
     */
    public Optional<LocalDate> getReleaseDate() {
        return Optional.ofNullable(this.releaseDate);
    }

    /**
     * Get a release letter header.
     * 
     * @return header as a string
     */
    public Optional<String> getHeader() {
        return Optional.ofNullable(this.header);
    }

    /**
     * Get a release letter body.
     * 
     * @return body as a string
     */
    public Optional<String> getBody() {
        return Optional.ofNullable(this.body);
    }

    /**
     * Get mentioned tickets' numbers.
     * 
     * @return list of ticket numbers
     */
    public List<Integer> getTicketNumbers() {
        return this.ticketNumbers;
    }

    /**
     * Get summary section.
     * 
     * @return summary section if exists
     */
    public Optional<String> getSummary() {
        final int start = this.body.indexOf("## Summary");
        final int end = this.body.indexOf("##", start + 2);
        if (start == -1) {
            return Optional.empty();
        } else {
            final String summary = end == -1 ? this.body.substring(start + 10) : this.body.substring(start + 10, end);
            return Optional.of(summary.strip());
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReleaseLetter that = (ReleaseLetter) o;
        return Objects.equals(this.fileName, that.fileName) && Objects.equals(this.header, that.header)
                && Objects.equals(this.body, that.body) && Objects.equals(this.versionNumber, that.versionNumber)
                && Objects.equals(this.releaseDate, that.releaseDate)
                && Objects.equals(this.ticketNumbers, that.ticketNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fileName, this.header, this.body, this.versionNumber, this.releaseDate,
                this.ticketNumbers);
    }

    @Override
    public String toString() {
        return "ReleaseLetter{" + "fileName='" + this.fileName + '\'' + ", header='" + this.header + '\'' + ", body='"
                + this.body + '\'' + ", versionNumber='" + this.versionNumber + '\'' + ", releaseDate="
                + this.releaseDate + ", ticketNumbers=" + this.ticketNumbers + '}';
    }

    /**
     * Builder for {@link ReleaseLetter}.
     */
    public static class Builder {
        private final String fileName;
        private String header = null;
        private String body = null;
        private String versionNumber = null;
        private LocalDate releaseDate = null;
        private List<Integer> ticketNumbers = new ArrayList<>();

        private Builder(final String fileName) {
            this.fileName = fileName;
        }

        /**
         * Add a header of a release letter.
         *
         * @param header header to use during GitHub release
         * @return builder instance for fluent programming
         */
        public Builder header(final String header) {
            this.header = header;
            return this;
        }

        /**
         * Add a body of a release letter.
         *
         * @param body body of the release letter
         * @return builder instance for fluent programming
         */
        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        /**
         * Add a version mentioned in a release letter.
         *
         * @param versionNumber version number as a string
         * @return builder instance for fluent programming
         */
        public Builder versionNumber(final String versionNumber) {
            this.versionNumber = versionNumber;
            return this;
        }

        /**
         * Add a release date mentioned in the release letter.
         *
         * @param releaseDate release date
         * @return builder instance for fluent programming
         */
        public Builder releaseDate(final LocalDate releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        /**
         * Add a list of tickets mentioned in a release letter.
         *
         * @param ticketNumbers list of mentioned tickets
         * @return builder instance for fluent programming
         */
        public Builder ticketNumbers(final List<Integer> ticketNumbers) {
            this.ticketNumbers = ticketNumbers;
            return this;
        }

        /**
         * Create a new {@link ReleaseLetter} instance.
         *
         * @return new {@link ReleaseLetter} instance
         */
        public ReleaseLetter build() {
            return new ReleaseLetter(this);
        }
    }
}
