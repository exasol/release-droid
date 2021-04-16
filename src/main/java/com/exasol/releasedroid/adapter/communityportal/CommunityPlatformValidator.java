package com.exasol.releasedroid.adapter.communityportal;

import com.exasol.releasedroid.usecases.report.Report;
import com.exasol.releasedroid.usecases.repository.Repository;
import com.exasol.releasedroid.usecases.validate.RepositoryValidator;

public class CommunityPlatformValidator implements RepositoryValidator {
    public CommunityPlatformValidator(final Repository javaRepository) {
    }

    @Override
    public Report validate() {
        // TODO add validation
        return Report.validationReport();
    }
}