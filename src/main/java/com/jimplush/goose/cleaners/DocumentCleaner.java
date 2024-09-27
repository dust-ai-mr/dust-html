package com.jimplush.goose.cleaners;

import org.jsoup.nodes.Document;

/**
 * User: Jim Plush
 * Date: 12/18/10
 */
public interface DocumentCleaner {

  public Document clean(Document doc);

}
