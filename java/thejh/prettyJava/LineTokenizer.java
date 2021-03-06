package thejh.prettyJava                                                                                                        ;
                                                                                                                                
import java.io.*                                                                                                                ;
import java.util.*                                                                                                              ;
                                                                                                                                
abstract class Token                                                                                                            {
  public boolean isKeyword()                                                                                                    {
    return false                                                                                                                ;}
  public boolean isModifier()                                                                                                   {
    return false                                                                                                                ;}
  public boolean isVariably()                                                                                                   {
    return false                                                                                                                ;}
  public boolean isClassy()                                                                                                     {
    return false                                                                                                                ;}
  public boolean isSimpleType()                                                                                                 {
    return false                                                                                                                ;}
  public boolean isWord()                                                                                                       {
    return false                                                                                                                ;}
  public boolean needsSymbolOrSpace()                                                                                           {
    return false                                                                                                                ;}
  public boolean isSymbol()                                                                                                     {
    return false                                                                                                                ;}
  public boolean isNumber()                                                                                                     {
    return false                                                                                                                ;}
  public boolean isString()                                                                                                     {
    return false                                                                                                                ;}}
                                                                                                                                
class NumberToken extends Token                                                                                                 {
  public String value                                                                                                           ;
                                                                                                                                
  public NumberToken(double value)                                                                                              {
    String[]valueParts=(value+"").split("\\.")                                                                                  ;
    this.value=valueParts[0]                                                                                                    ;}
                                                                                                                                
  public String toString()                                                                                                      {
    return value                                                                                                                ;}
                                                                                                                                
  public boolean needsSymbolOrSpace()                                                                                           {
    return true                                                                                                                 ;}
  public boolean isNumber()                                                                                                     {
    return true                                                                                                                 ;}}
                                                                                                                                
class WordToken extends Token                                                                                                   {
  public static String[]keywords                                                                                                ;
  static                                                                                                                        {
    String keywordsStr="float native super while "                                                                              ;
    keywordsStr+="catch extends int short try char final interface static void class finally long strictfp volatile const "     ;
    keywordsStr+="double implements protected throw byte else import public throws case enum instanceof return transient "      ;
    keywordsStr+="abstract continue for new switch assert default goto package synchronized boolean do if private this break"   ;
    keywords=keywordsStr.split(" ")                                                                                             ;}
  public static String[]modifiers                                                                                               ;
  static                                                                                                                        {
    String modifiersStr="native final static volatile protected public transient abstract synchronized private"                 ;
    modifiers=modifiersStr.split(" ")                                                                                           ;}
  public static String[]simpleTypes                                                                                             ;
  static                                                                                                                        {
    String simpleTypesStr="float int short char void long double byte boolean"                                                  ;
    simpleTypes=simpleTypesStr.split(" ")                                                                                       ;}
                                                                                                                                
  public String value                                                                                                           ;
                                                                                                                                
  public WordToken(String value)                                                                                                {
    this.value=value                                                                                                            ;}
                                                                                                                                
  public boolean isWord()                                                                                                       {
    return true                                                                                                                 ;}
                                                                                                                                
  public boolean isKeyword()                                                                                                    {
    for(String keyword:keywords)                                                                                                {
      if(keyword.equals(value))                                                                                                 {
        return true                                                                                                             ;}}
    return false                                                                                                                ;}
                                                                                                                                
  public boolean isModifier()                                                                                                   {
    for(String keyword:modifiers)                                                                                               {
      if(keyword.equals(value))                                                                                                 {
        return true                                                                                                             ;}}
    return false                                                                                                                ;}
                                                                                                                                
  public boolean isSimpleType()                                                                                                 {
    for(String keyword:simpleTypes)                                                                                             {
      if(keyword.equals(value))                                                                                                 {
        return true                                                                                                             ;}}
    return false                                                                                                                ;}
                                                                                                                                
  public boolean isVariably()                                                                                                   {
    char firstChar=value.charAt(0)                                                                                              ;
    return!isKeyword()&&firstChar>='a'&&firstChar<='z'                                                                          ;}
                                                                                                                                
  public boolean isClassy()                                                                                                     {
    char firstChar=value.charAt(0)                                                                                              ;
    return firstChar>='A'&&firstChar<='Z'                                                                                       ;}
                                                                                                                                
  public String toString()                                                                                                      {
    return value                                                                                                                ;}
                                                                                                                                
  public boolean needsSymbolOrSpace()                                                                                           {
    return true                                                                                                                 ;}}
                                                                                                                                
class SymbolToken extends Token                                                                                                 {
  public char value                                                                                                             ;
                                                                                                                                
  public SymbolToken(char value)                                                                                                {
    this.value=value                                                                                                            ;}
                                                                                                                                
