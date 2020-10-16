package com.exasol.repository.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kohsuke.github.*;
import org.mockito.Mockito;

import com.exasol.repository.GitBranchContent;
import com.exasol.repository.GitRepositoryException;

class JavaMavenGitBranchContentTest {
    @ParameterizedTest
    @ValueSource(strings = { "<project><version>1.0.0</version><artifactId>project</artifactId></project>", //
            "<project>\n<version>\n1.0.0\n</version>\n<artifactId>project</artifactId></project>",
            "<project>    <version>  1.0.0  </version> <artifactId>project</artifactId>   </project>" })
    // [utest->dsn~gr-provides-current-version~1]
    void testGetVersionWithCaching(final String pomFile) throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final GHBranch branchMock = Mockito.mock(GHBranch.class);
        final String branchName = "my_branch";
        when(ghRepositoryMock.getBranch(branchName)).thenReturn(branchMock);
        when(branchMock.getName()).thenReturn(branchName);
        when(contentMock.read()).thenReturn(new ByteArrayInputStream(pomFile.getBytes()));
        when(ghRepositoryMock.getFileContent(anyString(), anyString())).thenReturn(contentMock);
        final GitBranchContent repository = new JavaMavenGitBranchContent(ghRepositoryMock, branchName);
        assertAll(() -> assertThat(repository.getVersion(), equalTo("1.0.0")),
                () -> assertThat(repository.getVersion(), equalTo("1.0.0")),
                () -> verify(ghRepositoryMock, times(1)).getFileContent(anyString(), anyString()));
    }

    @Test
    // [utest->dsn~gr-provides-deliverables-information~1]
    void testGetDeliverables() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final GHBranch branchMock = Mockito.mock(GHBranch.class);
        final String branchName = "my_branch";
        final String pomFile = "<project><version>1.0.0</version><artifactId>project</artifactId></project>";
        when(ghRepositoryMock.getBranch(branchName)).thenReturn(branchMock);
        when(branchMock.getName()).thenReturn(branchName);
        when(contentMock.read()).thenReturn(new ByteArrayInputStream(pomFile.getBytes()));
        when(ghRepositoryMock.getFileContent(anyString(), anyString())).thenReturn(contentMock);
        final GitBranchContent repository = new JavaMavenGitBranchContent(ghRepositoryMock, branchName);
        assertThat(repository.getDeliverables(), equalTo(Map.of("project-1.0.0.jar", "./target/project-1.0.0.jar")));
    }

    @Test
    void testGetVersionInvalidPom() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final String version = "nothing here";
        final GHBranch branchMock = Mockito.mock(GHBranch.class);
        final String branchName = "my_branch";
        when(ghRepositoryMock.getBranch(branchName)).thenReturn(branchMock);
        when(branchMock.getName()).thenReturn(branchName);
        when(contentMock.read()).thenReturn(new ByteArrayInputStream(version.getBytes()));
        when(ghRepositoryMock.getFileContent(anyString(), anyString())).thenReturn(contentMock);
        assertThrows(GitRepositoryException.class, () -> new JavaMavenGitBranchContent(ghRepositoryMock, branchName));
    }
}