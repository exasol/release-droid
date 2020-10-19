package com.exasol.releaserobot.repository.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MavenPomParserTest {
    @ParameterizedTest
    @ValueSource(strings = { //
            "<project>" //
                    + "<artifactId>my-test-project</artifactId>" //
                    + "<version>1.2.3</version>" //
                    + "</project>", //

            "<project>" //
                    + "<artifactId>my-test-project</artifactId>" //
                    + "<version>1.2.3</version>" //
                    + "    <build>" //
                    + "        <plugins>" //
                    + "            <plugin>" //
                    + "                <artifactId>some-plugin</artifactId>\n" //
                    + "            </plugin>" //
                    + "        </plugins>" //
                    + "    </build>" //
                    + "</project>", //

            "<project>" //
                    + "    <artifactId>my-test-project</artifactId>" //
                    + "    <version>1.2.3</version>" //
                    + "    <build>" //
                    + "        <plugins>" //
                    + "            <plugin>" //
                    + "                <artifactId>maven-assembly-plugin</artifactId>" //
                    + "            </plugin>" //
                    + "        </plugins>" //
                    + "    </build>" //
                    + "</project>",

            "<project>" //
                    + "    <artifactId>my-test-project</artifactId>" //
                    + "    <version>1.2.3</version>" //
                    + "    <build>" //
                    + "        <plugins>" //
                    + "            <plugin>" //
                    + "                <artifactId>maven-assembly-plugin</artifactId>" //
                    + "                 <configuration>" //
                    + "                </configuration>" //
                    + "            </plugin>" //
                    + "        </plugins>" //
                    + "    </build>" //
                    + "</project>" })
    void testParseMavenPom(final String pom) {
        final MavenPom mavenPom = new MavenPomParser(pom).parse();
        assertAll(() -> assertThat(mavenPom.getVersion(), equalTo("1.2.3")),
                () -> assertThat(mavenPom.getArtifactId(), equalTo("my-test-project")),
                () -> assertThat(mavenPom.getDeliverableName(), equalTo("my-test-project-1.2.3")));
    }

    @Test
    void testParseMavenPomWithCompilerPlugin() {
        final String pom = "<project>" //
                + "    <artifactId>my-test-project</artifactId>" //
                + "    <version>1.2.3</version>" //
                + "    <properties>" //
                + "        <vscjdbc.version>5.0.4</vscjdbc.version>" //
                + "    </properties>" //
                + "    <build>" //
                + "        <plugins>" //
                + "            <plugin>" //
                + "                <artifactId>some-plugin</artifactId>" //
                + "            </plugin>" //
                + "            <plugin>" //
                + "                <artifactId>some-other-plugin</artifactId>" //
                + "            </plugin>" //
                + "            <plugin>" //
                + "                <artifactId>maven-assembly-plugin</artifactId>" //
                + "                 <configuration>" //
                + "                    <finalName>virtual-schema-dist-${vscjdbc.version}-bundle-${version}</finalName>"
                + "                </configuration>" //
                + "            </plugin>" //
                + "        </plugins>" //
                + "    </build>" //
                + "</project>";
        final MavenPom mavenPom = new MavenPomParser(pom).parse();
        assertAll(() -> assertThat(mavenPom.getVersion(), equalTo("1.2.3")),
                () -> assertThat(mavenPom.getArtifactId(), equalTo("my-test-project")),
                () -> assertThat(mavenPom.getDeliverableName(), equalTo("virtual-schema-dist-5.0.4-bundle-1.2.3")));
    }

    @ParameterizedTest
    @ValueSource(strings = { //
            "<project></project>", //

            "<project>" //
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
                    + "</project>", //

            "<project>" //
                    + "    <artifactId>my-test-project</artifactId>" //
                    + "    <version>1.2.3</version>" //
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
                    + "</project>" //
    })
    void testParseMavenPomInvalidPomNoElements(final String pom) {
        final MavenPomParser mavenPomParser = new MavenPomParser(pom);
        final IllegalStateException exception = assertThrows(IllegalStateException.class, mavenPomParser::parse);
        assertThat(exception.getMessage(), containsString("E-REP-MAV-2"));
    }

    @Test
    void testParseMavenPomInvalidPomEmptyElements() {
        final String pom = "<project>" //
                + "    <artifactId></artifactId>" //
                + "    <version></version>" //
                + "</project>";
        final MavenPomParser mavenPomParser = new MavenPomParser(pom);
        final IllegalStateException exception = assertThrows(IllegalStateException.class, mavenPomParser::parse);
        assertThat(exception.getMessage(), containsString("E-REP-MAV-2"));
    }
}