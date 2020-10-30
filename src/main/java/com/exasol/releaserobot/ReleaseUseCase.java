package com.exasol.releaserobot;

import java.util.List;

import com.exasol.releaserobot.report.Report;

public interface ReleaseUseCase {
	
	List<Report> release(final UserInput userInput);

}
