package com.exasol.releasedroid.usecases;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;

import java.io.*;
import java.util.*;

import org.kohsuke.github.*;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.repository.*;

/**
 * Base implementation of GitHub-based repository.
 */
public abstract class BaseRepository implements Repository {
    private static final String CHANGELOG_FILE_PATH = "doc/changes/changelog.md";
    private final GHRepository ghRepository;
    private final GHBranch branch;
    private final Optional<String> latestTag;
    private final Map<String, ReleaseLetter> releaseLetters = new HashMap<>();
    private final String fullName;

    /**
     * Create a new instance of {@link BaseRepository}.
     * 
     * @param ghRepository an instance of {@link GHRepository}
     * @param branchName   name of a branch to get content from
     * @param fullName     fully qualified name of the repository
     * @param latestTag    latest release tag
     */
    // [impl->dsn~repository-retrieves-branch-content~1]
    protected BaseRepository(final GHRepository ghRepository, final String branchName, final String fullName,
            final Optional<String> latestTag) {
        this.ghRepository = ghRepository;
        this.branch = getBranchByName(branchName);
        this.latestTag = latestTag;
        this.fullName = fullName;
    }

    private GHBranch getBranchByName(final String branchName) {
        try {
            return this.ghRepository.getBranch(branchName);
        } catch (final IOException exception) {
            throw new RepositoryException(ExaError.messageBuilder("E-REP-GH-3") //
                    .message("Cannot find a branch {{branchName}}.") //
                    .parameter("branchName", branchName) //
                    .mitigation("Please check if you specified a correct branch.").toString(), exception);
        }
    }

    @Override
    public String getSingleFileContentAsString(final String filePath) {
        try {
            final GHContent content = getFileContent(filePath);
            return getContent(content.read());
        } catch (final IOException exception) {
            throw new RepositoryException(ExaError.messageBuilder("E-REP-GH-2")
                                                  .message("Cannot find or read the file {{filePath}} in the repository {{repositoryName}}.")
                                                  .parameter("filePath", filePath) //
                                                  .parameter("repositoryName", this.ghRepository.getName()) //
                                                  .mitigation("Please add this file according to the user guide.").toString(), exception);
        }
    }

    private GHContent getFileContent(final String filePath) throws IOException {
        return this.ghRepository.getFileContent(filePath, this.branch.getName());
    }

    @Override
    public void updateFileContent(final String filePath, final String newContent, final String commitMessage) {
        try {
            this.getFileContent(filePath).update(newContent, commitMessage, this.branch.getName());
        } catch (final IOException exception) {
            throw new RepositoryException(ExaError.messageBuilder("E-REP-GH-6")
                                                  .message("Cannot update the file {{filePath}} in the repository {{repositoryName}}.")
                                                  .parameter("filePath", filePath) //
                                                  .parameter("repositoryName", this.ghRepository.getName()) //
                                                  .mitigation("Please add this file according to the user guide.").toString(), exception);
        }
    }

    private String getContent(final InputStream stream) throws IOException {
        final StringBuilder result = new StringBuilder();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                result.append(LINE_SEPARATOR);
                line = reader.readLine();
            }
        }
        return result.toString().stripTrailing();
    }

    @Override
    public boolean isOnDefaultBranch() {
        return this.ghRepository.getDefaultBranch().equals(this.branch.getName());
    }

    @Override
    public String getBranchName() {
        return this.branch.getName();
    }

    @Override
    public String getChangelogFile() {
        return getSingleFileContentAsString(CHANGELOG_FILE_PATH);
    }

    @Override
    public ReleaseLetter getReleaseLetter(final String version) {
        if (!this.releaseLetters.containsKey(version)) {
            final String fileName = "changes_" + version + ".md";
            final String filePath = "doc/changes/" + fileName;
            final String fileContent = getSingleFileContentAsString(filePath);
            this.releaseLetters.put(version, new ReleaseLetterParser(fileName, fileContent).parse());
        }
        return this.releaseLetters.get(version);
    }

    @Override
    public String getFullName() {
        return this.fullName;
    }

    @Override
    public Optional<String> getLatestTag() {
        return this.latestTag;
    }
}
