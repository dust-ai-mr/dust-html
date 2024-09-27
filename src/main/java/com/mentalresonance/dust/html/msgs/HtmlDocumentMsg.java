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

package com.mentalresonance.dust.html.msgs;

import com.mentalresonance.dust.core.actors.ActorRef;
import com.mentalresonance.dust.html.services.HtmlService;
import lombok.Getter;
import lombok.Setter;

/**
 * Message for passing HTML based documents around. Provides some convenient methods on the content.
 */
@Getter
public class HtmlDocumentMsg extends DocumentMsg {

    /**
     * The content
     */
    @Setter
    String html;

    /**
     * Constructor
     * @param sender return address
     */
    public HtmlDocumentMsg(ActorRef sender) {
        super(sender);
    }

    /**
     * Raw html of document
     * @return content of document
     */
    @Override
    public String getContent() {
        return html;
    }

    /**
     * Set document content
     * @param content content
     * @return content
     */
    public String setContent(String content) {
        html = content;
        return html;
    }

    /**
     * Text of the 'core' of the document - attempts to remove clutter. Returns text divided into paragraphs
     * via '\n'. For example, if this were a web page then the goal is things like advertisements and other clutter
     * would be removed.
     * @return decluttered raw text
     */
    public String extractContent() {
        return HtmlService.extractContent(html);
    }

    /**
     * Just the raw text of the document - white space normalized
     * @return raw text of document with one-space whitespace
     */
    public String getText() { return HtmlService.text(html); }

    /**
     * Just the raw text of the document - white space not normalized
     * @return raw text of document. White space as is.
     */
    public String getWholeText() { return HtmlService.wholeText(html); }

    /**
     * Pretty print info of document and first part of content
     */
    @Override
    public String toString() {
        String stype = null != type ? type : "html";
        return "HTMLDoc: %s [%s] - %s".formatted(
                source, stype,
                null != title ? title : (html.length() > 64 ? html.substring(0, 64) : html)
        );
    }
}
