package com.exasol.github;

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
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.mockito.Mockito;

class JavaMavenProjectTest {
    @ParameterizedTest
    @ValueSource(strings = { "<project><version>1.0.0</version></project>", //
            "<project>\n<version>\n1.0.0\n</version>\n</project>",
            "<project>    <version>  1.0.0  </version>   </project>" })
    void testGetVersionWithCaching(final String pomFile) throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        when(contentMock.getContent()).thenReturn(pomFile);
        when(ghRepositoryMock.getFileContent(anyString())).thenReturn(contentMock);
        final GitHubRepository repository = new JavaMavenProject(ghRepositoryMock, "");
        assertAll(() -> assertThat(repository.getVersion(), equalTo("1.0.0")),
                () -> assertThat(repository.getVersion(), equalTo("1.0.0")),
                () -> verify(ghRepositoryMock, times(1)).getFileContent(anyString()));
    }

    @Test
    void testGetVersionInvalidPom() throws IOException {
        final GHRepository ghRepositoryMock = Mockito.mock(GHRepository.class);
        final GHContent contentMock = Mockito.mock(GHContent.class);
        final String version = "nothing here";
        when(contentMock.getContent()).thenReturn(version);
        when(ghRepositoryMock.getFileContent(anyString())).thenReturn(contentMock);
        final GitHubRepository repository = new JavaMavenProject(ghRepositoryMock, "");
        assertThrows(GitHubException.class, repository::getVersion);
    }
}