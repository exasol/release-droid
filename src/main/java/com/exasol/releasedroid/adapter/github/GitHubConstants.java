package com.exasol.releasedroid.adapter.github;

/**
 * Contains GitHub-related constants.
 */
public class GitHubConstants {
    public static final String GITHUB_RELEASE_WORKFLOW = "release_droid_upload_github_release_assets.yml";
    private static final String WORKFLOW_DIRECTORY = ".github/workflows/";
    public static final String GITHUB_RELEASE_WORKFLOW_PATH = WORKFLOW_DIRECTORY + GITHUB_RELEASE_WORKFLOW;
    public static final String PREPARE_ORIGINAL_CHECKSUM_WORKFLOW = "release_droid_prepare_original_checksum.yml";
    public static final String PRINT_QUICK_CHECKSUM_WORKFLOW = "release_droid_print_quick_checksum.yml";
    public static final String PREPARE_ORIGINAL_CHECKSUM_WORKFLOW_PATH = WORKFLOW_DIRECTORY
            + PREPARE_ORIGINAL_CHECKSUM_WORKFLOW;
    public static final String PRINT_QUICK_CHECKSUM_WORKFLOW_PATH = WORKFLOW_DIRECTORY + PRINT_QUICK_CHECKSUM_WORKFLOW;

    private GitHubConstants() {
    }
}