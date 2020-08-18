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
 * This class represents a Maven-based Java project.
 */
public class JavaMavenProject extends AbstractGitHubRepository {
    /**
     * Create a new instance of {@link JavaMavenProject}.
     *
     * @param oauthAccessToken GitHub oauth Access Token
     * @param repository an instance of {@link GHRepository}
     */
    public JavaMavenProject(final GHRepository repository, final String oauthAccessToken) {
        super(repository, oauthAccessToken);
    }

    @Override
    public synchronized String getVersion() {
        final String versionKey = "version";
        if (!this.filesCache.containsKey(versionKey)) {
            this.filesCache.put(versionKey, getVersionFromPomFile());
        }
        return this.filesCache.get(versionKey);
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