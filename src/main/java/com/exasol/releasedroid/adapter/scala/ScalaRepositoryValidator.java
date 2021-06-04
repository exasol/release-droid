package com.exasol.releasedroid.adapter.scala;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationReport;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

public class ScalaRepositoryValidator implements RepositoryValidator {
    protected static final String BUILD_SBT = "build.sbt";
    private final ScalaRepository repository;

    public ScalaRepositoryValidator(final ScalaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Report validate() {
        final String buildSbt = this.repository.getSingleFileContentAsString(BUILD_SBT);
        final var report = ValidationReport.create();
        if (buildSbt.contains("ReproducibleBuildsPlugin")) {
            report.addSuccessfulResult("'sbt-reproducible-builds' plugin is included.");
        } else {
            report.addFailedResult(ExaError.messageBuilder("E-RD-REP-18")
                    .message("Cannot find required plugin: `sbt-reproducible-builds`.")
                    .mitigation("Please, check user guide and add this plugin to the build.").toString());
        }
        return report;
    }
}