package com.jimplush.goose.texthelpers;
/**
 * Created by IntelliJ IDEA.
 * User: robbie
 * Date: 5/13/11
 * Time: 3:53 PM
 */

import java.util.regex.Pattern;

public class StringSplitter {

  private Pattern pattern;

  public StringSplitter(String pattern) {
    this.pattern = Pattern.compile(pattern);
  }

  public String[] split(String input) {
    if (string.isNullOrEmpty(input)) return string.emptyArray;
    return pattern.split(input);
  }
}


