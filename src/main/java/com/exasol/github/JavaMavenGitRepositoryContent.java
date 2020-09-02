package com.exasol.github;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.*;

import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GHRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * This class represents a Maven-based Java project's content.
 */
public class JavaMavenGitRepositoryContent extends AbstractGitHubGitRepositoryContent {
    private String version = null;

    /**
     * Create a new instance of {@link GitHubGitRepository}.
     *
     * @param repository an instance of {@link GHRepository}
     * @param branch name of a branch to get content from
     */
    protected JavaMavenGitRepositoryContent(final GHRepository repository, final String branch) {
        super(repository, branch);
    }

    @Override
    public synchronized String getVersion() {
        if (this.version == null) {
            this.version = getVersionFromPomFile();
        }
        return this.version;
    }

    private String getVersionFromPomFile() {
        final String pom = getSingleFileContentAsString("pom.xml");
        final InputStream inputStream = IOUtils.toInputStream(pom);
        return parsePom(inputStream);
    }

    private String parsePom(final InputStream inputStream) {
        try {
            final DocumentBuilder documentBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
            final Document parsedPom = documentBuilder.parse(inputStream);
            final Element rootElement = parsedPom.getDocumentElement();
            return rootElement.getElementsByTagName("version").item(0).getTextContent().strip();
        } catch (final ParserConfigurationException | SAXException | IOException exception) {
            throw new GitHubException("Cannot find a project version in pom.xml file. "
                    + "Please, check that the pom.xml file contains <version></version> tag and the tag is not empty.",
                    exception);
        }
    }
}