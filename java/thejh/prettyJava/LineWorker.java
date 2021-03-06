package thejh.prettyJava                                                                                       ;
                                                                                                               
import java.util.*                                                                                             ;
                                                                                                               
class LineWorker                                                                                               {
  private String line                                                                                          ;
  private Scope scope                                                                                          ;
  private SourceClass srcClass                                                                                 ;
  private ArrayList<Token>tokens                                                                               ;
                                                                                                               
  public LineWorker(String line,Scope scope,SourceClass srcClass)                                              {
    this.line=line                                                                                             ;
    this.scope=scope                                                                                           ;
    this.srcClass=srcClass                                                                                     ;}
                                                                                                               
  public void assignmentToDeclaration()                                                                        {
    String type=null                                                                                           ;
    if(tokens.size()<2)                                                                                        {
      return                                                                                                   ;}
    if(!tokens.get(0).isVariably()||!tokens.get(1).toString().equals("="))                                     {
      return                                                                                                   ;}
    String name=tokens.get(0).toString()                                                                       ;
    if(null!=scope.getType(name))                                                                              {
      return                                                                                                   ;}
    do                                                                                                         {
      if(tokens.size()==3)                                                                                     {
        Token value=tokens.get(2)                                                                              ;
        if(value.isString())                                                                                   {
          type="String"                                                                                        ;
          break                                                                                                ;}
        else if(value.isVariably())                                                                            {
          type=scope.getType(value.toString())                                                                 ;
          break                                                                                                ;}}}
    while(false)                                                                                               ;
    if(type!=null)                                                                                             {
      tokens.add(0,new WordToken(type))                                                                        ;
      scope.setType(name,type)                                                                                 ;}
    else                                                                                                       {
      throw new RuntimeException("can't guess the type of '"+name+"' (and you didn't specify one)")            ;}}
                                                                                                               
  public void implicitCallBrackets()                                                                           {
    while(true)                                                                                                {
      Token lastToken=null                                                                                     ;
      Token twoAgo=null                                                                                        ;
      boolean completedOne=false                                                                               ;
      for(ListIterator<Token>iter=tokens.listIterator();iter.hasNext();)                                       {
        Token t=iter.next()                                                                                    ;
        if(lastToken!=null)                                                                                    {
          boolean tIsBlockerKeyword=t.isKeyword()&&!t.toString().equals("new")&&!t.toString().equals("this")   ;
          boolean lastIsClassy=lastToken.isClassy()||lastToken.isSimpleType()                                  ;
          boolean lastTokenIsNewClass=twoAgo!=null&&lastIsClassy&&"new".equals(twoAgo.toString())              ;
          boolean lastTokenBlocks=!lastToken.isString()&&!lastToken.isVariably()&&!lastTokenIsNewClass         ;
          if(!lastTokenBlocks&&!t.isSymbol()&&!tIsBlockerKeyword)                                              {
            iter.previous()                                                                                    ;
            iter.add(new SymbolToken('('))                                                                     ;
            int bracketLevel=0                                                                                 ;
            while(iter.hasNext())                                                                              {
              Token t2=iter.next()                                                                             ;
              if(t2.isSymbol()&&t2.toString().equals("("))                                                     {
                bracketLevel++                                                                                 ;}
              else if(t2.isSymbol()&&t2.toString().equals(")"))                                                {
                bracketLevel--                                                                                 ;
                if(bracketLevel<0)                                                                             {
                  iter.previous()                                                                              ;
                  iter.add(new SymbolToken(')'))                                                               ;
                  completedOne=true                                                                            ;
                  break                                                                                        ;}}}
            if(!completedOne)                                                                                  {
              tokens.add(new SymbolToken(')'))                                                                 ;
              completedOne=true                                                                                ;}
            break                                                                                              ;}}
        twoAgo=lastToken                                                                                       ;
        lastToken=t                                                                                            ;}
      if(!completedOne)                                                                                        {
        break                                                                                                  ;}}}
                                                                                                               
