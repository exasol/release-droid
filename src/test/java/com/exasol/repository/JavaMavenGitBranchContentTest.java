package com.exasol.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kohsuke.github.*;
import org.mockito.Mockito;

import com.exasol.github.GitHubException;

class JavaMavenGitBranchContentTest {
    @ParameterizedTest
    @ValueSource(strings = { "<project><version>1.0.0</version></project>", //
            "<project>\n<version>\n1.0.0\n</version>\n</project>",
            "<project>    <version>  1.0.0  </version>   </project>" })
    // [utest->dsn~gr-provides-current-version~1]
    void testGetVersionWithCaching(final String pomFile) throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final GHBranch branchMock = Mockito.mock(GHBranch.class);
        final String branchName = "my_branch";
        when(ghRepositoryMock.getBranch(branchName)).thenReturn(branchMock);
        when(branchMock.getName()).thenReturn(branchName);
        when(contentMock.getContent()).thenReturn(pomFile);
        when(ghRepositoryMock.getFileContent(anyString(), anyString())).thenReturn(contentMock);
        final GitBranchContent repository = new JavaMavenGitBranchContent(ghRepositoryMock, branchName);
        assertAll(() -> assertThat(repository.getVersion(), equalTo("1.0.0")),
                () -> assertThat(repository.getVersion(), equalTo("1.0.0")),
                () -> verify(ghRepositoryMock, times(1)).getFileContent(anyString(), anyString()));
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
        when(contentMock.getContent()).thenReturn(version);
        when(ghRepositoryMock.getFileContent(anyString(), anyString())).thenReturn(contentMock);
        assertThrows(GitHubException.class, () -> new JavaMavenGitBranchContent(ghRepositoryMock, branchName));
    }
}