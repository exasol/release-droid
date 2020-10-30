package com.exasol.releaserobot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.exasol.releaserobot.report.ReleaseReport;
import com.exasol.releaserobot.report.Report;
import com.exasol.releaserobot.report.ValidationReport;

public class ReleaseInteractor implements ReleaseUseCase{
	private static final Logger LOGGER = Logger.getLogger(ReleaseInteractor.class.getName());
	private ValidateUseCase validateUseCase;
	private Set<Platform> platforms;
	
	public ReleaseInteractor(ValidateUseCase validateUseCase, Set<Platform> platforms) {
		super();
		this.validateUseCase = validateUseCase;
		this.platforms = platforms;
	}

	@Override
	public List<Report> release(UserInput userInput) {
		LOGGER.info(() -> "Release started.");
		List<Report> reports = new ArrayList<>();
		
		ValidationReport validationReport = this.validateUseCase.validate(userInput);
		reports.add(validationReport);
		
		if(!validationReport.hasFailures()) {
			ReleaseReport releaseReport = this.makeRelease(userInput);
			logResults(Goal.RELEASE, releaseReport);
			reports.add(releaseReport);
		}
		return reports;
	}

	private ReleaseReport makeRelease(final UserInput userInput) {
		ReleaseReport releaseReport = new ReleaseReport();
		
		for (Platform platform : this.platforms) {
			try {
				platform.release(userInput);
				releaseReport.addSuccessfulRelease(platform.getPlatformName());
			} catch (final Exception runtimeException) {
				releaseReport.addFailedRelease(platform.getPlatformName(),
						ExceptionUtils.getStackTrace(runtimeException));
				break;
			}
		}
		return releaseReport;
	}
	
	// [impl->dsn~rr-creates-validation-report~1]
	// [impl->dsn~rr-creates-release-report~1]
	private void logResults(final Goal goal, final Report report) {
		if (report.hasFailures()) {
			LOGGER.severe(() -> "'" + goal + "' request failed: " + report.getFailuresReport());
		} else {
			LOGGER.info(report.getShortDescription());
		}
	}

}