  public void recognizeFloatingPointNumbers()                                                                  {
    Token twoAgo=null                                                                                          ;
    Token oneAgo=null                                                                                          ;
    for(ListIterator<Token>iter=tokens.listIterator();iter.hasNext();)                                         {
      Token t=iter.next()                                                                                      ;
      if(twoAgo!=null)                                                                                         {
        boolean lastWasDot=oneAgo.isSymbol()&&oneAgo.toString().equals(".")                                    ;
        boolean twoNumbers=t.isNumber()&&twoAgo.isNumber()                                                     ;
        if(lastWasDot&&twoNumbers)                                                                             {
          iter.previous()                                                                                      ;
          iter.remove()                                                                                        ;
          iter.previous()                                                                                      ;
          iter.remove()                                                                                        ;
          NumberToken numtok=(NumberToken)twoAgo                                                               ;
          numtok.value=((NumberToken)twoAgo).value+"."+numtok.value                                            ;
          twoAgo=null                                                                                          ;
          oneAgo=null                                                                                          ;
          continue                                                                                             ;}}
      twoAgo=oneAgo                                                                                            ;
      oneAgo=t                                                                                                 ;}}
                                                                                                               
  public void grabVartype()                                                                                    {
    int offset=0                                                                                               ;
    while(offset<tokens.size()&&tokens.get(offset).isModifier())                                               {
      offset++                                                                                                 ;}
    if(tokens.size()<offset+2)                                                                                 {
      return                                                                                                   ;}
    int varOffset=offset                                                                                       ;
    for(ListIterator<Token>iter=tokens.listIterator(offset);iter.hasNext();)                                   {
      Token typeToken=iter.next()                                                                              ;
      if(typeToken.isVariably())                                                                               {
        break                                                                                                  ;}
      boolean classy=typeToken.isClassy()||typeToken.isSimpleType()                                            ;
      boolean symbol=typeToken.isSymbol()                                                                      ;
      if(!classy&&!symbol)                                                                                     {
        return                                                                                                 ;}
      if(symbol)                                                                                               {
        boolean foundValidSymbol=false                                                                         ;
        String[]validSymbols="< > [ ] ,".split(" ")                                                            ;
        for(String validSymbol:validSymbols)                                                                   {
          if(validSymbol.equals(typeToken.toString()))                                                         {
            foundValidSymbol=true                                                                              ;}}
        if(!foundValidSymbol)                                                                                  {
          return                                                                                               ;}}
      varOffset++                                                                                              ;}
                                                                                                               
    if(offset==varOffset)                                                                                      {
      return                                                                                                   ;}
                                                                                                               
    if(varOffset==tokens.size())                                                                               {
      return                                                                                                   ;}
    List<Token>varTypeTokens=tokens.subList(offset,varOffset)                                                  ;
    String varType=distokenize(varTypeTokens)                                                                  ;
    Token varToken=tokens.get(varOffset)                                                                       ;
    if(!varToken.isVariably())                                                                                 {
      return                                                                                                   ;}
    scope.setType(varToken.toString(),varType)                                                                 ;}
                                                                                                               
  public String doIt()                                                                                         {
    LineTokenizer tokenizer=new LineTokenizer(line)                                                            ;
    tokens=tokenizer.doIt()                                                                                    ;
    recognizeFloatingPointNumbers()                                                                            ;
    implicitCallBrackets()                                                                                     ;
    assignmentToDeclaration()                                                                                  ;
    grabVartype()                                                                                              ;
    return distokenize(tokens)                                                                                 ;}
                                                                                                               
  public String distokenize(List<Token>tokens)                                                                 {
    String distokenized=""                                                                                     ;
    Token lastToken=null                                                                                       ;
    for(ListIterator<Token>iter=tokens.listIterator();iter.hasNext();)                                         {
      Token t=iter.next()                                                                                      ;
      if(lastToken!=null&&lastToken.needsSymbolOrSpace()&&!t.isSymbol())                                       {
        distokenized+=" "                                                                                      ;}
      distokenized+=t.toString()                                                                               ;
      lastToken=t                                                                                              ;}
    return distokenized                                                                                        ;}}
