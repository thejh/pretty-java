package thejh.prettyJava                                                                                                        ;
                                                                                                                                
import java.io.*                                                                                                                ;
import java.util.regex.*                                                                                                        ;
import java.util.*                                                                                                              ;
                                                                                                                                
class Scope                                                                                                                     {
  public Scope superscope                                                                                                       ;
  private HashMap<String,String>vartypes=new HashMap<String,String>()                                                           ;
  private int depth=0                                                                                                           ;
  private HashMap<String,Integer>uniqueIDs=new HashMap<String,Integer>()                                                        ;
                                                                                                                                
  public String getType(String var)                                                                                             {
    Scope s=this                                                                                                                ;
    while(s!=null)                                                                                                              {
      String value=s.vartypes.get(var)                                                                                          ;
      if(value!=null)                                                                                                           {
        return value                                                                                                            ;}
      s=s.superscope                                                                                                            ;}
    return null                                                                                                                 ;}
                                                                                                                                
  public void setType(String var,String type)                                                                                   {
    vartypes.put(var,type)                                                                                                      ;}
                                                                                                                                
  public Scope createSubscope()                                                                                                 {
    Scope sub=new Scope()                                                                                                       ;
    sub.superscope=this                                                                                                         ;
    sub.depth=depth+1                                                                                                           ;
    return sub                                                                                                                  ;}
                                                                                                                                
  public String makeNewName(String type)                                                                                        {
    int newID=1                                                                                                                 ;
    if(uniqueIDs.containsKey(type))                                                                                             {
      newID=1+uniqueIDs.get(type)                                                                                               ;}
    else                                                                                                                        {
      Scope s=this                                                                                                              ;
      while(s!=null&&(s=s.superscope)!=null)                                                                                    {
        if(s.uniqueIDs.containsKey(type))                                                                                       {
          newID=1+s.uniqueIDs.get(type)                                                                                         ;
          s=null                                                                                                                ;}}}
    uniqueIDs.put(type,newID)                                                                                                   ;
    return type+newID                                                                                                           ;}}
                                                                                                                                
class SourceClass                                                                                                               {
  public ArrayList<String>packageImports=new ArrayList<String>()                                                                ;
  public ArrayList<String>classImports=new ArrayList<String>()                                                                  ;
  public final String packagesRootFolder="/usr/share/javadoc/java-1.6.0-openjdk/api/"                                           ;
                                                                                                                                
  public SourceClass()                                                                                                          {
    packageImports.add("java.lang")                                                                                             ;}
                                                                                                                                
  public String[]classesInPackage(String pkg)                                                                                   {
    String folder=packagesRootFolder+pkg.replace('.','/')                                                                       ;
    String[]files=new File(folder).list()                                                                                       ;
    ArrayList<String>htmlFiles=new ArrayList<String>()                                                                          ;
    for(String file:files)                                                                                                      {
      if(file.endsWith(".html"))                                                                                                {
        htmlFiles.add(file.substring(0,file.length()-5))                                                                        ;}}
    return htmlFiles.toArray(new String[]{})                                                                                    ;}
                                                                                                                                
  public String lookupFQNsReturnType(String fqn,String method)                                                                  {
    String filename=packagesRootFolder+fqn.replace('.','/')+".html"                                                             ;
    String htmlData=PrettyJava.readEntireFile(filename)                                                                         ;
    htmlData=htmlData.substring(htmlData.indexOf("<!-- ========== METHOD SUMMARY =========== -->"))                             ;
    htmlData=htmlData.substring(0,htmlData.indexOf("<!-- ============ FIELD DETAIL =========== -->"))                           ;
    int methodIndex=htmlData.indexOf(">"+method+"</A></B>")                                                                     ;
    if(methodIndex==-1)                                                                                                         {
      return null                                                                                                               ;}
    int rettypeEndIndex=htmlData.lastIndexOf("</CODE></FONT></TD>",methodIndex)                                                 ;
    int rettypeBeginIndex=12+htmlData.lastIndexOf("<CODE>&nbsp;",rettypeEndIndex)                                               ;
    return htmlData.substring(rettypeBeginIndex,rettypeEndIndex)                                                                ;}
                                                                                                                                
