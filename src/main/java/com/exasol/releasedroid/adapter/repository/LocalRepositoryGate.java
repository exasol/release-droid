package com.exasol.releasedroid.adapter.repository;

import static org.eclipse.jgit.lib.Constants.R_TAGS;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.RemoteConfig;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.exception.RepositoryException;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;
import com.exasol.releasedroid.usecases.repository.version.Version;

/**
 * This class represents a local repository.
 */
// [impl->dsn~local-repository~1]
public class LocalRepositoryGate implements RepositoryGate {

    /**
     * Potential tags include "refs/tags/1.2.3", "refs/tags/v1.2.3", "refs/tags/go-module/v1.2.3". The last two variants
     * observed for a golang module in root folder or subfolder "go-module", respectively.
     *
     * @param refs tags retrieved from git repository.
     * @return version represented by latest tag
     */
    static Optional<Version> latestTagFromRefs(final List<Ref> refs) {
        return refs.stream() //
                .map(Ref::getName) //
                .map(Version::fromGitTag) //
                .sorted(Comparator.reverseOrder()) //
                .findFirst();
    }

    /**
     * Create a {@link LocalRepositoryGate} base on a working copy in a local folder and retrieve the full name of the
     * (remote) repository from its Git metadata.
     *
     * @param folder folder containing the files of the local repository
     * @return new instance of {@link LocalRepositoryGate}
     * @throws IOException if accessing the Git metadata of the local repository fails.
     */
    public static LocalRepositoryGate from(final Path folder) throws IOException {
        final String name = getRepoNameFromRemote(Git.open(folder.toFile())).orElse(folder.getFileName().toString());
        return new LocalRepositoryGate(folder.toString(), name);
    }

    private static final Pattern PATTERN = Pattern.compile(".*/([^/]+/[^/]+)\\.git");

    private static Optional<String> getRepoNameFromRemote(final Git git) {
        try {
            final List<RemoteConfig> remotes = git.remoteList().call();
            final Optional<RemoteConfig> origin = remotes.stream().filter(remote -> remote.getName().equals("origin"))
                    .findAny();
            if (origin.isPresent()) {
                final String path = origin.get().getURIs().get(0).getPath();
                final String repoName = PATTERN.matcher(path).replaceFirst("$1");
//                final String[] pathParts = path.split("/");
//                final String repoName = pathParts[pathParts.length - 1].replace(".git", "");
                return Optional.of(repoName);
            } else {
                return Optional.empty();
            }
        } catch (final Exception exception) {
            return Optional.empty();
        }
    }

    private final String localPath;
    private final String fullName;

    /**
     * Create a new instance of {@link LocalRepositoryGate}.
     *
     * @param localPath path to a root of a local repository
     * @param fullName  name of a repository
     */
    public LocalRepositoryGate(final String localPath, final String fullName) {
        this.localPath = localPath;
        this.fullName = fullName;
    }

    @Override
    public String getSingleFileContentAsString(final String filePath) {
        final Path path = Paths.get(this.localPath, filePath);
        try {
            return Files.readString(path);
        } catch (final IOException exception) {
            throw new RepositoryException(ExaError.messageBuilder("E-RD-REP-1")
                    .message("Cannot read a file from the local repository: {{path}}.").parameter("path", path)
                    .mitigation("Please check that the file exists and the local path is correct").toString());
        }
    }

    @Override
    public boolean hasFile(final String filePath) {
        return Files.exists(Paths.get(this.localPath, filePath));
    }

    @Override
    public void updateFileContent(final String filePath, final String newContent, final String commitMessage) {
        throw new UnsupportedOperationException(ExaError.messageBuilder("E-RD-REP-8")
                .message("File updates are not currently supported on a local repository.").toString());
    }

    @Override
    // We assume that there is no a default branch in a local repository as a default branch in our case means a branch
    // on which we create a new tag and start a release process. And a release now is only implemented on the GitHub.
    public boolean isOnDefaultBranch() {
        return false;
    }

    @Override
    public String getBranchName() {
        final File rootDirectory = new File(this.localPath);
        try (final Git git = Git.open(rootDirectory)) {
            return git.getRepository().getBranch();
        } catch (final IOException exception) {
            throw new RepositoryException(ExaError.messageBuilder("E-RD-REP-6")
                    .message("Cannot retrieve a name of a local git branch.").toString());
        }
    }

    @Override
    public Optional<Version> getLatestTag() {
        final File rootDirectory = new File(this.localPath);
        try (final Git git = Git.open(rootDirectory)) {
            return latestTagFromRefs(git.getRepository().getRefDatabase().getRefsByPrefix(R_TAGS));
        } catch (final IOException exception) {
            throw new RepositoryException(
                    ExaError.messageBuilder("E-RD-REP-7")
                            .message("Failed to retrieve latest tag from the local git repository.").toString(),
                    exception);
        }
    }

    @Override
    public String getName() {
        return this.fullName;
    }
}