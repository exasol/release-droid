package com.exasol.releasedroid.usecases.repository;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;
import static com.exasol.releasedroid.usecases.repository.BaseRepository.CHANGELOG_FILE;
import static com.exasol.releasedroid.usecases.repository.BaseRepository.CONFIGURATION_FILE;
import static com.exasol.releasedroid.usecases.request.PlatformName.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.adapter.github.GitHubGateway;
import com.exasol.releasedroid.adapter.repository.CommonRepositoryValidator;
import com.exasol.releasedroid.adapter.repository.GenericRepository;
import com.exasol.releasedroid.usecases.exception.RepositoryException;

@ExtendWith(MockitoExtension.class)
class RepositoryTest {

    private static final String VERSION = "1.2.3";
    private static final String MAVEN = module("maven", "maven-folder-1/pom.xml");
    private static final String GOLANG_ROOT = module("golang", "go.mod");
    private static final String GOLANG_SUBS = module("golang", "go-folder-1/go.mod")
            + module("golang", "go-folder-2/go.mod");

    private static String module(final String type, final String path) {
        return "  - type: " + type + "\n    path: " + path + "\n";
    }

    @Mock
    private RepositoryGate repositoryGateMock;
    @Mock
    private GitHubGateway ghGatewayMock;

    private static final String NAME = "test-repository";

    @Test
    void getName() {
        final Repository repository = repository();
        when(this.repositoryGateMock.getName()).thenReturn(NAME);
        assertThat(repository.getName(), equalTo(NAME));
    }

    @Test
    void getChangelogFile() {
        final String changelog = "Changelog";
        final Repository repository = repository(changelog);
        assertThat(repository.getChangelog(), equalTo(changelog));
    }

    @Test
    void getReleaseLetter() {
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("changes_0.1.0.md")
                .releaseDate(LocalDate.of(2020, 9, 21)).header("GitHub validation and release support")
                .versionNumber("0.1.0").body("## Features").build();
        final Repository repository = repository("# Exasol Release Droid 0.1.0, released 2020-09-21" + LINE_SEPARATOR
                + "Code name: GitHub validation and release support" + LINE_SEPARATOR + "## Features");
        assertThat(repository.getReleaseLetter("0.1.0"), equalTo(releaseLetter));
    }

    @Test
    void getLatestTag() {
        final Repository repository = repository();
        final Optional<String> tag = Optional.of("1.1.1");
        when(this.repositoryGateMock.getLatestTag()).thenReturn(tag);
        assertThat(repository.getLatestTag(), equalTo(tag));
    }

    @Test
    void getBranchName() {
        final Repository repository = repository();
        final String branch = "branch";
        when(this.repositoryGateMock.getBranchName()).thenReturn(branch);
        assertThat(repository.getBranchName(), equalTo(branch));
    }

    @Test
    void isOnDefaultBranch() {
        final Repository repository = repository();
        when(this.repositoryGateMock.isOnDefaultBranch()).thenReturn(false);
        assertThat(repository.isOnDefaultBranch(), equalTo(false));
    }

    @Test
    void getReleaseConfig() {
        final Repository repository = repository("");
        assertThat(repository.getReleaseConfig(), equalTo(Optional.of(ReleaseConfig.builder().build())));
    }

    @Test
    void golangTags() {
        verifyTags(null, VERSION); // no project configuration
        verifyTags(MAVEN, VERSION);
        verifyTags(MAVEN + GOLANG_ROOT, "v" + VERSION);
        verifyTags(MAVEN + GOLANG_SUBS, VERSION, "go-folder-1/v" + VERSION, "go-folder-2/v" + VERSION);
        verifyTags(MAVEN + GOLANG_ROOT + GOLANG_SUBS, "v" + VERSION, "go-folder-1/v" + VERSION,
                "go-folder-2/v" + VERSION);
    }

    @Test
    // [utest->dsn~repository-provides-current-version~1]
    void testGetVersion() {
        when(this.repositoryGateMock.getSingleFileContentAsString(CHANGELOG_FILE)).thenReturn( //
                "# Changelog\n" //
                        + "* [0.3.0](changes_0.3.0.md)\n" //
                        + "* [0.2.0](changes_0.2.0.md)\n" //
                        + "* [0.1.0](changes_0.1.0.md)");
        assertThat(repository().getVersion(), equalTo("0.3.0"));
    }

    @ParameterizedTest
    @ValueSource(strings = { //
            "# Changelog\n", //
            "# Changelog\n ][0.1.0](link)", //
    })
    void testGetVersionInvalidChangelog(final String changelog) {
        when(this.repositoryGateMock.getSingleFileContentAsString(CHANGELOG_FILE)).thenReturn(changelog);
        final RepositoryException exception = assertThrows(RepositoryException.class, repository()::getVersion);
        assertThat(exception.getMessage(), containsString("E-RD-REP-21"));
    }

    @Test
    void testGetRepositoryValidators() {
        final Repository repository = repository();
        assertAll(() -> assertThat(repository.getRepositoryValidators().size(), equalTo(1)), //
                () -> assertTrue(repository.getRepositoryValidators().get(0) instanceof CommonRepositoryValidator));
    }

    @Test
    void testGetPlatformValidators() {
        final Repository repository = repository();
        assertAll(() -> assertThat(repository.getPlatformValidators().size(), equalTo(3)),
                () -> assertTrue(repository.getPlatformValidators().containsKey(GITHUB)),
                () -> assertTrue(repository.getPlatformValidators().containsKey(COMMUNITY)),
                () -> assertTrue(repository.getPlatformValidators().containsKey(JIRA)));
    }

    // ---------------------------------------------------------------------------------------

    private void verifyTags(final String configFile, final String... tags) {
        when(this.repositoryGateMock.getSingleFileContentAsString(CHANGELOG_FILE)).thenReturn("[" + VERSION + "]");
        if (configFile != null) {
            when(this.repositoryGateMock.hasFile(CONFIGURATION_FILE)).thenReturn(true);
            when(this.repositoryGateMock.getSingleFileContentAsString(CONFIGURATION_FILE))
                    .thenReturn("sources:\n" + configFile);
        }
        final Repository repo = repository();
        assertThat(repo.getVersion(), equalTo(VERSION));
        assertThat(repo.getGitTags(), equalTo(Arrays.asList(tags)));
    }

    private Repository repository() {
        return repository(null);
    }

    private Repository repository(final String fileContent) {
        final Repository repo = new GenericRepository(this.repositoryGateMock, this.ghGatewayMock);
        if (fileContent != null) {
            when(this.repositoryGateMock.getSingleFileContentAsString(any())).thenReturn(fileContent);
        }
        return repo;
    }
}