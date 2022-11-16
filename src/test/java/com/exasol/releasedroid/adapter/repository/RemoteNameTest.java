package com.exasol.releasedroid.adapter.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RemoteListCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.junit.jupiter.api.Test;

class RemoteNameTest {

    private static final List<RemoteConfig> REMOTES = List.of( //
            remoteConfig("name"), //
            remoteConfig("origin", "https://github.com/owner/full-name.git"));

    @Test
    void failure() throws GitAPIException {
        final RemoteListCommand command = mock(RemoteListCommand.class);
        when(command.call()).thenThrow(mock(GitAPIException.class));
        final RemoteName testee = new RemoteName(git(command));
        final Optional<String> optional = assertDoesNotThrow(() -> testee.retrieve());
        assertThat(optional.isEmpty(), is(true));
    }

    @Test
    void getRepoNameFromRemote() throws GitAPIException {
        final RemoteListCommand command = mock(RemoteListCommand.class);
        when(command.call()).thenReturn(REMOTES);
        final RemoteName testee = new RemoteName(git(command));
        assertThat(testee.retrieve().isPresent(), is(true));
        assertThat(testee.retrieve().get(), equalTo("owner/full-name"));
    }

    private Git git(final RemoteListCommand command) {
        final Git git = mock(Git.class);
        when(git.remoteList()).thenReturn(command);
        return git;
    }

    private static RemoteConfig remoteConfig(final String name, final String... urls) {
        final RemoteConfig result = mock(RemoteConfig.class);
        when(result.getName()).thenReturn(name);
        final List<URIish> uris = Stream.of(urls).map(RemoteNameTest::uri).collect(Collectors.toList());
        when(result.getURIs()).thenReturn(uris);
        return result;
    }

    private static URIish uri(final String string) {
        try {
            return new URIish(string);
        } catch (final URISyntaxException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
