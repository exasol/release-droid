package com.exasol.releasedroid.adapter.repository;

import static org.eclipse.jgit.lib.Constants.R_TAGS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.lib.Ref;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.version.Version;

class LocalRepositoryGateTest {

    @Test
    void latestTagFromRefs() {
        final List<Ref> refs = List.of( //
                mockRef("1.2.3"), //
                mockRef("v1.2.1"), //
                mockRef("xxx"), //
                mockRef("go-sufolder/9.9.9-a"), //
                mockRef("go-subfolder/v1.0.1"));
        final Optional<Version> actual = LocalRepositoryGate.latestTagFromRefs(refs);
        assertAll(() -> assertThat(actual.isPresent(), is(true)), //
                () -> assertThat(actual.get(), equalTo(Version.parse("1.2.3"))));
    }

    @Test
    void emptyRefs() {
        final Optional<Version> actual = LocalRepositoryGate.latestTagFromRefs(List.of());
        assertThat(actual.isPresent(), is(false));
    }

    @Test
    void from(@TempDir final Path temp) throws Exception {
        final String configuration = "[remote \"origin\"]\n" //
                + "url = https://github.com/owner/full-name.git\n";
        RemoteNameTest.emulateGitProject(temp, configuration);
        final LocalRepositoryGate testee = LocalRepositoryGate.from(temp);
        assertThat(testee.getName(), equalTo("owner/full-name"));
    }

    @Test
    void fileContentSuccess(@TempDir final Path temp) throws Exception {
        final String filename = "file.txt";
        final String content = "sample content";
        Files.writeString(temp.resolve(filename), content);
        final LocalRepositoryGate testee = new LocalRepositoryGate(temp.toString(), "");
        assertThat(testee.getSingleFileContentAsString(filename), equalTo(content));
    }

    @Test
    void fileContentFailure() throws Exception {
        final LocalRepositoryGate testee = new LocalRepositoryGate("/non/existing/folder", "");
        final Exception e = assertThrows(RepositoryException.class,
                () -> testee.getSingleFileContentAsString("file.txt"));
        assertThat(e.getMessage(), startsWith("E-RD-REP-1: Cannot read a file from the local repository"));
    }

    @Test
    void branchNameSuccess(@TempDir final Path temp) throws Exception {
        RemoteNameTest.emulateGitProject(temp, "");
        final LocalRepositoryGate testee = new LocalRepositoryGate(temp.toString(), "");
        assertThat(testee.getBranchName(), equalTo("refactor/380-get-project-name"));
    }

    @Test
    void branchNameFailure() {
        final LocalRepositoryGate testee = new LocalRepositoryGate("/non/existing/folder", "");
        final Exception e = assertThrows(RepositoryException.class, () -> testee.getBranchName());
        assertThat(e.getMessage(), equalTo("E-RD-REP-6: Cannot retrieve a name of a local git branch."));
    }

    private Ref mockRef(final String name) {
        final Ref ref = Mockito.mock(Ref.class);
        when(ref.getName()).thenReturn(R_TAGS + name);
        return ref;
    }
}
