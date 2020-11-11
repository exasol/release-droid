package com.exasol.releaserobot.repository.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.*;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.PluginExecution;
import org.junit.jupiter.api.Test;

class MavenPomParserTest {
    @Test
    void testParsePom() throws IOException {
        final String pom = "<project>" //
                + "<artifactId>my-test-project</artifactId>" //
                + "<version>1.2.3</version>" //
                + "</project>";
        final MavenPom mavenPom = getMavenPom(pom);
        assertAll(() -> assertThat(mavenPom.hasArtifactId(), equalTo(true)),
                () -> assertThat(mavenPom.hasVersion(), equalTo(true)),
                () -> assertThat(mavenPom.hasProperties(), equalTo(false)),
                () -> assertThat(mavenPom.hasPlugins(), equalTo(false)),
                () -> assertThat(mavenPom.getVersion(), equalTo("1.2.3")),
                () -> assertThat(mavenPom.getArtifactId(), equalTo("my-test-project")));
    }

    private MavenPom getMavenPom(final String pom) throws IOException {
        final File pomFile = getFile(pom);
        return new MavenPomParser(pomFile).parse();
    }

    private File getFile(final String pomString) throws IOException {
        final File tempPomFile = File.createTempFile("pomProjection", null);
        tempPomFile.deleteOnExit();
        try (final BufferedWriter out = new BufferedWriter(new FileWriter(tempPomFile))) {
            out.write(pomString);
        }
        return tempPomFile;
    }

    @Test
    void testParseMavenPomWithPlugins() throws IOException {
        final String pom = "<project>" //
                + "    <artifactId>my-test-project</artifactId>" //
                + "    <version>1.2.3</version>" //
                + "    <build>" //
                + "        <plugins>" //
                + "            <plugin>" //
                + "                <artifactId>some-plugin</artifactId>" //
                + "            </plugin>" //
                + "            <plugin>" //
                + "                <artifactId>maven-assembly-plugin</artifactId>" //
                + "                 <configuration>" //
                + "                    <finalName>virtual-schema-dist-${vscjdbc.version}-bundle-${version}</finalName>"
                + "                </configuration>" //
                + "            </plugin>" //
                + "            <plugin>" //
                + "                <artifactId>some-other-plugin</artifactId>" //
                + "                 <configuration>" //
                + "                </configuration>" //
                + "            </plugin>" //
                + "        </plugins>" //
                + "    </build>" //
                + "</project>";
        final MavenPom mavenPom = getMavenPom(pom);
        final List<MavenPlugin> plugins = mavenPom.getPlugins();
        assertAll(() -> assertThat(mavenPom.getVersion(), equalTo("1.2.3")), //
                () -> assertThat(mavenPom.getArtifactId(), equalTo("my-test-project")), //
                () -> assertThat(mavenPom.hasProperties(), equalTo(false)), //
                () -> assertThat(mavenPom.hasPlugins(), equalTo(true)), //
                () -> assertThat(plugins.size(), equalTo(3)), //
                () -> assertThat(plugins.get(0).getArtifactId(), equalTo("some-plugin")), //
                () -> assertThat(plugins.get(0).hasConfiguration(), equalTo(false)), //
                () -> assertThat(plugins.get(1).getArtifactId(), equalTo("maven-assembly-plugin")), //
                () -> assertThat(plugins.get(1).hasConfiguration(), equalTo(true)), //
                () -> assertThat(plugins.get(2).getArtifactId(), equalTo("some-other-plugin")), //
                () -> assertThat(plugins.get(2).hasConfiguration(), equalTo(true)) //
        );
    }

    @Test
    void testParseMavenPomWithProperties() throws IOException {
        final String pom = "<project>" //
                + "    <artifactId>my-test-project</artifactId>" //
                + "    <version>1.2.3</version>" //
                + "    <properties>" //
                + "        <vscjdbc.version>5.0.4</vscjdbc.version>" //
                + "    </properties>" //
                + "</project>";
        final MavenPom mavenPom = getMavenPom(pom);
        final Map<String, String> properties = mavenPom.getProperties();
        assertAll(() -> assertThat(mavenPom.getVersion(), equalTo("1.2.3")),
                () -> assertThat(mavenPom.getArtifactId(), equalTo("my-test-project")),
                () -> assertThat(mavenPom.hasProperties(), equalTo(true)),
                () -> assertThat(properties.size(), equalTo(1)),
                () -> assertThat(properties.containsKey("vscjdbc.version"), equalTo(true)),
                () -> assertThat(properties.get("vscjdbc.version"), equalTo("5.0.4")));
    }

    @Test
    void testParseMavenPluginsWithExecutions() throws IOException {
        final String pom = "<project>" //
                + "    <artifactId>my-test-project</artifactId>" //
                + "    <version>1.2.3</version>" //
                + "    <build>" //
                + "        <plugins>" //
                + "            <plugin>" //
                + "                <groupId>org.apache.maven.plugins</groupId>" //
                + "                <artifactId>maven-gpg-plugin</artifactId>" //
                + "                <version>1.6</version>" //
                + "                <executions>" //
                + "                    <execution>" //
                + "                        <id>sign-artifacts</id>" //
                + "                        <phase>verify</phase>" //
                + "                        <goals>" //
                + "                            <goal>sign</goal>" //
                + "                        </goals>" //
                + "                        <configuration>" //
                + "                            <gpgArguments>" //
                + "                                <arg>--pinentry-mode</arg>" //
                + "                                <arg>loopback</arg>" //
                + "                            </gpgArguments>" //
                + "                        </configuration>" //
                + "                    </execution>" //
                + "                </executions>" //
                + "            </plugin>" //
                + "        </plugins>" //
                + "    </build>" //
                + "</project>";
        final MavenPom mavenPom = getMavenPom(pom);
        final List<MavenPlugin> plugins = mavenPom.getPlugins();
        final List<PluginExecution> executions = plugins.get(0).getExecutions();
        assertAll(() -> assertThat(mavenPom.hasPlugins(), equalTo(true)), //
                () -> assertThat(plugins.size(), equalTo(1)), //
                () -> assertThat(plugins.get(0).getArtifactId(), equalTo("maven-gpg-plugin")), //
                () -> assertThat(plugins.get(0).hasConfiguration(), equalTo(false)), //
                () -> assertThat(plugins.get(0).hasExecutions(), equalTo(true)), //
                () -> assertThat(executions.size(), equalTo(1)), //
                () -> assertThat(executions.get(0).getId(), equalTo("sign-artifacts")), //
                () -> assertThat(executions.get(0).getConfiguration().toString(), containsString("--pinentry-mode")) //
        );
    }

    @Test
    void testParseMavenPomEmpty() throws IOException {
        final String pom = "<project>" //
                + "    <artifactId></artifactId>" //
                + "    <version></version>" //
                + "</project>";
        final MavenPom mavenPom = getMavenPom(pom);
        assertAll(() -> assertThat(mavenPom.hasVersion(), equalTo(false)),
                () -> assertThat(mavenPom.hasArtifactId(), equalTo(false)),
                () -> assertThat(mavenPom.hasPlugins(), equalTo(false)),
                () -> assertThat(mavenPom.hasProperties(), equalTo(false)));
    }
}