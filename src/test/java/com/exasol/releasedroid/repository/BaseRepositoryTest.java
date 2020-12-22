package com.exasol.releasedroid.repository;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.releasedroid.usecases.Repository;

class BaseRepositoryTest {
    private static final String NAME = "test-repository";

    @Test
    void testGetName() {
        final Repository repository = createRepository("");
        assertThat(repository.getName(), equalTo(NAME));
    }

    @Test
    void testGetChangelogFile() {
        final String changelog = "Changelog";
        final Repository repository = createRepository(changelog);
        assertThat(repository.getChangelogFile(), equalTo(changelog));
    }

    @Test
    void testGetReleaseLetter() {
        final ReleaseLetter releaseLetter = ReleaseLetter.builder("changes_0.1.0.md")
                .releaseDate(LocalDate.of(2020, 9, 21)).header("GitHub validation and release support")
                .versionNumber("0.1.0").body("## Features").build();
        final Repository repository = createRepository("# Exasol Release Droid 0.1.0, released 2020-09-21"
                + LINE_SEPARATOR + "Code name: GitHub validation and release support" + LINE_SEPARATOR + "## Features");
        assertThat(repository.getReleaseLetter("0.1.0"), equalTo(releaseLetter));
    }

    @ParameterizedTest
    @ValueSource(strings = { "<project><version>1.0.0</version><artifactId>project</artifactId></project>", //
            "<project>\n<version>\n1.0.0\n</version>\n<artifactId>project</artifactId></project>",
            "<project>    <version>  1.0.0  </version> <artifactId>project</artifactId>   </project>" })
    // [utest->dsn~repository-provides-current-version~1]
    void testGetVersion(final String pomFile) {
        final Repository repository = createRepository(pomFile);
        assertThat(repository.getVersion(), equalTo("1.0.0"));
    }

    private Repository createRepository(final String fileContent) {
        return new DummyRepository(NAME, fileContent);
    }

    @Test
    // [utest->dsn~repository-provides-deliverables-information~1]
    void testGetDeliverables() {
        final String pomFile = "<project><version>1.0.0</version><artifactId>project</artifactId></project>";
        final Repository repository = createRepository(pomFile);
        assertThat(repository.getDeliverables(), equalTo(Map.of("project-1.0.0.jar", "./target/project-1.0.0.jar")));
    }

    @Test
    // [utest->dsn~repository-provides-deliverables-information~1]
    void testGetDeliverablesWithPluginInformationDeprecatedVersionTag() {
        final String pom = "<project>" //
                + "    <artifactId>my-test-project</artifactId>" //
                + "    <version>1.2.3</version>" //
                + "    <properties>" //
                + "        <vscjdbc.version>5.0.4</vscjdbc.version>" //
                + "    </properties>" //
                + "    <build>" //
                + "        <plugins>" //
                + "            <plugin>" //
                + "                <artifactId>maven-assembly-plugin</artifactId>" //
                + "                 <configuration>" //
                + "                    <finalName>virtual-schema-dist-${vscjdbc.version}-bundle-${version}</finalName>"
                + "                </configuration>" //
                + "            </plugin>" //
                + "        </plugins>" //
                + "    </build>" //
                + "</project>";
        final Repository repository = createRepository(pom);
        assertThat(repository.getDeliverables(), equalTo(Map.of("virtual-schema-dist-5.0.4-bundle-1.2.3.jar",
                "./target/virtual-schema-dist-5.0.4-bundle-1.2.3.jar")));
    }

    @Test
    // [utest->dsn~repository-provides-deliverables-information~1]
    void testGetDeliverablesWithPluginInformation() {
        final String pom = "<project>" //
                + "    <artifactId>my-test-project</artifactId>" //
                + "    <version>1.2.3</version>" //
                + "    <properties>" //
                + "        <vscjdbc.version>5.0.4</vscjdbc.version>" //
                + "    </properties>" //
                + "    <build>" //
                + "        <plugins>" //
                + "            <plugin>" //
                + "                <artifactId>maven-assembly-plugin</artifactId>" //
                + "                 <configuration>" //
                + "                    <finalName>virtual-schema-dist-${vscjdbc.version}-bundle-${project.version}</finalName>" //
                + "                </configuration>" //
                + "            </plugin>" //
                + "        </plugins>" //
                + "    </build>" //
                + "</project>";
        final Repository repository = createRepository(pom);
        assertThat(repository.getDeliverables(), equalTo(Map.of("virtual-schema-dist-5.0.4-bundle-1.2.3.jar",
                "./target/virtual-schema-dist-5.0.4-bundle-1.2.3.jar")));
    }

    @Test
    // [utest->dsn~repository-provides-deliverables-information~1]
    void testGetDeliverablesFails() {
        final String pom = "<project>" //
                + "    <artifactId>my-test-project</artifactId>" //
                + "    <version>1.2.3</version>" //
                + "    <properties>" //
                + "    </properties>" //
                + "    <build>" //
                + "        <plugins>" //
                + "            <plugin>" //
                + "                <artifactId>maven-assembly-plugin</artifactId>" //
                + "                 <configuration>" //
                + "                    <finalName>virtual-schema-dist-${vscjdbc.version}-bundle-${version}</finalName>"
                + "                </configuration>" //
                + "            </plugin>" //
                + "        </plugins>" //
                + "    </build>" //
                + "</project>";
        final Repository repository = createRepository(pom);
        final IllegalStateException exception = assertThrows(IllegalStateException.class, repository::getDeliverables);
        assertThat(exception.getMessage(), containsString("E-RR-REP-3"));
    }

    private static final class DummyRepository extends BaseRepository {
        private final String fileContent;

        protected DummyRepository(final String repositoryName, final String fileContent) {
            super(repositoryName);
            this.fileContent = fileContent;
        }

        @Override
        public String getSingleFileContentAsString(final String filePath) {
            return this.fileContent;
        }

        @Override
        public void updateFileContent(final String filePath, final String newContent, final String commitMessage) {
        }

        @Override
        public boolean isOnDefaultBranch() {
            return false;
        }

        @Override
        public String getBranchName() {
            return null;
        }

        @Override
        public Optional<String> getLatestTag() {
            return Optional.empty();
        }
    }
}