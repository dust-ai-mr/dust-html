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
import com.mentalresonance.dust.core.msgs.ReturnableMsg;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;


/**
 * Abstract document. Provides everything except content.
 */
@Getter
@Setter
public abstract class DocumentMsg extends ReturnableMsg {
    /**
     * Convenient id
     */
    String uuid = UUID.randomUUID().toString();

    /**
     * Optional title
     */
    String title;

    /**
     * Optional author
     */
    String author;

    /**
     * Time this object was created
     */
    Long createdTs;

    /**
     * Time content was created
     */
    Long contentTs;

    /**
     * Reference to source of doc. Often a url
     */
    String source;

    /**
     * Tag along object
     */
    Serializable tag;

    /**
     * Optional type info
     */
    String type;

    /**
     * Constructor
     * @param sender return address
     */
    public DocumentMsg(ActorRef sender) {
        super(sender);
    }

    /**
     * Content
     * @return the content of the document
     */
    public abstract String getContent();
}
