package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GitHubReleaseInfoTest {

    @Test
    void tagUrl() {
        final String name = "name";
        final String version = "1.2.3";
        final GitHubReleaseInfo info = GitHubReleaseInfo.builder().repositoryName(name).version(version).build();
        assertThat(info.getTagUrl(), equalTo(GitHubReleaseInfo.getTagUrl(name, version)));
    }

    @Test
    void htmlUrl() throws MalformedURLException {
        final URL url = new URL("http://www.abc");
        final GitHubReleaseInfo info = GitHubReleaseInfo.builder().htmlUrl(url).build();
        assertThat(info.getHtmlUrl(), equalTo(url));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void draft(final boolean draft) {
        final GitHubReleaseInfo info = GitHubReleaseInfo.builder().draft(draft).build();
        assertThat(info.isDraft(), is(draft));
    }

    @Test
    void noAdditionalTags() {
        final GitHubReleaseInfo info = GitHubReleaseInfo.builder().build();
        assertThat(info.additionalTagsReport().isEmpty(), is(true));
    }

    @Test
    void oneAdditionalTag() {
        final GitHubReleaseInfo info = GitHubReleaseInfo.builder().additionalTags(List.of("v1.2.3")).build();
        assertThat(info.additionalTagsReport().get(), equalTo("1 additional tag: v1.2.3"));
    }

    @Test
    void multipleAdditionalTags() {
        final GitHubReleaseInfo info = GitHubReleaseInfo.builder().additionalTags(List.of("v1.2.3", "go-module/v1.2.3"))
                .build();
        assertThat(info.additionalTagsReport().get(), equalTo("2 additional tags: v1.2.3, go-module/v1.2.3"));
    }

}
