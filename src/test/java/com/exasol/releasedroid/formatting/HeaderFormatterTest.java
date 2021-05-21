package com.exasol.releasedroid.formatting;

import static com.exasol.releasedroid.usecases.ReleaseDroidConstants.LINE_SEPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.exasol.releasedroid.usecases.request.Goal;
import com.exasol.releasedroid.usecases.request.PlatformName;
import com.exasol.releasedroid.usecases.response.ReleaseDroidResponse;

class HeaderFormatterTest {
    @Test
    void testFormatHeader() {
        final ReleaseDroidResponse response = ReleaseDroidResponse.builder() //
                .fullRepositoryName("me/my-repository") //
                .goal(Goal.VALIDATE) //
                .platformNames(List.of(PlatformName.GITHUB, PlatformName.MAVEN)) //
                .branch("my_branch") //
                .localPath(".") //
                .build();
        final HeaderFormatter headerFormatter = new HeaderFormatter();
        final String header = headerFormatter.formatHeader(response);
        assertThat(header, containsString(LINE_SEPARATOR //
                + "Goal: VALIDATE" + LINE_SEPARATOR //
                + "Repository: me/my-repository" + LINE_SEPARATOR //
                + "Platforms: GITHUB, MAVEN" + LINE_SEPARATOR //
                + "Git branch: my_branch" + LINE_SEPARATOR //
                + "Local path: ." + LINE_SEPARATOR));
    }
}