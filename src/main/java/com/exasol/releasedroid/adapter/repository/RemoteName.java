package com.exasol.releasedroid.adapter.repository;

import java.util.Optional;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.RemoteConfig;

/**
 * Retrieve remote name of a Git repository with owner. This is relevant if local folder name is different from original
 * name of repository on GitHub.
 */
public class RemoteName {

    private static final Pattern PATTERN = Pattern.compile(".*/([^/]+/[^/]+)\\.git");
    private final Git git;

    public RemoteName(final Git git) {
        this.git = git;
    }

    /**
     * @return name with owner
     */
    public Optional<String> retrieve() {
        try {
            return this.git.remoteList().call().stream() //
                    .filter(this::isOrigin) //
                    .findAny() //
                    .flatMap(this::nameWithOwner);
        } catch (final GitAPIException exception) {
            return Optional.empty();
        }
    }

    private boolean isOrigin(final RemoteConfig remote) {
        return remote.getName().equals("origin");
    }

    private Optional<String> nameWithOwner(final RemoteConfig remote) {
        final String path = remote.getURIs().get(0).getPath();
        final String repoName = PATTERN.matcher(path).replaceFirst("$1");
        return Optional.of(repoName);
    }
}
