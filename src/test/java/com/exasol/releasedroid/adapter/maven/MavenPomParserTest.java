package com.exasol.releasedroid.adapter.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.*;
import java.util.Map;

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
                + "    <properties>" //
                + "        <plugin.version>5.0.4</plugin.version>" //
                + "    </properties>" //
                + "    <build>" //
                + "        <plugins>" //
                + "            <plugin>" //
                + "                <artifactId>some-plugin</artifactId>" //
                + "                <version>0.5.0</version>" //
                + "            </plugin>" //
                + "            <plugin>" //
                + "                <artifactId>maven-assembly-plugin</artifactId>" //
                + "                <version>1.0.3</version>" //
                + "            </plugin>" //
                + "            <plugin>" //
                + "                <artifactId>some-other-plugin</artifactId>" //
                + "                <version>${plugin.version}</version>" //
                + "                <configuration>" //
                + "                </configuration>" //
                + "            </plugin>" //
                + "        </plugins>" //
                + "    </build>" //
                + "</project>";
        final MavenPom mavenPom = getMavenPom(pom);
        final Map<String, MavenPlugin> plugins = mavenPom.getPlugins();
        assertAll(() -> assertThat(mavenPom.getVersion(), equalTo("1.2.3")), //
                () -> assertThat(mavenPom.getArtifactId(), equalTo("my-test-project")), //
                () -> assertThat(mavenPom.hasProperties(), equalTo(true)), //
                () -> assertThat(mavenPom.hasPlugins(), equalTo(true)), //
                () -> assertThat(plugins.size(), equalTo(3)), //
                () -> assertThat(plugins.containsKey("some-plugin"), equalTo(true)), //
                () -> assertThat(plugins.containsKey("maven-assembly-plugin"), equalTo(true)), //
                () -> assertThat(plugins.containsKey("some-other-plugin"), equalTo(true)), //
                () -> assertThat(plugins.get("some-plugin").hasVersion(), equalTo(true)), //
                () -> assertThat(plugins.get("some-plugin").getVersion(), equalTo("0.5.0")), //
                () -> assertThat(plugins.get("maven-assembly-plugin").hasVersion(), equalTo(true)), //
                () -> assertThat(plugins.get("maven-assembly-plugin").getVersion(), equalTo("1.0.3")), //
                () -> assertThat(plugins.get("some-other-plugin").hasVersion(), equalTo(true)), //
                () -> assertThat(plugins.get("some-other-plugin").getVersion(), equalTo("5.0.4")) //
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

    @Test
    void testParseMavenPomWithMissingPluginVersion() throws IOException {
        final String pom = "<project>" //
                + "    <artifactId>my-test-project</artifactId>" //
                + "    <version>1.2.3</version>" //
                + "    <build>" //
                + "        <plugins>" //
                + "            <plugin>" //
                + "                <artifactId>some-plugin</artifactId>" //
                + "            </plugin>" //
                + "        </plugins>" //
                + "    </build>" //
                + "</project>";
        final MavenPom mavenPom = getMavenPom(pom);
        final Map<String, MavenPlugin> plugins = mavenPom.getPlugins();
        assertAll(() -> assertThat(mavenPom.getVersion(), equalTo("1.2.3")), //
                () -> assertThat(mavenPom.getArtifactId(), equalTo("my-test-project")), //
                () -> assertThat(mavenPom.hasProperties(), equalTo(false)), //
                () -> assertThat(mavenPom.hasPlugins(), equalTo(true)), //
                () -> assertThat(plugins.size(), equalTo(1)), //
                () -> assertThat(plugins.containsKey("some-plugin"), equalTo(true)), //
                () -> assertThat(plugins.get("some-plugin").hasVersion(), equalTo(false)) //
        );
    }
}