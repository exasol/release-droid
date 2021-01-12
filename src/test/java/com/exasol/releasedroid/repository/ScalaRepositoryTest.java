package com.exasol.releasedroid.repository;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.Repository;

@ExtendWith(MockitoExtension.class)
class ScalaRepositoryTest {
    @Mock
    private RepositoryGate repositoryGateMock;

    @Test
    void testGetVersion() {
        final Repository repository = getRepository();
        final String changelog = "# Changelog" + LINE_SEPARATOR //
                + "* [0.2.0](changes_0.2.0.md)" + LINE_SEPARATOR //
                + "* [0.1.0](changes_0.1.0.md)";
        when(this.repositoryGateMock.getSingleFileContentAsString("doc/changes/changelog.md")).thenReturn(changelog);
        assertThat(repository.getVersion(), equalTo("0.2.0"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "# Changelog" })
    void testGetVersionThrowsException(final String changelog) {
        final Repository repository = getRepository();
        when(this.repositoryGateMock.getSingleFileContentAsString("doc/changes/changelog.md")).thenReturn(changelog);
        final RepositoryException exception = assertThrows(RepositoryException.class, repository::getVersion);
        assertThat(exception.getMessage(), containsString("E-RR-REP-9"));
    }

    private Repository getRepository() {
        return new ScalaRepository(this.repositoryGateMock);
    }
}