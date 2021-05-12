package com.exasol.releasedroid.usecases.repository;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.validate.ReleasePlatformValidator;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

@ExtendWith(MockitoExtension.class)
class BaseRepositoryTest {
    private static final String NAME = "test-repository";
    @Mock
    private RepositoryGate repositoryGateMock;

    @Test
    void testGetName() {
        final Repository repository = createRepository("");
        when(this.repositoryGateMock.getName()).thenReturn(NAME);
        assertThat(repository.getName(), equalTo(NAME));
    }

    @Test
    void testGetChangelogFile() {
        final String changelog = "Changelog";
        final Repository repository = createRepository(changelog);
        assertThat(repository.getChangelogFile(), equalTo(changelog));
    }

    @Test
    void testGetReleaseLetter() {
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("changes_0.1.0.md")
                .releaseDate(LocalDate.of(2020, 9, 21)).header("GitHub validation and release support")
                .versionNumber("0.1.0").body("## Features").build();
        final Repository repository = createRepository("# Exasol Release Droid 0.1.0, released 2020-09-21"
                + LINE_SEPARATOR + "Code name: GitHub validation and release support" + LINE_SEPARATOR + "## Features");
        assertThat(repository.getReleaseLetter("0.1.0"), equalTo(releaseLetter));
    }

    @Test
    void testGetLatestTag() {
        final Repository repository = createRepository("");
        final Optional<String> tag = Optional.of("1.1.1");
        when(this.repositoryGateMock.getLatestTag()).thenReturn(tag);
        assertThat(repository.getLatestTag(), equalTo(tag));
    }

    @Test
    void testGetBranchName() {
        final Repository repository = createRepository("");
        final String branch = "branch";
        when(this.repositoryGateMock.getBranchName()).thenReturn(branch);
        assertThat(repository.getBranchName(), equalTo(branch));
    }

    @Test
    void testIsOnDefaultBranch() {
        final Repository repository = createRepository("");
        when(this.repositoryGateMock.isOnDefaultBranch()).thenReturn(false);
        assertThat(repository.isOnDefaultBranch(), equalTo(false));
    }

    @Test
    void testGetReleaseConfig() {
        final Repository repository = createRepository("");
        assertThat(repository.getReleaseConfig(), equalTo(Optional.of(ReleaseConfig.builder().build())));
    }

    private Repository createRepository(final String fileContent) {
        return new DummyRepository(this.repositoryGateMock, fileContent);
    }

    private static final class DummyRepository extends BaseRepository {
        private final String fileContent;

        protected DummyRepository(final RepositoryGate repositoryGate, final String fileContent) {
            super(repositoryGate);
            this.fileContent = fileContent;
        }

        @Override
        public String getSingleFileContentAsString(final String filePath) {
            return this.fileContent;
        }

        @Override
        public String getVersion() {
            return null;
        }

        @Override
        public List<RepositoryValidator> getRepositoryValidators() {
            return null;
        }

        @Override
        public Map<PlatformName, ReleasePlatformValidator> getPlatformValidators() {
            return null;
        }
    }
}