package com.exasol;

public interface ReleaseMaker {
    public void validate(ReleasePlatform platform, String projectName);

    public void release(ReleasePlatform platform, String projectName);
}