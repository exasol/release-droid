package com.exasol.releasedroid.adapter.github;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.BaseRepository;
import com.exasol.releasedroid.usecases.repository.ReleaseLetter;
import com.exasol.releasedroid.usecases.repository.ReleaseLetterParser;

@ExtendWith(MockitoExtension.class)
// [utest->dsn~automatically-modifying-release-date~1]
class GitHubRepositoryModifierTest {
    private static final String VERSION = "1.1.1";
    private static final String CHANGES_FILE_NAME = "changes_1.1.1.md";
    private final static String CHANGES_PATH = "doc/changes/" + CHANGES_FILE_NAME;
    private final GitHubRepositoryModifier gitHubRepositoryModifier = new GitHubRepositoryModifier();
    @Mock
    private BaseRepository repositoryMock;

    @BeforeEach
    void beforeEach() {
        when(this.repositoryMock.getVersion()).thenReturn(VERSION);
    }

    @Test
    void testWriteReleaseDateRequireNoChanges() {
        final String releaseLetterAsString = "# Exasol Release Droid 1.1.1, released " + LocalDate.now()
                + LINE_SEPARATOR + " ##";
        final ReleaseLetter releaseLetter = getReleaseLetter(releaseLetterAsString);
        when(this.repositoryMock.getReleaseLetter(VERSION)).thenReturn(releaseLetter);
        this.gitHubRepositoryModifier.writeReleaseDate(this.repositoryMock);
        verify(this.repositoryMock, times(0)).updateFileContent(any(), any(), any());
    }

    private ReleaseLetter getReleaseLetter(final String releaseLetterAsString) {
        return new ReleaseLetterParser(CHANGES_FILE_NAME, releaseLetterAsString).parse();
    }

    @ParameterizedTest
    @ValueSource(strings = { //
            "# Exasol Release Droid 1.1.1, released 2020-09-21\n ##", //
            "# Exasol Release Droid 1.1.1, released 2020-XX-XX\n ##", //
            "# Exasol Release Droid 1.1.1, released \n ##", //
            "# Exasol Release Droid 1.1.1, released blablabla\n ##" //
    })
    void testWriteReleaseDateWithDateSwap(String releaseLetterAsString) {
        releaseLetterAsString = releaseLetterAsString.replace("\n", LINE_SEPARATOR);
        final ReleaseLetter releaseLetter = getReleaseLetter(releaseLetterAsString);
        when(this.repositoryMock.getReleaseLetter(VERSION)).thenReturn(releaseLetter);
        when(this.repositoryMock.getSingleFileContentAsString(CHANGES_PATH)).thenReturn(releaseLetterAsString);
        this.gitHubRepositoryModifier.writeReleaseDate(this.repositoryMock);
        verify(this.repositoryMock, times(1)).updateFileContent(CHANGES_PATH,
                "# Exasol Release Droid 1.1.1, released " + LocalDate.now().toString() + "\n ##",
                "Automatic release date update for " + VERSION);
    }

    @ParameterizedTest
    @ValueSource(strings = { //
            "# Exasol Release Droid 1.1.1, Released 2020-XX-XX\n ##", //
            "# Exasol Release Droid 1.1.1, released", //
            "# Exasol Release Droid 1.1.1, \n ##" //
    })
    void testWriteReleaseDateThrowsException(String releaseLetterAsString) {
        releaseLetterAsString = releaseLetterAsString.replace("\n", LINE_SEPARATOR);
        final ReleaseLetter releaseLetter = getReleaseLetter(releaseLetterAsString);
        when(this.repositoryMock.getReleaseLetter(VERSION)).thenReturn(releaseLetter);
        when(this.repositoryMock.getSingleFileContentAsString(CHANGES_PATH)).thenReturn(releaseLetterAsString);
        final RepositoryException repositoryException = assertThrows(RepositoryException.class,
                () -> this.gitHubRepositoryModifier.writeReleaseDate(this.repositoryMock));
        assertThat(repositoryException.getMessage(), containsString("E-RR-GH-6"));
    }
}