package com.exasol.releasedroid.output.guide;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.RELEASE_DROID_CREDENTIALS;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.adapter.github.*;
import com.exasol.releasedroid.adapter.repository.GenericRepository;
import com.exasol.releasedroid.adapter.repository.LocalRepositoryGate;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.repository.RepositoryGate;

class ReleaseGuideTest {

    private static final Path PROJECTS = Paths.get("c:/Huge/Workspaces/Git");

    @Test
    void test() throws Exception {
        final String folderName = "extension-manager"; // keeper "exasol-testcontainers";
        final RepositoryGate gate = LocalRepositoryGate.from(PROJECTS.resolve(folderName));
        final Repository repo = new GenericRepository(gate, gitHubGateway());
        ReleaseGuide.from(repo).write(Paths.get("c:/HOME/Doc/221111-Release-Guide/sample-guide.html"));
    }

    private GitHubGateway gitHubGateway() {
        final XProperties properties = new XProperties(Paths.get(RELEASE_DROID_CREDENTIALS));
        return new GitHubAPIAdapter(new GitHubConnectorImpl(properties));
    }
}
