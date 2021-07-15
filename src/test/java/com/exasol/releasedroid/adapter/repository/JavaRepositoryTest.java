package com.exasol.releasedroid.adapter.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.repository.RepositoryGate;

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
        return new JavaRepository(this.repositoryGateMock, null);
    }
}