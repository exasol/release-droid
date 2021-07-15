package com.exasol.releasedroid.adapter.repository;

import static com.exasol.releasedroid.adapter.repository.ScalaRepositoryValidator.BUILD_SBT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.exasol.releasedroid.usecases.report.Report;

@ExtendWith(MockitoExtension.class)
class ScalaRepositoryValidatorTest {
    @Mock
    private ScalaRepository scalaRepository;
    private ScalaRepositoryValidator validator;

    @BeforeEach
    void BeforeEach() {
        this.validator = new ScalaRepositoryValidator(this.scalaRepository);
    }

    @Test
    void validateContainsReproduciblePlugin() {
        when(this.scalaRepository.getSingleFileContentAsString(BUILD_SBT))
                .thenReturn(".enablePlugins(ReproducibleBuildsPlugin, GitVersioning)");
        final Report validate = this.validator.validate();
        assertFalse(validate.hasFailures());
    }

    @Test
    void validateDoesNotContainReproduciblePlugin() {
        when(this.scalaRepository.getSingleFileContentAsString(BUILD_SBT)).thenReturn("No plugin here");
        final Report validate = this.validator.validate();
        assertTrue(validate.hasFailures());
    }
}