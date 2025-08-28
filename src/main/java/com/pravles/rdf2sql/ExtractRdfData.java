/*
 * Copyright 2025 Pravles Redneckoff
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.pravles.rdf2sql;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static javax.xml.xpath.XPathConstants.NODESET;

public class ExtractRdfData implements Function<File, DataFromRdf> {
    @Override
    public DataFromRdf apply(final File file) {
        if (file == null) {
            return DataFromRdf.builder()
                    .success(false)
                    .build();
        }

        if (!(file.exists() && file.canRead())) {
            return DataFromRdf.builder()
                    .success(false)
                    .build();
        }

        try (final InputStream is = new FileInputStream(file)) {
            final DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(is);
            final XPathFactory xPathFactory = XPathFactory.newInstance();
            final XPath xPath = xPathFactory.newXPath();
            xPath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    switch (prefix) {
                        case "dcterms": return "http://purl.org/dc/terms/";
                        case "rdf": return "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
                        case "pgterms": return "http://www.gutenberg.org/2009/pgterms/";
                        default: return XMLConstants.NULL_NS_URI;
                    }
                }

                public String getPrefix(String namespaceURI) {
                    return null; // not used
                }

                public Iterator getPrefixes(String namespaceURI) {
                    return null; // not used
                }
            });

            final String languageCode = extactSingleNodeContents(doc, xPath, "//dcterms:language//rdf:value/text()");

            final String title = escapeSpecialCharacters(extactSingleNodeContents(doc, xPath, "//dcterms:title/text()"));

            final String creator = escapeSpecialCharacters(extactSingleNodeContents(doc, xPath, "//dcterms:creator/pgterms:agent/pgterms:name/text()"));

            final String textUrl = extractMultipleNodeUrl(doc, xPath, "//dcterms:hasFormat/pgterms:file/@rdf:about", ".utf-8");

            final String rdfUrl = extractMultipleNodeUrl(doc, xPath, "//dcterms:hasFormat/pgterms:file/@rdf:about", ".rdf");


            final String smallCoverUrl = extractMultipleNodeUrl(doc, xPath, "//dcterms:hasFormat/pgterms:file/@rdf:about", ".cover.small.jpg");

            final String mediumCoverUrl = extractMultipleNodeUrl(doc, xPath, "//dcterms:hasFormat/pgterms:file/@rdf:about", ".cover.medium.jpg");

            final List<String> subjects = extractSubjects(doc, xPath, "//dcterms:subject//rdf:value");

            final boolean fiction = subjects.stream()
                    .anyMatch(subject -> subject.toLowerCase().contains("fiction"));

            final boolean poetry = subjects.stream()
                    .anyMatch(subject ->
                            subject.toLowerCase().contains("poetry"));

            doc.getDocumentElement().normalize();

            return DataFromRdf.builder()
                    .languageCode(languageCode)
                    .title(title)
                    .creator(creator)
                    .textUrl(textUrl)
                    .rdfUrl(rdfUrl)
                    .smallCoverUrl(smallCoverUrl)
                    .mediumCoverUrl(mediumCoverUrl)
                    .subjects(subjects)
                    .fiction(fiction)
                    .poetry(poetry)
                    .success(true)
                    .build();

        } catch (final IOException | SAXException | ParserConfigurationException | XPathExpressionException e) {
            e.printStackTrace();
            return DataFromRdf.builder()
                    .success(false)
                    .build();
        }
    }

    private String escapeSpecialCharacters(final String input) {
        return input.replace("'", "''") ;
    }

    private String extractMultipleNodeUrl(
            final Document doc, final XPath xPath, String expr,
            final String suffix) throws XPathExpressionException {

        final XPathExpression xPathExpression = xPath.compile(expr);
        final NodeList nodeList = (NodeList) xPathExpression.evaluate(doc, NODESET);
        final List<String> texts = new ArrayList<>(nodeList.getLength());
        for (int i=0; i < nodeList.getLength(); i++) {
            texts.add(nodeList.item(i).getTextContent());
        }

        return texts.stream()
                .filter(text -> text.endsWith(suffix))
                .findFirst()
                .orElse("");
    }

    private List<String> extractSubjects(
            final Document doc, final XPath xPath, String expr) throws XPathExpressionException {

        final XPathExpression xPathExpression = xPath.compile(expr);
        final NodeList nodeList = (NodeList) xPathExpression.evaluate(doc, NODESET);
        final List<String> texts = new ArrayList<>(nodeList.getLength());
        for (int i=0; i < nodeList.getLength(); i++) {
            texts.add(nodeList.item(i).getTextContent());
        }

        return texts;
    }




    private String extactSingleNodeContents(final Document doc, final XPath xPath, String expr) throws XPathExpressionException {
        final XPathExpression xPathExpression = xPath.compile(expr);
        final NodeList nodeList = (NodeList) xPathExpression.evaluate(doc,
                NODESET);

        if (nodeList.getLength() != 1) {
            return "";
        }
        return nodeList.item(0).getTextContent();
    }
}
