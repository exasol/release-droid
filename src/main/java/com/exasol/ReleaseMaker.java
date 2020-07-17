package com.exasol;

import com.exasol.platform.GitHubRepository;

public class ReleaseMaker {
    private final GitHubRepository repository;
    private final String version;

    public ReleaseMaker(final GitHubRepository repository, final String version) {
        this.repository = repository;
        this.version = version;
    }

    public void makeRelease(final ReleasePlatform platform) {
        if (platform == ReleasePlatform.GITHUB) {
            releaseGitHub();
        } else {
            throw new IllegalArgumentException("Release for platform " + platform + " is not supported");
        }
    }

    private void releaseGitHub() {
        final String changes = getChanges(this.version);
        final int firstLineEnd = changes.indexOf('\n');
        this.repository.release(this.version, changes.substring(0, firstLineEnd), changes.substring(firstLineEnd + 1));
    }

    private String getChanges(final String version) {
        final String changesFileName = "changes-" + version + ".md";
        return this.repository.getSingleFileContentAsString("doc/changes/" + changesFileName);
    }
}