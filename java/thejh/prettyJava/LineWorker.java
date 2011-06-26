package thejh.prettyJava                                                                                       ;
                                                                                                               
import java.util.*                                                                                             ;
                                                                                                               
class LineWorker                                                                                               {
  private String line                                                                                          ;
  private Scope scope                                                                                          ;
  private SourceClass srcClass                                                                                 ;
                                                                                                               
  public LineWorker(String line,Scope scope,SourceClass srcClass)                                              {
    this.line=line                                                                                             ;
    this.scope=scope                                                                                           ;
    this.srcClass=srcClass                                                                                     ;}
                                                                                                               
  public void implicitCallBrackets(ArrayList<Token>tokens)                                                     {
    while(true)                                                                                                {
      Token lastToken=null                                                                                     ;
      Token twoAgo=null                                                                                        ;
      boolean completedOne=false                                                                               ;
      for(ListIterator<Token>iter=tokens.listIterator();iter.hasNext();)                                       {
        Token t=iter.next()                                                                                    ;
        if(lastToken!=null)                                                                                    {
          boolean tIsBlockerKeyword=t.isKeyword()&&!t.toString().equals("new")&&!t.toString().equals("this")   ;
          boolean lastTokenIsNewClass=twoAgo!=null&&lastToken.isClassy()&&"new".equals(twoAgo.toString())      ;
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
                                                                                                               
  public void recognizeFloatingPointNumbers(ArrayList<Token>tokens)                                            {
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
                                                                                                               
  public String doIt()                                                                                         {
    LineTokenizer tokenizer=new LineTokenizer(line)                                                            ;
    ArrayList<Token>tokens=tokenizer.doIt()                                                                    ;
    recognizeFloatingPointNumbers(tokens)                                                                      ;
    implicitCallBrackets(tokens)                                                                               ;
    return distokenize(tokens)                                                                                 ;}
                                                                                                               
  public String distokenize(ArrayList<Token>tokens)                                                            {
    String distokenized=""                                                                                     ;
    Token lastToken=null                                                                                       ;
    for(ListIterator<Token>iter=tokens.listIterator();iter.hasNext();)                                         {
      Token t=iter.next()                                                                                      ;
      if(lastToken!=null&&lastToken.needsSymbolOrSpace()&&!t.isSymbol())                                       {
        distokenized+=" "                                                                                      ;}
      distokenized+=t.toString()                                                                               ;
      lastToken=t                                                                                              ;}
    return distokenized                                                                                        ;}}
