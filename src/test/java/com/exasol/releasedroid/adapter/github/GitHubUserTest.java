package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.adapter.github.GitHubUser;

class GitHubUserTest {
    private static GitHubUser user;

    @BeforeAll
    static void setUp() {
        user = new GitHubUser("user", "token");
    }

    @Test
    void testGetUsername() {
        assertThat(user.getUsername(), equalTo("user"));
    }

    @Test
    void testGetToken() {
        assertThat(user.getToken(), equalTo("token"));
    }
}