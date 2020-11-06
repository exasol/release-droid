package com.exasol.releaserobot.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.exasol.releaserobot.usecases.Repository;

class GitHubGitRepositoryTest {
    @Test
    void testGitRepository() {
        final Optional<String> latestTag = Optional.of("1.0.0");
        final Repository branchMock = Mockito.mock(Repository.class);
        final RepositoryTOGOAWAY repository = new GitHubGitRepository(latestTag, branchMock);
        final Optional<String> latestReleaseTag = repository.getLatestTag();
        assertAll(() -> assertThat(latestReleaseTag.isPresent(), equalTo(true)),
                () -> assertThat(latestReleaseTag.get(), equalTo("1.0.0")),
                () -> assertThat(repository.getBranch(), equalTo(branchMock)));

    }
}