package thejh.prettyJava                                                                               ;
                                                                                                       
import java.io.*                                                                                       ;
                                                                                                       
public class PrettyJava                                                                                {
  public static final String[] completeLineEating = new String[]{"if", "for", "else if", "while"};     ;
                                                                                                       
  public static int getIndentation(String line)                                                        {
    for (int i=0; i<line.length(); i++)                                                                {
      char c = line.charAt(i)                                                                          ;
      if (c != ' ')                                                                                    {
        return i                                                                                       ;}}
    return -1                                                                                          ;}
                                                                                                       
  public static String stripIndentation(String line)                                                   {
    int indent = getIndentation(line)                                                                  ;
    if (indent == -1)                                                                                  {
      return ""                                                                                        ;}
    return line.substring(indent)                                                                      ;}
                                                                                                       
  public static boolean isWhitespaceOnly(String line)                                                  {
    for (int i=0; i<line.length(); i++)                                                                {
      char c = line.charAt(i)                                                                          ;
      if (c != ' ')                                                                                    {
        return false                                                                                   ;}}
    return true                                                                                        ;}
                                                                                                       
  public static boolean couldNeedSemicolon(String line)                                                {
    for (int i=0; i<line.length(); i++)                                                                {
      char c = line.charAt(i)                                                                          ;
      if (c != ' ')                                                                                    {
        if (line.indexOf("//") == i)                                                                   {
          return false                                                                                 ;}
        return true                                                                                    ;}}
    return false                                                                                       ;}
                                                                                                       
  public static String addPadding(String line, int targetLength)                                       {
    while (line.length() < targetLength)                                                               {
      line += " "                                                                                      ;}
    return line                                                                                        ;}
                                                                                                       
  public static String nTimes(String str, int times)                                                   {
    String result = ""                                                                                 ;
    for (int i=0; i<times; i++)                                                                        {
      result += str                                                                                    ;}
    return result                                                                                      ;}
                                                                                                       
  public static String readEntireFile(String name) throws Exception                                    {
    String content = ""                                                                                ;
    File file = new File(name)                                                                         ;
    FileInputStream rawin = new FileInputStream(file)                                                  ;
    BufferedReader in = new BufferedReader(new InputStreamReader(rawin))                               ;
    String line = null                                                                                 ;
    while ( (line = in.readLine()) != null)                                                            {
      if (!content.equals(""))                                                                         {
        content += "\n"                                                                                ;}
      content += line                                                                                  ;}
    return content                                                                                     ;}
                                                                                                       
  public static String compile(String code)                                                            {
    String[] lines = code.split("\\n")                                                                 ;
    int[] indentations = new int[lines.length]                                                         ;
    boolean[] linesWithContent = new boolean[lines.length]                                             ;
    int maxLineLength = 0                                                                              ;
    for (int i=0; i<lines.length; i++)                                                                 {
      linesWithContent[i] = !isWhitespaceOnly(lines[i])                                                ;
      String lineWithoutIndent = stripIndentation(lines[i])                                            ;
      for (String completeLineEater: completeLineEating)                                               {
        if (lineWithoutIndent.startsWith(completeLineEater+" "))                                       {
          String withoutEater = lineWithoutIndent.substring(completeLineEater.length()+1)              ;
          lines[i] = nTimes(" ", getIndentation(lines[i]))+completeLineEater+" ("+withoutEater+")"     ;}}
      if (lines[i].length() > maxLineLength)                                                           {
        maxLineLength = lines[i].length()                                                              ;}
      int indentation = getIndentation(lines[i])                                                       ;
      indentations[i] = indentation                                                                    ;}
    String[] uglySymbols = new String[lines.length]                                                    ;
                                                                                                       
    for (int i=0; i<lines.length; i++)                                                                 {
      uglySymbols[i] = ""                                                                              ;
      if (!linesWithContent[i])                                                                        {
        continue                                                                                       ;}
      int nexti = i                                                                                    ;
      boolean hasNextLine = true                                                                       ;
      do                                                                                               {
        nexti++                                                                                        ;
        if (nexti == lines.length)                                                                     {
          hasNextLine = false                                                                          ;
          break                                                                                        ;}}
      while (!linesWithContent[nexti])                                                                 ;
      int indentDiff;                                                                                  ;
      if (hasNextLine)                                                                                 {
        indentDiff = indentations[nexti] - indentations[i]                                             ;}
      else                                                                                             {
        indentDiff = -indentations[i]                                                                  ;}
      if (indentDiff <= 0 && couldNeedSemicolon(lines[i]))                                             {
        uglySymbols[i] += ";"                                                                          ;}
      if (indentDiff == 2)                                                                             {
        uglySymbols[i] += "{"                                                                          ;}
      else if (indentDiff < 0)                                                                         {
        if ( (-indentDiff)%2 != 0)                                                                     {
          throw new RuntimeException("indentation must be a multiple of 2!")                           ;}
        for (int n=0; n<-indentDiff; n+=2)                                                             {
          uglySymbols[i] += "}"                                                                        ;}}}
                                                                                                       
    int paddingTarget = maxLineLength+3                                                                ;
    for (int i=0; i<lines.length; i++)                                                                 {
      lines[i] = addPadding(lines[i], paddingTarget) + uglySymbols[i]                                  ;}
    String output = ""                                                                                 ;
    for (int i=0; i<lines.length; i++)                                                                 {
      if (i > 0)                                                                                       {
        output += "\n"                                                                                 ;}
      output += lines[i]                                                                               ;}
    return output                                                                                      ;}
                                                                                                       
  public static void main(String[] args) throws Exception                                              {
    String code = readEntireFile(args[0])                                                              ;
    String compiled = compile(code)                                                                    ;
    System.out.println(compiled)                                                                       ;}}
