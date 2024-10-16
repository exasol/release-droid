package com.exasol.releasedroid.adapter.github;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.*;
import org.kohsuke.github.GHWorkflowRun.Conclusion;

import com.google.common.collect.Streams;

class LatestSuccessfulRunTest {
    @Test
    void testSuccessfulRunFound() {
        final GHWorkflow workflow = mockWorkflow( //
                mockRun("failed", Conclusion.FAILURE), //
                mockRun("cancelled", Conclusion.CANCELLED), //
                mockRun("success", Conclusion.SUCCESS));
        final GitHubAPIAdapter testee = new GitHubAPIAdapter(mock(GitHubConnector.class));
        final GHWorkflowRun actual = testee.latestSuccessfulRun(workflow);
        assertThat(actual.getName(), equalTo("success"));
    }

    @Test
    void testNoSuccessfulRun() {
        final GHWorkflow workflow = mockWorkflow( //
                mockRun("failed", Conclusion.FAILURE), //
                mockRun("cancelled", Conclusion.CANCELLED));
        final GitHubAPIAdapter testee = new GitHubAPIAdapter(mock(GitHubConnector.class));
        final GHWorkflowRun actual = testee.latestSuccessfulRun(workflow);
        assertNull(actual);
    }

    @SuppressWarnings("unchecked")
    private GHWorkflow mockWorkflow(final GHWorkflowRun first, final GHWorkflowRun... runs) {
        final PagedIterator<GHWorkflowRun> iterator = mock(PagedIterator.class);
        mockIterator(iterator, first, runs);

        final PagedIterable<GHWorkflowRun> iterable = mock(PagedIterable.class);
        when(iterable.iterator()).thenReturn(iterator);

        final GHWorkflow workflow = mock(GHWorkflow.class);
        when(workflow.listRuns()).thenReturn(iterable);
        return workflow;
    }

    private GHWorkflowRun mockRun(final String name, final Conclusion conclusion) {
        final GHWorkflowRun run = mock(GHWorkflowRun.class);
        when(run.getConclusion()).thenReturn(conclusion);
        when(run.getName()).thenReturn(name);
        return run;
    }

    @SuppressWarnings("varargs")
    @SafeVarargs
    private <T> void mockIterator(final PagedIterator<T> iterator, final T first, final T... items) {
        final Boolean[] hasNext = Streams.concat( //
                Arrays.stream(items).map(x -> true), //
                Stream.of(false)) //
                .toArray(Boolean[]::new);
        when(iterator.hasNext()).thenReturn(true, hasNext);
        when(iterator.next()).thenReturn(first, items);
    }
}
