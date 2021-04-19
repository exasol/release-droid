package com.exasol.releasedroid.adapter.communityportal;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class represents a response from the Exasol Community Portal authorization service.
 */
public class CommunityPortalAuthorizationResponse {
    private final boolean statusOk;
    private final String token;
    private final String errorMessage;

    private CommunityPortalAuthorizationResponse(final boolean statusOk, final String token,
            final String errorMessage) {
        this.statusOk = statusOk;
        this.token = token;
        this.errorMessage = errorMessage;
    }

    /**
     * Create a new instance of {@link CommunityPortalAuthorizationResponse}
     * 
     * @param responseBody body of the authorization HTTP response
     * @return new instance of {@link CommunityPortalAuthorizationResponse}
     */
    public static CommunityPortalAuthorizationResponse createCommunityPortalAuthorizationResponse(
            final String responseBody) {
        return new CommunityPortalAuthorizationResponseParser().parseResponseBody(responseBody);
    }

    /**
     * Get an authentication token if exists.
     * 
     * @return token
     */
    public Optional<String> getToken() {
        return Optional.ofNullable(this.token);
    }

    /**
     * Get error message if exists.
     * 
     * @return error message
     */
    public Optional<String> getErrorMessage() {
        return Optional.ofNullable(this.errorMessage);
    }

    /**
     * CHeck is the response has an OK status.
     * 
     * @return response status
     */
    public boolean isStatusOk() {
        return this.statusOk;
    }

    private static class CommunityPortalAuthorizationResponseParser {
        public CommunityPortalAuthorizationResponse parseResponseBody(final String responseBody) {
            final var document = loadXMLFromString(responseBody);
            final String status = document.getDocumentElement().getAttribute("status");
            if (status.equals("success")) {
                return createResponseWithToken(document);
            } else {
                return createResponseWithError(document);
            }
        }

        private CommunityPortalAuthorizationResponse createResponseWithToken(final Document document) {
            final var token = getStringContent(document.getDocumentElement().getChildNodes(), "value");
            return new CommunityPortalAuthorizationResponse(true, token, null);
        }

        private CommunityPortalAuthorizationResponse createResponseWithError(final Document document) {
            String errorMessage = null;
            final NodeList nodes = document.getDocumentElement().getChildNodes();
            for (var i = 0; i < nodes.getLength(); ++i) {
                if (nodes.item(i).getNodeName().equals("error")) {
                    errorMessage = getStringContent(nodes.item(i).getChildNodes(), "message");
                    break;
                }
            }
            return new CommunityPortalAuthorizationResponse(false, null, errorMessage);
        }

        private String getStringContent(final NodeList nodes, final String nodeName) {
            for (var j = 0; j < nodes.getLength(); ++j) {
                if (nodes.item(j).getNodeName().equals(nodeName)) {
                    return nodes.item(j).getTextContent().strip();
                }
            }
            return null;
        }

        private Document loadXMLFromString(final String xml) {
            final var factory = DocumentBuilderFactory.newInstance();
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            final var inputSource = new InputSource(new StringReader(xml));
            try {
                return factory.newDocumentBuilder().parse(inputSource);
            } catch (final ParserConfigurationException | SAXException | IOException exception) {
                throw new IllegalStateException(exception);
            }
        }
    }
}