  public String lookupReturnType(String type,String method)                                                                     {
    for(String classImport:classImports)                                                                                        {
      if(classImport.endsWith("."+type))                                                                                        {
        return lookupFQNsReturnType(classImport,method)                                                                         ;}}
    for(String pkg:packageImports)                                                                                              {
      for(String cls:classesInPackage(pkg))                                                                                     {
        if(cls.equals(type))                                                                                                    {
          return lookupFQNsReturnType(pkg+"."+cls,method)                                                                       ;}}}
    return null                                                                                                                 ;}}
                                                                                                                                
public class PrettyJava                                                                                                         {
  public static final String[]completeLineEating=new String[]{"if","for","else if","while"}                                     ;
                                                                                                                                
                                                                                                                                
                                                                                                                                
  public static final String ppVar="(?:[a-z][a-zA-Z$_0-9]*)"                                                                    ;
  public static final String ppPrimitive="(?:byte|short|int|long|float|double|boolean|char|void)"                               ;
  public static final String ppType="(?:(?:[A-Z][a-zA-Z$_0-9]*|"+ppPrimitive+")(?:\\[\\])*)"                                    ;
  public static final String ppDeclareModifier="(?:public|private|protected|static|final)"                                      ;
  public static final String ppDeclareModifiers="(?:(?:"+ppDeclareModifier+" )*"+ppDeclareModifier+" +)?"                       ;
  public static final String ppMethodParams="(?:\\(((?:"+ppType+" +"+ppVar+" *, *)*"+ppType+" +"+ppVar+")?\\))"                 ;
                                                                                                                                
  public static final Pattern pDeclareMethod=Pattern.compile(ppDeclareModifiers+"("+ppType+") +("+ppVar+") *"+ppMethodParams)   ;
  public static final Pattern pForInVar=Pattern.compile("((?:"+ppType+" )?)("+ppVar+") +in +("+ppVar+")")                       ;
  public static final Pattern pForFromTo=Pattern.compile("(?:("+ppVar+") in )?\\[(.*)(\\.{2,3})(.*)\\]")                        ;
  public static final Pattern pImport=Pattern.compile("import ([*a-zA-Z0-9_$.]+)")                                              ;
                                                                                                                                
  public static int getIndentation(String line)                                                                                 {
    for(int i=0;i<line.length();i++)                                                                                            {
      char c=line.charAt(i)                                                                                                     ;
      if(c!=' ')                                                                                                                {
        return i                                                                                                                ;}}
    return -1                                                                                                                   ;}
                                                                                                                                
  public static String compileFor(String line,Scope s)                                                                          {
                                                                                                                                
    Matcher mFromTo=pForFromTo.matcher(line)                                                                                    ;
    if(mFromTo.matches())                                                                                                       {
      String var=mFromTo.group(1)                                                                                               ;
      String start=mFromTo.group(2)                                                                                             ;
      String rangeDots=mFromTo.group(3)                                                                                         ;
      String end=mFromTo.group(4)                                                                                               ;
      if(var==null)                                                                                                             {
        var=s.makeNewName("$number")                                                                                            ;}
      return "int "+var+" = "+start+"; "+var+" < "+end+"; "+var+"++"                                                            ;}
                                                                                                                                
                                                                                                                                
    Matcher mInVar=pForInVar.matcher(line)                                                                                      ;
    if(mInVar.matches())                                                                                                        {
      String type=mInVar.group(1)                                                                                               ;
      String var=mInVar.group(2)                                                                                                ;
      String array=mInVar.group(3)                                                                                              ;
      if(type.equals(""))                                                                                                       {
        String arrayType=s.getType(array)                                                                                       ;
        if(arrayType==null)                                                                                                     {
          throw new RuntimeException("Can't find the type of '"+array+"'")                                                      ;}
        if(arrayType.endsWith("[]"))                                                                                            {
          type=arrayType.substring(0,arrayType.length()-2)                                                                      ;}
        else                                                                                                                    {
          throw new RuntimeException("Can't loop through a '"+arrayType+"'")                                                    ;}}
      return type+" "+var+": "+array                                                                                            ;}
    return line                                                                                                                 ;}
                                                                                                                                
