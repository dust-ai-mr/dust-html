package com.jimplush.goose.texthelpers;
/**
 * Created by IntelliJ IDEA.
 * User: robbie
 * Date: 5/13/11
 * Time: 12:11 AM
 */

public class string {
  
  private string(){}

  public static final String empty = "";
  public static final String[] emptyArray = new String[] {empty};

  public static boolean isNullOrEmpty(String input) {
    if (input == null) return true;
    if (input.length() == 0) return true;
    return false;
  }

  public static StringSplitter SPACE_SPLITTER = new StringSplitter(" ");
}


