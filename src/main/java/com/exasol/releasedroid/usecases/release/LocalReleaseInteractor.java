package com.exasol.releasedroid.usecases.release;

import java.util.List;

import com.exasol.errorreporting.ExaError;
import com.exasol.releasedroid.usecases.UserInput;
import com.exasol.releasedroid.usecases.report.Report;

/**
 * This class prevents a release on local repository. Currently we only release on the GitHub.
 */
public class LocalReleaseInteractor implements ReleaseUseCase {
    @Override
    public List<Report> release(final UserInput userInput) {
        throw new UnsupportedOperationException(
                ExaError.messageBuilder("E-RR-5").message("Local release in not supported.")
                        .mitigation("Please remove 'local' argument to release").toString());
    }
}