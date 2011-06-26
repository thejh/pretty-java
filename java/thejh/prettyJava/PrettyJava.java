package thejh.prettyJava                                                                                                            ;
                                                                                                                                    
import java.io.*                                                                                                                    ;
import java.util.regex.*                                                                                                            ;
import java.util.*                                                                                                                  ;
                                                                                                                                    
class Scope                                                                                                                         {
  public Scope superscope                                                                                                           ;
  private HashMap<String, String> vartypes = new HashMap<String, String>()                                                          ;
  private int depth = 0                                                                                                             ;
                                                                                                                                    
  public String getType(String var)                                                                                                 {
    Scope s = this                                                                                                                  ;
    while (s != null)                                                                                                               {
      String value = s.vartypes.get(var)                                                                                            ;
      if (value != null)                                                                                                            {
        return value                                                                                                                ;}
      s = s.superscope                                                                                                              ;}
    return null                                                                                                                     ;}
                                                                                                                                    
  public void setType(String var, String type)                                                                                      {
    System.err.println(depth+": '"+var+"' has type '"+type+"'")                                                                     ;
    vartypes.put(var, type)                                                                                                         ;}
                                                                                                                                    
  public Scope createSubscope()                                                                                                     {
    Scope sub = new Scope()                                                                                                         ;
    sub.superscope = this                                                                                                           ;
    sub.depth = depth+1                                                                                                             ;
    return sub                                                                                                                      ;}}
                                                                                                                                    
public class PrettyJava                                                                                                             {
  public static final String[] completeLineEating = new String[]{"if", "for", "else if", "while"}                                   ;
                                                                                                                                    
  // A java variable can also start with other stuff, but it's aganist                                                              
  // convention, so I don't care.                                                                                                   
  public static final String ppVar = "(?:[a-z][a-zA-Z$_0-9]*)"                                                                      ;
  public static final String ppPrimitive = "(?:byte|short|int|long|float|double|boolean|char|void)"                                 ;
  public static final String ppType = "(?:(?:[A-Z][a-zA-Z$_0-9]*|"+ppPrimitive+")(?:\\[\\])*)"                                      ;
  public static final String ppDeclareModifier = "(?:public|private|protected|static|final)"                                        ;
  public static final String ppDeclareModifiers = "(?:(?:"+ppDeclareModifier+" )*"+ppDeclareModifier+")?"                           ;
  public static final String ppMethodParams = "(?:\\(((?:"+ppType+" +"+ppVar+" *, *)*"+ppType+" +"+ppVar+")?\\))"                   ;
                                                                                                                                    
  public static final Pattern pDeclare = Pattern.compile("("+ppType+") +("+ppVar+") +(;|=.*)")                                      ;
  public static final Pattern pDeclareMethod = Pattern.compile(ppDeclareModifiers+" +("+ppType+") +("+ppVar+") *"+ppMethodParams)   ;
  public static final Pattern pForInVar = Pattern.compile("((?:"+ppType+" )?)("+ppVar+") +in +("+ppVar+")")                         ;
  public static final Pattern pForFromTo = Pattern.compile("("+ppVar+") in \\[(.*)(\\.{2,3})(.*)\\]")                               ;
                                                                                                                                    
  public static int getIndentation(String line)                                                                                     {
    for (int i = 0; i < line.length(); i++)                                                                                         {
      char c = line.charAt(i)                                                                                                       ;
      if (c != ' ')                                                                                                                 {
        return i                                                                                                                    ;}}
    return -1                                                                                                                       ;}
                                                                                                                                    