  public static String handleLine(String line,Scope s,SourceClass srcClass)                                                     {
    Matcher mDeclareMethod=pDeclareMethod.matcher(line)                                                                         ;
    if(mDeclareMethod.matches())                                                                                                {
      String returnType=mDeclareMethod.group(1)                                                                                 ;
      String methodName=mDeclareMethod.group(2)                                                                                 ;
      String paramsStr=mDeclareMethod.group(3)                                                                                  ;
      if(paramsStr!=null)                                                                                                       {
        String[]paramStrs=paramsStr.split(" *, *")                                                                              ;
        for(String paramStr:paramStrs)                                                                                          {
          String[]paramStrParts=paramStr.trim().split(" +")                                                                     ;
          String type=paramStrParts[0]                                                                                          ;
          String var=paramStrParts[1]                                                                                           ;
          s.setType(var,type)                                                                                                   ;}}}
                                                                                                                                
    Matcher mImport=pImport.matcher(line)                                                                                       ;
    if(mImport.matches())                                                                                                       {
      String imported=mImport.group(1)                                                                                          ;
      if(imported.endsWith(".*"))                                                                                               {
        srcClass.packageImports.add(imported.substring(0,imported.length()-2))                                                  ;}
      else                                                                                                                      {
        srcClass.classImports.add(imported)                                                                                     ;}}
                                                                                                                                
    line=new LineWorker(line,s,srcClass).doIt()                                                                                 ;
                                                                                                                                
    return line                                                                                                                 ;}
                                                                                                                                
  public static String stripIndentation(String line)                                                                            {
    int indent=getIndentation(line)                                                                                             ;
    if(indent==-1)                                                                                                              {
      return ""                                                                                                                 ;}
    return line.substring(indent)                                                                                               ;}
                                                                                                                                
  public static boolean isWhitespaceOnly(String line)                                                                           {
    for(int i=0;i<line.length();i++)                                                                                            {
      char c=line.charAt(i)                                                                                                     ;
      if(c!=' ')                                                                                                                {
        return false                                                                                                            ;}}
    return true                                                                                                                 ;}
                                                                                                                                
  public static boolean couldNeedSemicolon(String line)                                                                         {
    for(int i=0;i<line.length();i++)                                                                                            {
      char c=line.charAt(i)                                                                                                     ;
      if(c!=' ')                                                                                                                {
        if(i==line.indexOf("//"))                                                                                               {
          return false                                                                                                          ;}
        return true                                                                                                             ;}}
    return false                                                                                                                ;}
                                                                                                                                
  public static String addPadding(String line,int targetLength)                                                                 {
    while(line.length()<targetLength)                                                                                           {
      line+=" "                                                                                                                 ;}
    return line                                                                                                                 ;}
                                                                                                                                
  public static String nTimes(String str,int times)                                                                             {
    String result=""                                                                                                            ;
    for(int i=0;i<times;i++)                                                                                                    {
      result+=str                                                                                                               ;}
    return result                                                                                                               ;}
                                                                                                                                
  public static String readEntireFile(String name)                                                                              {
    try                                                                                                                         {
      String content=""                                                                                                         ;
      File file=new File(name)                                                                                                  ;
      FileInputStream rawin=new FileInputStream(file)                                                                           ;
      BufferedReader in=new BufferedReader(new InputStreamReader(rawin))                                                        ;
      String line=null                                                                                                          ;
      while((line=in.readLine())!=null)                                                                                         {
        if(!content.equals(""))                                                                                                 {
          content+="\n"                                                                                                         ;}
        content+=line                                                                                                           ;}
      return content                                                                                                            ;}
    catch(Exception e)                                                                                                          {
      throw new RuntimeException(e)                                                                                             ;}}
                                                                                                                                
