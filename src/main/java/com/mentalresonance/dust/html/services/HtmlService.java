/*
 * Copyright 2024 Alan Littleford
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mentalresonance.dust.html.services;

import com.jimplush.goose.ContentExtractor;
import com.jimplush.goose.cleaners.DefaultDocumentCleaner;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Convenient services for processing HtmlDocuments
 */
@Slf4j
public class HtmlService {

    /**
     * Constructor
     */
    public HtmlService() {}
    /**
     * Make document from raw html for page found at baseUri (or "")
     *
     * @param html html content
     * @param baseUri base uri to resolve relative urls against
     * @return Document
     */
    public static Document makeDoc(String html, String baseUri) {
        return Jsoup.parseBodyFragment(html, baseUri);
    }

    /**
     * Clean up html optionally keeping links
     *
     * @param html dirty html
     * @param keepLInks if true keep a and href elements
     * @return cleaned html
     */
    public static Document clean(String html, Boolean keepLInks) {
        return clean(Jsoup.parseBodyFragment(html, ""), keepLInks);
    }

    /**
     * Clean up html optionally keeping links
     * @param dirty DocumentMsg
     * @param keepLinks - if true keep a and hrefs
     * @return cleaned DocumentMsg
     */

    public static Document clean(Document dirty, Boolean keepLinks) {
        Document clean;

        Safelist wl = new Safelist()
            .addTags(
                    "b", "blockquote", "br", "caption", "cite", "code", "col",
                    "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6",
                    "i", "p", "pre", "q", "small", "strike", "strong",
                    "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "a")

            .addAttributes("blockquote", "cite")
            .addAttributes("col", "span", "width")
            .addAttributes("colgroup", "span", "width")
            .addAttributes("q", "cite")
            .addAttributes("table", "summary", "width")
            .addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width")
            .addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width")
            .addProtocols("blockquote", "cite", "http", "https")
            .addProtocols("cite", "cite", "http", "https")
            .addProtocols("img", "src", "http", "https")
            .addProtocols("q", "cite", "http", "https");

        if (keepLinks)
            wl = wl.addAttributes( "a", "href"); // Keep links for later extraction

        /**
         * First completely remove elements we don't want to even consider.
         */
        for(Element element: dirty.body().getAllElements())
        {
            if (
                element.classNames().contains("screen-reader-text") ||
                Arrays.asList("figure", "script", "style", "picture", "source").contains(element.tagName().toLowerCase())
            ){
                element.remove();
            }
        }

        // Now remove the tags leaving, we hope, useful text
        clean = new Cleaner(wl).clean(dirty);

        for(Element element: clean.body().getAllElements()) {
            if (!element.hasText() && element.isBlock() ||
                    ((element.text().length() < 30) && element.isBlock())
            ) {
                element.remove();
            }
        }
        return clean;
    }

    /**
     * Pre clean - for document extraction
     * @param doc the document
     * @return cleaned document
     */
    public static Document preClean(Document doc) {
        return new DefaultDocumentCleaner().clean(doc);
    }

    /**
     * Get all links in a document - do not convert to absolute (Legacy)
     * @param doc document
     * @param baseUri of this document
     * @param onSite if true only those links pointing back to this domain
     * @return list of [url, link text]
     * @throws MalformedURLException if bad url
     * @throws URISyntaxException if bad syntax
     */
    public static List<List<String>> links(Document doc, String baseUri, boolean onSite) throws MalformedURLException, URISyntaxException {
        return links(doc, baseUri, onSite, false);
    }

    /**
     * Get all links in a document
     * @param doc the document
     * @param baseUri of this document
     * @param onSite  if true only those links pointing back to this domain
     * @param absolute if true all links are made absolute
     * @return list of [url, link text]
     * @throws MalformedURLException if bad url
     * @throws URISyntaxException if bad syntax
     */
    public static List<List<String>> links(Document doc, String baseUri, boolean onSite, boolean absolute) throws URISyntaxException, MalformedURLException {
        LinkedList<List<String>> list = new LinkedList<>();
        String baseHost = new URI(baseUri).toURL().getHost();

        for(Element link: doc.select("a[href]")) {
            String href = absolute ? link.attr("abs:href") : link.attr("href");
            if ((! onSite) || onSiteLink(href, baseHost)) {
                list.add(Arrays.asList(href, link.text().trim()));
            }
        }
        return list;
    }

    /**
     * Extract (cleaned up) text of 'main content' of input html
     * @param html raw html
     * @return cleaned main content
     */
    public static String extractContent(String html) {
        return new ContentExtractor().extractContent(html);
    }
    /**
     * Extract (cleaned up) text of 'main content' of document
     * @param doc document to process
     * @return cleaned main content
     */
    public static String extractContent(Document doc) {
        return new ContentExtractor().extractContent(doc);
    }

    /**
     * Returns text with whitespace normalized
     * @param html raw html
     * @return  text with whitespace normalized
     */
    public static String text(String html) { return Jsoup.parse(html).text(); }

    /**
     * Returns text with white space not normalized
     * @param html raw html
     * @return text with white space not normalized
     */
    public static String wholeText(String html) { return Jsoup.parse(html).wholeText(); }

    /**
     * Return list of urls in the html
     * @param html raw html
     * @return list of urls
     */
    public static List<String> urls(String html) {
        List<String> urls = new LinkedList<>();
        Elements links = Jsoup.parse(html).select("a[href]");

        for (Element link : links) {
            urls.add(link.attr("abs:href"));
        }
        return urls;
    }

    /**
     * Does link refer to same host as baseHost. We include subdomains as meaning same host (as well as no host).
     * (i.e. is link to same or related site, or not).
     * @param link to investigate
     * @param baseHost relative to this
     * @return true if same host, false if not
     * @throws MalformedURLException if bad url
     * @throws URISyntaxException if bad syntax
     */
    public static boolean onSiteLink(String link, String baseHost) throws URISyntaxException, MalformedURLException {
        try {
            if (link.startsWith("/") || link.startsWith("#"))
                return true;
            else {
                String host = new URI(link).toURL().getHost();
                return host.endsWith(baseHost);
            }
        } catch (Exception e) {
            return false;
        }
    }
}
