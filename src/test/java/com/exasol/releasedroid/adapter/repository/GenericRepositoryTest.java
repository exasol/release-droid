package com.exasol.releasedroid.adapter.repository;

import static com.exasol.releasedroid.usecases.repository.BaseRepository.CHANGELOG_FILE_PATH;
import static com.exasol.releasedroid.usecases.request.PlatformName.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;

@ExtendWith(MockitoExtension.class)
class GenericRepositoryTest {
    @Mock
    private RepositoryGate repositoryGateMock;

    @Test
    void testGetVersion() {
        when(this.repositoryGateMock.getSingleFileContentAsString(CHANGELOG_FILE_PATH)).thenReturn( //
                "# Changelog\n" //
                        + "* [0.3.0](changes_0.3.0.md)\n" //
                        + "* [0.2.0](changes_0.2.0.md)\n" //
                        + "* [0.1.0](changes_0.1.0.md)");
        final GenericRepository genericRepository = new GenericRepository(this.repositoryGateMock, null);
        assertThat(genericRepository.getVersion(), equalTo("0.3.0"));
    }

    @Test
    void testGetVersionWithException() {
        when(this.repositoryGateMock.getSingleFileContentAsString(CHANGELOG_FILE_PATH)).thenReturn( //
                "# Changelog\n");
        final GenericRepository genericRepository = new GenericRepository(this.repositoryGateMock, null);
        final RepositoryException exception = assertThrows(RepositoryException.class, genericRepository::getVersion);
        assertThat(exception.getMessage(), containsString("E-RD-REP-21"));

    }

    @Test
    void testGetRepositoryValidators() {
        final GenericRepository genericRepository = new GenericRepository(this.repositoryGateMock, null);
        assertAll(() -> assertThat(genericRepository.getRepositoryValidators().size(), equalTo(1)), //
                () -> assertTrue(
                        genericRepository.getRepositoryValidators().get(0) instanceof CommonRepositoryValidator));
    }

    @Test
    void testGetPlatformValidators() {
        final GenericRepository genericRepository = new GenericRepository(this.repositoryGateMock, null);
        assertAll(() -> assertThat(genericRepository.getPlatformValidators().size(), equalTo(3)),
                () -> assertTrue(genericRepository.getPlatformValidators().containsKey(GITHUB)),
                () -> assertTrue(genericRepository.getPlatformValidators().containsKey(COMMUNITY)),
                () -> assertTrue(genericRepository.getPlatformValidators().containsKey(JIRA)));
    }
}