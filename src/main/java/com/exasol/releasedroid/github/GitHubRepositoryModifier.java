package com.exasol.releasedroid.github;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import java.time.LocalDate;
import java.util.Optional;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.repository.*;
import com.exasol.releasedroid.usecases.Repository;

/**
 * Writes changes to a GitHub repository.
 */
public class GitHubRepositoryModifier implements RepositoryModifier {
    @Override
    // [impl->dsn~automatically-modifying-release-date~1]
    public void writeReleaseDate(final Repository repository) {
        final String version = repository.getVersion();
        final ReleaseLetter releaseLetter = repository.getReleaseLetter(version);
        final Optional<LocalDate> releaseDate = releaseLetter.getReleaseDate();
        if ((releaseDate.isEmpty()) || !(releaseDate.get().equals(LocalDate.now()))) {
            updateReleaseDate(repository, releaseLetter);
        }
    }

    private void updateReleaseDate(final Repository repository, final ReleaseLetter releaseLetter) {
        final String filePath = getChangesFilePath(repository);
        final String changes = repository.getSingleFileContentAsString(filePath);
        final String modifiedChanges = modifyChanges(releaseLetter, changes);
        repository.updateFileContent(filePath, modifiedChanges,
                "Automatic release date update for " + repository.getVersion());
    }

    private String getChangesFilePath(final Repository repository) {
        final String fileName = "changes_" + repository.getVersion() + ".md";
        return "doc/changes/" + fileName;
    }

    private String modifyChanges(final ReleaseLetter releaseLetter, final String changes) {
        final Optional<LocalDate> releaseDate = releaseLetter.getReleaseDate();
        if (releaseDate.isPresent()) {
            return changes.replace(releaseDate.get().toString(), LocalDate.now().toString());
        } else {
            if (changes.contains("released") && changes.contains(LINE_SEPARATOR)) {
                final String substring = getSubstringToReplace(changes);
                return changes.replace(substring, "released " + LocalDate.now().toString() + LINE_SEPARATOR);
            } else {
                throw new RepositoryException(ExaError.messageBuilder("E-REP-GH-1")
                        .message("Unable to detect a release date stab in the changes file.")
                        .mitigation("Please, update the release date manually").toString());
            }
        }
    }

    private String getSubstringToReplace(final String changes) {
        final int start = changes.indexOf("released");
        final int end = changes.indexOf(LINE_SEPARATOR);
        return changes.substring(start, end + 1);
    }
}