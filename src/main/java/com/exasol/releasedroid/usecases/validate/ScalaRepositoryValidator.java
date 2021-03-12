package com.exasol.releasedroid.usecases.validate;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.repository.ScalaRepository;
import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.report.ValidationResult;

public class ScalaRepositoryValidator implements RepositoryValidator {
    protected static final String BUILD_SBT = "build.sbt";
    private final ScalaRepository repository;

    public ScalaRepositoryValidator(final ScalaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Report validate() {
        final String buildSbt = this.repository.getSingleFileContentAsString(BUILD_SBT);
        final Report report = Report.validationReport();
        if (buildSbt.contains("ReproducibleBuildsPlugin")) {
            report.addResult(ValidationResult.successfulValidation("'sbt-reproducible-builds' plugin is included."));
        } else {
            report.addResult(ValidationResult.failedValidation(ExaError.messageBuilder("E-RR-VAL-11")
                    .message("Cannot find required plugin: `sbt-reproducible-builds`.")
                    .mitigation("Please, check user guide and add this plugin to the build.").toString()));
        }
        return report;
    }
}