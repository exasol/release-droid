package com.exasol.releasedroid.usecases.response;

import java.util.List;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.request.Goal;
import com.exasol.releasedroid.usecases.request.PlatformName;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReleaseDroidResponse {
    private final String fullRepositoryName;
    private final Goal goal;
    private final String branch;
    private final String localPath;
    private final List<PlatformName> platformNames;
    private final List<Report> reports;
}