  public String toString()                                                                                                      {
    return new String(new char[]{value})                                                                                        ;}
                                                                                                                                
  public boolean isSymbol()                                                                                                     {
    return true                                                                                                                 ;}}
                                                                                                                                
class StringToken extends Token                                                                                                 {
  public String value                                                                                                           ;
                                                                                                                                
  public StringToken(String value)                                                                                              {
    this.value=value                                                                                                            ;}
                                                                                                                                
  public String toString()                                                                                                      {
    String value=this.value                                                                                                     ;
    value=value.replaceAll("\\\\","\\\\\\\\")                                                                                   ;
    value=value.replaceAll("\\\"","\\\\\"")                                                                                     ;
    value=value.replaceAll("\\f","\\\\f")                                                                                       ;
    value=value.replaceAll("\\r","\\\\r")                                                                                       ;
    value=value.replaceAll("\\n","\\\\n")                                                                                       ;
    value=value.replaceAll("\\t","\\\\t")                                                                                       ;
                                                                                                                                
    value=value.replaceAll("\b","\\\\b")                                                                                        ;
    return "\""+value+"\""                                                                                                      ;}
                                                                                                                                
  public boolean isString()                                                                                                     {
    return true                                                                                                                 ;}}
                                                                                                                                
class CharToken extends Token                                                                                                   {
  public char value                                                                                                             ;
                                                                                                                                
  public CharToken(char value)                                                                                                  {
    this.value=value                                                                                                            ;}
                                                                                                                                
  public String toString()                                                                                                      {
    if(value=='\\')                                                                                                             {
      return "'\\\\'"                                                                                                           ;}
    else if(value=='\'')                                                                                                        {
      return "'\\''"                                                                                                            ;}
    else if(value=='\f')                                                                                                        {
      return "'\\f'"                                                                                                            ;}
    else if(value=='\r')                                                                                                        {
      return "'\\r'"                                                                                                            ;}
    else if(value=='\n')                                                                                                        {
      return "'\\n'"                                                                                                            ;}
    else if(value=='\t')                                                                                                        {
      return "'\\t'"                                                                                                            ;}
    else if(value=='\b')                                                                                                        {
      return "'\\b'"                                                                                                            ;}
    else                                                                                                                        {
      return "'"+value+"'"                                                                                                      ;}}}
                                                                                                                                
class LineTokenizer extends StreamTokenizer                                                                                     {
  public LineTokenizer(String code)                                                                                             {
    super(new StringReader(code))                                                                                               ;
    resetSyntax()                                                                                                               ;
    slashSlashComments(true)                                                                                                    ;
    slashStarComments(true)                                                                                                     ;
    whitespaceChars(' ',' ')                                                                                                    ;
    quoteChar('"')                                                                                                              ;
    quoteChar('\'')                                                                                                             ;
    parseNumbers()                                                                                                              ;
    ordinaryChar('.')                                                                                                           ;
    wordChars('a','z')                                                                                                          ;
    wordChars('A','Z')                                                                                                          ;
    wordChars('_','_')                                                                                                          ;}
                                                                                                                                
  public ArrayList<Token>doIt()                                                                                                 {
    ArrayList<Token>tokens=new ArrayList<Token>();                                                                              ;
    while(true)                                                                                                                 {
      try                                                                                                                       {
        nextToken()                                                                                                             ;}
      catch(Exception e)                                                                                                        {
        throw new RuntimeException(e)                                                                                           ;}
      if(ttype==TT_WORD)                                                                                                        {
        String word=sval                                                                                                        ;
        tokens.add(new WordToken(word))                                                                                         ;}
      else if(ttype==TT_NUMBER)                                                                                                 {
        double num=nval                                                                                                         ;
        tokens.add(new NumberToken(num))                                                                                        ;}
      else if(ttype==TT_EOL)                                                                                                    {
        throw new RuntimeException("don't stuff an eol in me")                                                                  ;}
      else if(ttype==TT_EOF)                                                                                                    {
        return tokens                                                                                                           ;}
      else if(ttype=='"')                                                                                                       {
        String str=sval                                                                                                         ;
        tokens.add(new StringToken(str))                                                                                        ;}
      else if(ttype=='\'')                                                                                                      {
        if(sval.length()!=1)                                                                                                    {
          throw new RuntimeException("a char is one character, not more, not less!")                                            ;}
        char chr=sval.charAt(0)                                                                                                 ;
        tokens.add(new CharToken(chr))                                                                                          ;}
      else                                                                                                                      {
        char symbol=(char)ttype                                                                                                 ;
        tokens.add(new SymbolToken(symbol))                                                                                     ;}}}}
