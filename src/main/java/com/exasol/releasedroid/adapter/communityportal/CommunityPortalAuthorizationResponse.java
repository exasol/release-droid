package com.exasol.releasedroid.adapter.communityportal;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
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
        private final XPath xPath = XPathFactory.newInstance().newXPath();

        /**
         * Parse response body.
         *
         * @param responseBody response body
         * @return instance of {@link CommunityPortalAuthorizationResponse}
         */
        public CommunityPortalAuthorizationResponse parseResponseBody(final String responseBody) {
            final var document = loadXMLFromString(responseBody);
            final String status = getByPath(document, "response/@status");
            if (status.equals("success")) {
                return createResponseWithToken(document);
            } else {
                return createResponseWithError(document);
            }
        }

        private String getByPath(final Document document, final String path) {
            try {
                return this.xPath.compile(path).evaluate(document);
            } catch (final XPathExpressionException exception) {
                throw new IllegalStateException(exception);
            }
        }

        private CommunityPortalAuthorizationResponse createResponseWithToken(final Document document) {
            final String token = getByPath(document, "response/value");
            return new CommunityPortalAuthorizationResponse(true, token, null);
        }

        private CommunityPortalAuthorizationResponse createResponseWithError(final Document document) {
            final String errorMessage = getByPath(document, "response/error/message").strip();
            return new CommunityPortalAuthorizationResponse(false, null, errorMessage);
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