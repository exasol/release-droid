package com.exasol.releasedroid.output.guide;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.exasol.releasedroid.usecases.repository.RepositoryGate;

class ChangesFileParserTest {

    static final String SAMPLE = lines( //
            "# Header", //
            "", //
            "Code name: bla bla", //
            "", //
            "##Summary", //
            "", //
            "Initial sentence", //
            "* first list item with `code`", //
            "* second item", //
            "", //
            "Paragraph after the list.", //
            "", //
            "Another paragraph.");
    private static final String SUFFIX = lines( //
            "", //
            "# Another header", //
            "", //
            "bla bla", //
            "");
    private static final String EXPECTED = lines( //
            "Initial sentence", //
            "<ul>", //
            "<li>first list item with <code>code</code></li>", //
            "<li>second item</li>", //
            "</ul>", //
            "Paragraph after the list.", //
            "Another paragraph.");

    static String lines(final String... lines) {
        return Stream.of(lines).collect(Collectors.joining("\n"));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void test(final boolean withSuffix) throws Exception {
        final RepositoryGate gate = mock(RepositoryGate.class);
        when(gate.getSingleFileContentAsString(any())).thenReturn(withSuffix ? SAMPLE + SUFFIX : SAMPLE);
        final ChangesFileParser testee = new ChangesFileParser(gate, "1.2.3");
        assertThat(testee.getSummary(), equalTo(EXPECTED));
    }

}