  public static String compileFor(String line, Scope s)                                                                             {
    // "i in [0..9]"                                                                                                                
    Matcher mFromTo = pForFromTo.matcher(line)                                                                                      ;
    if (mFromTo.matches())                                                                                                          {
      String var = mFromTo.group(1)                                                                                                 ;
      String start = mFromTo.group(2)                                                                                               ;
      String rangeDots = mFromTo.group(3)                                                                                           ;
      String end = mFromTo.group(4)                                                                                                 ;
      return "int "+var+" = "+start+"; "+var+" < "+end+"; "+var+"++"                                                                ;}
                                                                                                                                    
    // "[String ]line in lines"                                                                                                     
    Matcher mInVar = pForInVar.matcher(line)                                                                                        ;
    if (mInVar.matches())                                                                                                           {
      String type = mInVar.group(1)                                                                                                 ;
      String var = mInVar.group(2)                                                                                                  ;
      String array = mInVar.group(3)                                                                                                ;
      if (type.equals(""))                                                                                                          {
        String arrayType = s.getType(array)                                                                                         ;
        if (arrayType == null)                                                                                                      {
          throw new RuntimeException("Can't find the type of '"+array+"'")                                                          ;}
        if (arrayType.endsWith("[]"))                                                                                               {
          type = arrayType.substring(0, arrayType.length()-2)                                                                       ;}
        else                                                                                                                        {
          throw new RuntimeException("Can't loop through a '"+arrayType+"'")                                                        ;}}
      return type+" "+var+": "+array                                                                                                ;}
    return line                                                                                                                     ;}
                                                                                                                                    
  public static void grabFromLine(String line, Scope s)                                                                             {
    Matcher mDeclare = pDeclare.matcher(line)                                                                                       ;
    if (mDeclare.matches())                                                                                                         {
      String type = mDeclare.group(1)                                                                                               ;
      String var = mDeclare.group(2)                                                                                                ;
      s.setType(var, type)                                                                                                          ;}
                                                                                                                                    
    Matcher mDeclareMethod = pDeclareMethod.matcher(line)                                                                           ;
    if (mDeclareMethod.matches())                                                                                                   {
      String returnType = mDeclareMethod.group(1)                                                                                   ;
      String methodName = mDeclareMethod.group(2)                                                                                   ;
      String paramsStr = mDeclareMethod.group(3)                                                                                    ;
      if (paramsStr != null)                                                                                                        {
        String[] paramStrs = paramsStr.split(" *, *")                                                                               ;
        for (String paramStr: paramStrs)                                                                                            {
          String[] paramStrParts = paramStr.trim().split(" +")                                                                      ;
          String type = paramStrParts[0]                                                                                            ;
          String var = paramStrParts[1]                                                                                             ;
          s.setType(var, type)                                                                                                      ;}}}}
                                                                                                                                    
  public static String stripIndentation(String line)                                                                                {
    int indent = getIndentation(line)                                                                                               ;
    if (indent == -1)                                                                                                               {
      return ""                                                                                                                     ;}
    return line.substring(indent)                                                                                                   ;}
                                                                                                                                    
  public static boolean isWhitespaceOnly(String line)                                                                               {
    for (int i = 0; i < line.length(); i++)                                                                                         {
      char c = line.charAt(i)                                                                                                       ;
      if (c != ' ')                                                                                                                 {
        return false                                                                                                                ;}}
    return true                                                                                                                     ;}
                                                                                                                                    
  public static boolean couldNeedSemicolon(String line)                                                                             {
    for (int i = 0; i < line.length(); i++)                                                                                         {
      char c = line.charAt(i)                                                                                                       ;
      if (c != ' ')                                                                                                                 {
        if (line.indexOf("//") == i)                                                                                                {
          return false                                                                                                              ;}
        return true                                                                                                                 ;}}
    return false                                                                                                                    ;}
                                                                                                                                    
  public static String addPadding(String line, int targetLength)                                                                    {
    while (line.length() < targetLength)                                                                                            {
      line += " "                                                                                                                   ;}
    return line                                                                                                                     ;}
                                                                                                                                    
  public static String nTimes(String str, int times)                                                                                {
    String result = ""                                                                                                              ;
    for (int i = 0; i < times; i++)                                                                                                 {
      result += str                                                                                                                 ;}
    return result                                                                                                                   ;}
                                                                                                                                    
  public static String readEntireFile(String name) throws Exception                                                                 {
    String content = ""                                                                                                             ;
    File file = new File(name)                                                                                                      ;
    FileInputStream rawin = new FileInputStream(file)                                                                               ;
    BufferedReader in = new BufferedReader(new InputStreamReader(rawin))                                                            ;
    String line = null                                                                                                              ;
    while ( (line = in.readLine()) != null)                                                                                         {
      if (!content.equals(""))                                                                                                      {
        content += "\n"                                                                                                             ;}
      content += line                                                                                                               ;}
    return content                                                                                                                  ;}
                                                                                                                                    
