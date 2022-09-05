package com.exasol.releasedroid.usecases.repository.version;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.Test;

class RevisionTest {

    private static final String VERSION = "7.8.9";
    private static final Module MAVEN_ROOT = new Module("maven", "a");
    private static final Module GOLANG_ROOT = new Module("golang", "go.mod");
    private static final Module MAVEN_SUB = new Module("maven", "maven-folder-1/pom.xml");
    private static final Module GOLANG_SUB1 = new Module("golang", "go-folder-1/go.mod");
    private static final Module GOLANG_SUB2 = new Module("golang", "go-folder-2/go.mod");

    @Test
    void from() throws IOException {
        assertThat(Revision.from("1.2.3", Collections.emptyList()).modules(), empty());
        final Module module = MAVEN_SUB;
        final List<Map<String, Object>> moduleSpec = List.of(Map.of("type", module.type, "path", module.path));
        assertThat(Revision.from("1.2.3", moduleSpec).modules(), equalTo(List.of(module)));
    }

    @Test
    void versionTest() throws IOException {
        assertThat(version().getVersion(), equalTo(VERSION));
    }

    @Test
    void golangRootModule() throws IOException {
        assertThat(version(MAVEN_ROOT, MAVEN_SUB, GOLANG_SUB1).containsGolangRootModule(), is(false));
        assertThat(version(MAVEN_ROOT, MAVEN_SUB, GOLANG_SUB1, GOLANG_ROOT).containsGolangRootModule(), is(true));
    }

    @Test
    void golangSubFolders() throws IOException {
        assertThat(version(GOLANG_SUB1, GOLANG_SUB2, GOLANG_ROOT).golangSubFolders(),
                containsInAnyOrder("go-folder-1/go.mod", "go-folder-2/go.mod"));
    }

    @Test
    void tags() throws IOException {
        assertThat(version().getTags(), equalTo(List.of(VERSION)));
        assertThat(version(MAVEN_ROOT).getTags(), equalTo(List.of(VERSION)));
        assertThat(version(MAVEN_ROOT, MAVEN_SUB).getTags(), equalTo(List.of(VERSION)));
        assertThat(version(MAVEN_ROOT, GOLANG_ROOT).getTags(), equalTo(List.of("v" + VERSION)));
        assertThat(version(MAVEN_ROOT, GOLANG_SUB1).getTags(), equalTo(List.of(VERSION, "go-folder-1/v" + VERSION)));
        assertThat(version(MAVEN_ROOT, GOLANG_ROOT, GOLANG_SUB1).getTags(),
                equalTo(List.of("v" + VERSION, "go-folder-1/v" + VERSION)));
    }

    private Revision version(final Module... modules) {
        return new Revision(VERSION, Arrays.stream(modules));
    }
}
