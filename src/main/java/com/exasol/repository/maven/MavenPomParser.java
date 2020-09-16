package com.exasol.repository.maven;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.xml.parsers.*;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.exasol.github.GitHubException;

/**
 * Parser for {@link MavenPom}.
 */
public class MavenPomParser {
    private final Element parsedPomRoot;

    /**
     * Create a new instance of {@link MavenPomParser}.
     * 
     * @param pom pom file as a string
     */
    public MavenPomParser(final String pom) {
        this.parsedPomRoot = parsePom(pom);
    }

    private Element parsePom(final String pom) {
        try (final InputStream inputStream = IOUtils.toInputStream(pom)) {
            final DocumentBuilder documentBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
            final Document parsedPom = documentBuilder.parse(inputStream);
            return parsedPom.getDocumentElement();
        } catch (final ParserConfigurationException | SAXException | IOException exception) {
            throw new GitHubException("E-REP-MAV-1: Cannot parse pom.xml file. "
                    + "Please, check that the pom.xml file in a valid format.", exception);
        }
    }

    /**
     * Parse pom file.
     * 
     * @return new instance of {@link MavenPom}
     */
    public MavenPom parse() {
        final String artifactId = parseChildElement("artifactId");
        final String version = parseChildElement("version");
        final String deliverableName = parseDeliverableName(artifactId, version);
        final MavenPom.Builder builder = MavenPom.builder();
        return builder.version(version).artifactId(artifactId).deliverableName(deliverableName).build();
    }

    private String parseDeliverableName(final String artifactId, final String version) {
        final Optional<String> deliverableNameOptional = parseDeliverableName();
        final String deliverableName;
        if (deliverableNameOptional.isEmpty()) {
            deliverableName = artifactId + "-" + version;
        } else {
            deliverableName = deliverableNameOptional.get();
        }
        return deliverableName;
    }

    private String parseChildElement(final String elementName) {
        final Node item = getMandatoryChildNode(elementName);
        final String element = item.getTextContent().strip();
        if ((element != null) && !element.isEmpty()) {
            return element;
        } else {
            throw throwParsingException(elementName);
        }
    }

    private Node getMandatoryChildNode(final String elementName) {
        final Node item = this.parsedPomRoot.getElementsByTagName(elementName).item(0);
        if (item != null) {
            return item;
        } else {
            throw throwParsingException(elementName);
        }
    }

    private IllegalStateException throwParsingException(final String elementName) {
        return new IllegalStateException(
                "E-REP-MAV-2: Unable to parse pom file because of a missing element: " + elementName);
    }

    private Optional<String> parseDeliverableName() {
        final Element build = (Element) this.parsedPomRoot.getElementsByTagName("build").item(0);
        if (build == null) {
            return Optional.empty();
        } else {
            return parsePlugins(build);
        }
    }

    private Optional<String> parsePlugins(final Element build) {
        final NodeList plugins = build.getElementsByTagName("plugins").item(0).getChildNodes();
        for (int i = 0; i < plugins.getLength(); i++) {
            final Node next = plugins.item(i);
            if (next.getNodeType() == Node.ELEMENT_NODE) {
                final Element plugin = (Element) next;
                final Node artifactIdNode = plugin.getElementsByTagName("artifactId").item(0);
                final String artifactId = artifactIdNode.getTextContent().strip();
                if (artifactId.equals("maven-assembly-plugin")) {
                    return parseMavenAssemblyPlugin(plugin);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<String> parseMavenAssemblyPlugin(final Element plugin) {
        final Element configurations = (Element) plugin.getElementsByTagName("configuration").item(0);
        if (configurations == null) {
            return Optional.empty();
        } else {
            return getParseConfigurations(configurations);
        }
    }

    private Optional<String> getParseConfigurations(final Element configurations) {
        final Node finalNameNode = configurations.getElementsByTagName("finalName").item(0);
        if ((finalNameNode == null) || (finalNameNode.getTextContent() == null)
                || (finalNameNode.getTextContent().isEmpty())) {
            return Optional.empty();
        } else {
            return parseFinalName(finalNameNode);
        }
    }

    private Optional<String> parseFinalName(final Node finalNameNode) {
        String finalName = finalNameNode.getTextContent().strip();
        while (finalName.contains("${")) {
            finalName = replaceVariable(finalName);
        }
        return Optional.of(finalName);
    }

    private String replaceVariable(final String finalName) {
        final int startIndex = finalName.indexOf("${") + 2;
        final int endIndex = finalName.indexOf('}');
        final String tag = finalName.substring(startIndex, endIndex);
        final String replacement = findReplacement(tag);
        return finalName.replace("${" + tag + "}", replacement);
    }

    private String findReplacement(final String tag) {
        if (tag.equals("version")) {
            return parseChildElement("version");
        } else {
            final Element properties = (Element) getMandatoryChildNode("properties");
            final Node tagNode = properties.getElementsByTagName(tag).item(0);
            if (tagNode == null) {
                throw throwParsingException(tag);
            } else {
                return tagNode.getTextContent().strip();
            }
        }
    }
}