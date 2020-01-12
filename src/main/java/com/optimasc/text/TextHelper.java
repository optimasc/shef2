package com.optimasc.text;

public class TextHelper
{
  public static String splitLine(String s, int maxLength)
  {
    StringBuilder sb = new StringBuilder(s);

    int i = 0;
    while ((i = sb.indexOf(" ", i + maxLength)) != -1) {
        sb.replace(i, i + 1, "\n");
    }

    return sb.toString();
  }
  
}
