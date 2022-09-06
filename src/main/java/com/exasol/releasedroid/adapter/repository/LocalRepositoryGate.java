package com.exasol.releasedroid.adapter.repository;

import static org.eclipse.jgit.lib.Constants.R_TAGS;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;

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
                    .message("Cannot read a file from the local repository: {{path}}.")
                    .parameter("path", this.localPath + filePath)
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