  public static String compile(String code)                                                                                     {
    Scope s=new Scope()                                                                                                         ;
    SourceClass srcClass=new SourceClass()                                                                                      ;
    String[]lines=code.split("\\n")                                                                                             ;
    int[]indentations=new int[lines.length]                                                                                     ;
    boolean[]linesWithContent=new boolean[lines.length]                                                                         ;
    Scope[]lineScopes=new Scope[lines.length]                                                                                   ;
    int maxLineLength=0                                                                                                         ;
    int lastIndent=0                                                                                                            ;
    for(int i=0;i<lines.length;i++)                                                                                             {
      linesWithContent[i]=!isWhitespaceOnly(lines[i])                                                                           ;
      String lineWithoutIndent=stripIndentation(lines[i])                                                                       ;
      int indentation=getIndentation(lines[i])                                                                                  ;
      indentations[i]=indentation                                                                                               ;
      if(linesWithContent[i])                                                                                                   {
        int indentDiff=indentation-lastIndent                                                                                   ;
        if(indentDiff==2)                                                                                                       {
          s=s.createSubscope()                                                                                                  ;}
        else if(indentDiff<0)                                                                                                   {
          for(int n=indentDiff/2;n<0;n++)                                                                                       {
            s=s.superscope                                                                                                      ;}}
        lineScopes[i]=s                                                                                                         ;
        for(String completeLineEater:completeLineEating)                                                                        {
          if(lineWithoutIndent.startsWith(completeLineEater+" "))                                                               {
            String withoutEater=lineWithoutIndent.substring(completeLineEater.length()+1)                                       ;
            if(completeLineEater.equals("for"))                                                                                 {
              withoutEater=compileFor(withoutEater,s)                                                                           ;}
            lines[i]=nTimes(" ",getIndentation(lines[i]))+completeLineEater+" ("+withoutEater+")"                               ;}}
        lineWithoutIndent=handleLine(stripIndentation(lines[i]),s,srcClass)                                                     ;
        lines[i]=nTimes(" ",getIndentation(lines[i]))+lineWithoutIndent                                                         ;
        lastIndent=indentation                                                                                                  ;}
      if(lines[i].length()>maxLineLength)                                                                                       {
        maxLineLength=lines[i].length()                                                                                         ;}}
    String[]uglySymbols=new String[lines.length]                                                                                ;
                                                                                                                                
    for(int i=0;i<lines.length;i++)                                                                                             {
      uglySymbols[i]=""                                                                                                         ;
      if(!linesWithContent[i])                                                                                                  {
        continue                                                                                                                ;}
      int nexti=i                                                                                                               ;
      boolean hasNextLine=true                                                                                                  ;
      do                                                                                                                        {
        nexti++                                                                                                                 ;
        if(nexti==lines.length)                                                                                                 {
          hasNextLine=false                                                                                                     ;
          break                                                                                                                 ;}}
      while(!linesWithContent[nexti])                                                                                           ;
      int indentDiff                                                                                                            ;
      if(hasNextLine)                                                                                                           {
        indentDiff=indentations[nexti]-indentations[i]                                                                          ;}
      else                                                                                                                      {
        indentDiff=-indentations[i]                                                                                             ;}
      if(indentDiff<=0&&couldNeedSemicolon(lines[i]))                                                                           {
        uglySymbols[i]+=";"                                                                                                     ;}
      if(indentDiff==2)                                                                                                         {
        uglySymbols[i]+="{"                                                                                                     ;}
      else if(indentDiff<0)                                                                                                     {
        if((-indentDiff)%2!=0)                                                                                                  {
          throw new RuntimeException("indentation must be a multiple of 2!")                                                    ;}
        for(int n=0;n<-indentDiff;n+=2)                                                                                         {
          uglySymbols[i]+="}"                                                                                                   ;}}}
                                                                                                                                
    int paddingTarget=maxLineLength+3                                                                                           ;
    for(int i=0;i<lines.length;i++)                                                                                             {
      lines[i]=addPadding(lines[i],paddingTarget)+uglySymbols[i]                                                                ;}
    String output=""                                                                                                            ;
    for(int i=0;i<lines.length;i++)                                                                                             {
      if(i>0)                                                                                                                   {
        output+="\n"                                                                                                            ;}
      output+=lines[i]                                                                                                          ;}
    return output                                                                                                               ;}
                                                                                                                                
  public static void main(String[]args)throws Exception                                                                         {
    String code=readEntireFile(args[0])                                                                                         ;
    String compiled=compile(code)                                                                                               ;
    System.out.println(compiled)                                                                                                ;}}
