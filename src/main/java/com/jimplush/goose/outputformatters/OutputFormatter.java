package com.jimplush.goose.outputformatters;

import org.jsoup.nodes.Element;

/**
 * User: jim plush
 * Date: 12/19/10
 */
public interface OutputFormatter {

  public Element getFormattedElement(Element topNode);


  public String getFormattedText();

  public String getFormattedText(Element topNode);

}
