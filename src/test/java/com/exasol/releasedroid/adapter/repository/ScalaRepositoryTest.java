package com.exasol.releasedroid.adapter.repository;

import static com.exasol.releasedroid.adapter.repository.ScalaRepository.BUILD_SBT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;

@ExtendWith(MockitoExtension.class)
class ScalaRepositoryTest {
    @Mock
    private RepositoryGate repositoryGateMock;

    @Test
    void testGetVersion() {
        final Repository repository = getRepository();
        final String buildFile = "lazy val root = project.in(file(\".\")).settings(moduleName := \"testing-release-robot\")"
                + ".settings(version := \"0.2.0\").settings(orgSettings)";
        when(this.repositoryGateMock.getSingleFileContentAsString(BUILD_SBT)).thenReturn(buildFile);
        assertThat(repository.getVersion(), equalTo("0.2.0"));
    }

    @Test
    void testGetVersionThrowsException() {
        final Repository repository = getRepository();
        final String buildFile = "lazy val root = project.in(file(\".\")).settings(moduleName := \"testing-release-robot\")"
                + ".settings(orgSettings)";
        when(this.repositoryGateMock.getSingleFileContentAsString(BUILD_SBT)).thenReturn(buildFile);
        final RepositoryException exception = assertThrows(RepositoryException.class, repository::getVersion);
        assertThat(exception.getMessage(), containsString("E-RD-REP-9"));
    }

    private Repository getRepository() {
        return new ScalaRepository(this.repositoryGateMock, null);
    }
}