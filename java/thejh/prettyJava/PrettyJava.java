package thejh.prettyJava                                                       ;

import java.io.*                                                               ;

public class PrettyJava                                                        {
  public static int getIndentation(String line)                                {
    for (int i=0; i<line.length(); i++)                                        {
      char c = line.charAt(i)                                                  ;
      if (c != ' ')                                                            {
        return i                                                               ;}}
    return -1                                                                  ;}
  
  public static String addPadding(String line, int targetLength)               {
    while (line.length() < targetLength)                                       {
      line += " "                                                              ;}
    return line                                                                ;}
  
  public static String readEntireFile(String name) throws Exception            {
    String content = ""                                                        ;
    File file = new File(name)                                                 ;
    FileInputStream rawin = new FileInputStream(file)                          ;
    BufferedReader in = new BufferedReader(new InputStreamReader(rawin))       ;
    String line = null                                                         ;
    while ((line = in.readLine()) != null)                                     {
      if (!content.equals(""))                                                 {
        content += "\n"                                                        ;}
      content += line                                                          ;}
    return content                                                             ;}
  
  public static String compile(String code)                                    {
    code += "\n"                                                               ;
    String[] lines = code.split("\\n")                                         ;
    int[] indentations = new int[lines.length]                                 ;
    int maxLineLength = 0                                                      ;
    for (int i=0; i<lines.length; i++)                                         {
      String line = lines[i]                                                   ;
      if (line.length() > maxLineLength)                                       {
        maxLineLength = line.length()                                          ;}
      int indentation = getIndentation(line)                                   ;
      if (indentation == -1)                                                   {
        //IMPORTANT: no blank first line possible!
        indentations[i] = indentations[i-1]                                    ;}
      else                                                                     {
        indentations[i] = indentation                                          ;}}
    //we have to close all brackets at the end of the file
    indentations[lines.length-1] = 0                                           ;
    String[] uglySymbols = new String[lines.length]                            ;
    for (int i=0; i<lines.length-1; i++)                                       {
      uglySymbols[i] = ""                                                      ;
      int indentDiff = indentations[i+1] - indentations[i]                     ;
      if (indentDiff == 2)                                                     {
        uglySymbols[i] += "{"                                                  ;}
      else if (indentDiff < 0)                                                 {
        if ((-indentDiff)%2 != 0)                                              {
          throw new RuntimeException("indentation must be a multiple of 2!")   ;}
        for (int n=0; n<-indentDiff; n+=2)                                     {
          uglySymbols[i] += "}"                                                ;}}}
    int paddingTarget = maxLineLength+5                                        ;
    for (int i=0; i<lines.length-1; i++)                                       {
      lines[i] = addPadding(lines[i], paddingTarget) + uglySymbols[i]          ;}
    String output = "";
    for (int i=0; i<lines.length-1; i++)                                       {
      if (i > 0)                                                               {
        output += "\n"                                                         ;}
      output += lines[i]                                                       ;}
    return output                                                              ;}
  
  public static void main(String[] args) throws Exception                      {
    String code = readEntireFile(args[0])                                      ;
    String compiled = compile(code)                                            ;
    System.out.println(compiled)                                               ;}}