  public static String compile(String code)                                                                                         {
    Scope s = new Scope()                                                                                                           ;
    String[] lines = code.split("\\n")                                                                                              ;
    int[] indentations = new int[lines.length]                                                                                      ;
    boolean[] linesWithContent = new boolean[lines.length]                                                                          ;
    Scope[] lineScopes = new Scope[lines.length]                                                                                    ;
    int maxLineLength = 0                                                                                                           ;
    int lastIndent = 0                                                                                                              ;
    for (int i = 0; i < lines.length; i++)                                                                                          {
      linesWithContent[i] = !isWhitespaceOnly(lines[i])                                                                             ;
      String lineWithoutIndent = stripIndentation(lines[i])                                                                         ;
      int indentation = getIndentation(lines[i])                                                                                    ;
      indentations[i] = indentation                                                                                                 ;
      if (linesWithContent[i])                                                                                                      {
        int indentDiff = indentation - lastIndent                                                                                   ;
        if (indentDiff == 2)                                                                                                        {
          s = s.createSubscope()                                                                                                    ;}
        else if (indentDiff < 0)                                                                                                    {
          for (int n = indentDiff/2; n < 0; n++)                                                                                    {
            s = s.superscope                                                                                                        ;}}
        lineScopes[i] = s                                                                                                           ;
        grabFromLine(lineWithoutIndent, s)                                                                                          ;
        for (String completeLineEater: completeLineEating)                                                                          {
          if (lineWithoutIndent.startsWith(completeLineEater+" "))                                                                  {
            String withoutEater = lineWithoutIndent.substring(completeLineEater.length()+1)                                         ;
            if (completeLineEater.equals("for"))                                                                                    {
              withoutEater = compileFor(withoutEater, s)                                                                            ;}
            lines[i] = nTimes(" ", getIndentation(lines[i]))+completeLineEater+" ("+withoutEater+")"                                ;}}
        lastIndent = indentation                                                                                                    ;}
      if (lines[i].length() > maxLineLength)                                                                                        {
        maxLineLength = lines[i].length()                                                                                           ;}}
    String[] uglySymbols = new String[lines.length]                                                                                 ;
                                                                                                                                    
    for (int i = 0; i < lines.length; i++)                                                                                          {
      uglySymbols[i] = ""                                                                                                           ;
      if (!linesWithContent[i])                                                                                                     {
        continue                                                                                                                    ;}
      int nexti = i                                                                                                                 ;
      boolean hasNextLine = true                                                                                                    ;
      do                                                                                                                            {
        nexti++                                                                                                                     ;
        if (nexti == lines.length)                                                                                                  {
          hasNextLine = false                                                                                                       ;
          break                                                                                                                     ;}}
      while (!linesWithContent[nexti])                                                                                              ;
      int indentDiff;                                                                                                               ;
      if (hasNextLine)                                                                                                              {
        indentDiff = indentations[nexti] - indentations[i]                                                                          ;}
      else                                                                                                                          {
        indentDiff = -indentations[i]                                                                                               ;}
      if (indentDiff <= 0 && couldNeedSemicolon(lines[i]))                                                                          {
        uglySymbols[i] += ";"                                                                                                       ;}
      if (indentDiff == 2)                                                                                                          {
        uglySymbols[i] += "{"                                                                                                       ;}
      else if (indentDiff < 0)                                                                                                      {
        if ( (-indentDiff)%2 != 0)                                                                                                  {
          throw new RuntimeException("indentation must be a multiple of 2!")                                                        ;}
        for (int n=0; n<-indentDiff; n+=2)                                                                                          {
          uglySymbols[i] += "}"                                                                                                     ;}}}
                                                                                                                                    
    int paddingTarget = maxLineLength+3                                                                                             ;
    for (int i = 0; i < lines.length; i++)                                                                                          {
      lines[i] = addPadding(lines[i], paddingTarget) + uglySymbols[i]                                                               ;}
    String output = ""                                                                                                              ;
    for (int i = 0; i < lines.length; i++)                                                                                          {
      if (i > 0)                                                                                                                    {
        output += "\n"                                                                                                              ;}
      output += lines[i]                                                                                                            ;}
    return output                                                                                                                   ;}
                                                                                                                                    
  public static void main(String[] args) throws Exception                                                                           {
    String code = readEntireFile(args[0])                                                                                           ;
    String compiled = compile(code)                                                                                                 ;
    System.out.println(compiled)                                                                                                    ;}}
