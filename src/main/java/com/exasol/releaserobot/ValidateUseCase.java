package com.exasol.releaserobot;

import com.exasol.releaserobot.report.ValidationReport;

public interface ValidateUseCase {

	ValidationReport validate(UserInput userInput);

}
