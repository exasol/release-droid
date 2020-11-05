package com.exasol.releaserobot.usecases.validate;

import com.exasol.releaserobot.repository.Branch;
import com.exasol.releaserobot.repository.Repository;
import com.exasol.releaserobot.usecases.UserInput;

public interface RepositoryGateway {

    Repository getRepository(UserInput userInput);

    Branch getDefaultBranch();

}
