package com.exasol.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.exasol.platform.GitHubRepository;

public class ProjectValidatorJavaTest {
    @Test
    void validateVersionFromPom() {
        final GitHubRepository repositoryMock = mock(GitHubRepository.class);
        final String pom = "<version>1.0.1</version>";
        when(repositoryMock.getSingleFileContentAsString("pom.xml")).thenReturn(pom);
        final ProjectValidatorJava validator = new ProjectValidatorJava(repositoryMock);
        assertThat(validator.getVersion(), equalTo("1.0.1"));
    }

    @Test
    void validateVersionFromPomMissing() {
        final GitHubRepository repositoryMock = mock(GitHubRepository.class);
        final String pom = "";
        when(repositoryMock.getSingleFileContentAsString("pom.xml")).thenReturn(pom);
        final ProjectValidatorJava validator = new ProjectValidatorJava(repositoryMock);
        final IllegalStateException exception = assertThrows(IllegalStateException.class, validator::getVersion);
        assertThat(exception.getMessage(), containsString("Cannot find a project version in pom.xml file."));
    }
}