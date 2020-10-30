package com.exasol.releaserobot.maven;

import java.net.URI;

import org.json.JSONObject;

import com.exasol.releaserobot.Platform;
import com.exasol.releaserobot.ReleaseMaker;
import com.exasol.releaserobot.Platform.PlatformName;
import com.exasol.releaserobot.UserInput;
import com.exasol.releaserobot.github.GitHubException;
import com.exasol.releaserobot.github.GithubGateway;
import com.exasol.releaserobot.validation.PlatformValidator;

/**
 * This class controls Maven platform.
 */
public class MavenPlatform implements Platform {
	
	private final ReleaseMaker releaseMaker;
	
    /**
     * Create a new instance of {@link MavenPlatform}.
     *
     * @param githubGateway instance of {@link GithubGateway}
     */
    public MavenPlatform(final ReleaseMaker releaseMaker) {
        this.releaseMaker = releaseMaker;
    }

	@Override
	public void release(UserInput userInput) throws GitHubException {
		this.releaseMaker.makeRelease();
	}
	
	@Override
    public PlatformName getPlatformName() {
        return PlatformName.MAVEN;
    }
}