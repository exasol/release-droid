package com.exasol.releasedroid.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.Repository;

@ExtendWith(MockitoExtension.class)
class JavaRepositoryTest {
    @Mock
    private RepositoryGate repositoryGateMock;

    @ParameterizedTest
    @ValueSource(strings = { "<project><version>1.0.0</version><artifactId>project</artifactId></project>", //
            "<project>\n<version>\n1.0.0\n</version>\n<artifactId>project</artifactId></project>",
            "<project>    <version>  1.0.0  </version> <artifactId>project</artifactId>   </project>" })
    // [utest->dsn~repository-provides-current-version~1]
    void testGetVersion(final String pomFile) {
        when(this.repositoryGateMock.getSingleFileContentAsString(anyString())).thenReturn(pomFile);
        final JavaRepository repository = createRepository();
        assertThat(repository.getVersion(), equalTo("1.0.0"));
    }

    private JavaRepository createRepository() {
        return new JavaRepository(this.repositoryGateMock);
    }

    @Test
    // [utest->dsn~repository-provides-deliverables-information~1]
    void testGetDeliverables() {
        final String pomFile = "<project><version>1.0.0</version><artifactId>project</artifactId></project>";
        when(this.repositoryGateMock.getSingleFileContentAsString(anyString())).thenReturn(pomFile);
        final JavaRepository repository = createRepository();
        assertThat(repository.getDeliverables(), equalTo(Map.of("project-1.0.0.jar", "./target/project-1.0.0.jar")));
    }

    @Test
    // [utest->dsn~repository-provides-deliverables-information~1]
    void testGetDeliverablesWithPluginInformationDeprecatedVersionTag() {
        final String pom = "<project>" //
                + " <artifactId>my-test-project</artifactId>" //
                + " <version>1.2.3</version>" //
                + " <properties>" //
                + " <vscjdbc.version>5.0.4</vscjdbc.version>" //
                + " </properties>" //
                + " <build>" //
                + " <plugins>" //
                + " <plugin>" //
                + " <artifactId>maven-assembly-plugin</artifactId>" //
                + " <configuration>" //
                + " <finalName>virtual-schema-dist-${vscjdbc.version}-bundle-${version}</finalName>"
                + " </configuration>" //
                + " </plugin>" //
                + " </plugins>" //
                + " </build>" //
                + "</project>";
        when(this.repositoryGateMock.getSingleFileContentAsString(anyString())).thenReturn(pom);
        final JavaRepository repository = createRepository();
        assertThat(repository.getDeliverables(), equalTo(Map.of("virtual-schema-dist-5.0.4-bundle-1.2.3.jar",
                "./target/virtual-schema-dist-5.0.4-bundle-1.2.3.jar")));
    }

    @Test
    // [utest->dsn~repository-provides-deliverables-information~1]
    void testGetDeliverablesWithPluginInformation() {
        final String pom = "<project>" //
                + " <artifactId>my-test-project</artifactId>" //
                + " <version>1.2.3</version>" //
                + " <properties>" //
                + " <vscjdbc.version>5.0.4</vscjdbc.version>" //
                + " </properties>" //
                + " <build>" //
                + " <plugins>" //
                + " <plugin>" //
                + " <artifactId>maven-assembly-plugin</artifactId>" //
                + " <configuration>" //
                + " <finalName>virtual-schema-dist-${vscjdbc.version}-bundle-${project.version}</finalName>" //
                + " </configuration>" //
                + " </plugin>" //
                + " </plugins>" //
                + " </build>" //
                + "</project>";
        when(this.repositoryGateMock.getSingleFileContentAsString(anyString())).thenReturn(pom);
        final JavaRepository repository = createRepository();
        assertThat(repository.getDeliverables(), equalTo(Map.of("virtual-schema-dist-5.0.4-bundle-1.2.3.jar",
                "./target/virtual-schema-dist-5.0.4-bundle-1.2.3.jar")));
    }

    @Test
    // [utest->dsn~repository-provides-deliverables-information~1]
    void testGetDeliverablesFails() {
        final String pom = "<project>" //
                + " <artifactId>my-test-project</artifactId>" //
                + " <version>1.2.3</version>" //
                + " <properties>" //
                + " </properties>" //
                + " <build>" //
                + " <plugins>" //
                + " <plugin>" //
                + " <artifactId>maven-assembly-plugin</artifactId>" //
                + " <configuration>" //
                + " <finalName>virtual-schema-dist-${vscjdbc.version}-bundle-${version}</finalName>"
                + " </configuration>" //
                + " </plugin>" //
                + " </plugins>" //
                + " </build>" //
                + "</project>";
        when(this.repositoryGateMock.getSingleFileContentAsString(anyString())).thenReturn(pom);
        final JavaRepository repository = createRepository();
        final IllegalStateException exception = assertThrows(IllegalStateException.class, repository::getDeliverables);
        assertThat(exception.getMessage(), containsString("E-RR-REP-3"));
    }

    @Test
    void testGetRepositoryLanguage() {
        final JavaRepository repository = createRepository();
        assertThat(repository.getRepositoryLanguage(), equalTo(Repository.Language.JAVA));
    }
}