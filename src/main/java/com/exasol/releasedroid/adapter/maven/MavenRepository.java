package com.exasol.releasedroid.adapter.maven;

import com.exasol.releasedroid.usecases.repository.Repository;

public interface MavenRepository extends Repository {

    MavenPom getMavenPom();

}
