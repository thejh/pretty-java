package thejh.prettyJava                                                      ;
                                                                              
import java.util.*                                                            ;
                                                                              
class LineWorker                                                              {
  private String line                                                         ;
  private Scope scope                                                         ;
  private SourceClass srcClass                                                ;
                                                                              
  public LineWorker(String line,Scope scope,SourceClass srcClass)             {
    this.line=line                                                            ;
    this.scope=scope                                                          ;
    this.srcClass=srcClass                                                    ;}
                                                                              
  public void implicitCallBrackets(ArrayList<Token>tokens)                    {
    while(true)                                                               {
      Token lastToken=null                                                    ;
      boolean found=false                                                     ;
      for(ListIterator<Token>iter=tokens.listIterator();iter.hasNext();)      {
        Token t=iter.next()                                                   ;
        if(lastToken!=null)                                                   {
          if(lastToken.isVariably()&&!t.isSymbol()&&!t.isKeyword())           {
            iter.previous()                                                   ;
            iter.add(new SymbolToken('('))                                    ;
            tokens.add(new SymbolToken(')'))                                  ;
            found=true                                                        ;
            break                                                             ;}}
        lastToken=t                                                           ;}
      if(!found)                                                              {
        break                                                                 ;}}}
                                                                              
  public void recognizeFloatingPointNumbers(ArrayList<Token>tokens)           {
    Token twoAgo=null                                                         ;
    Token oneAgo=null                                                         ;
    for(ListIterator<Token>iter=tokens.listIterator();iter.hasNext();)        {
      Token t=iter.next()                                                     ;
      if(twoAgo!=null)                                                        {
        boolean lastWasDot=oneAgo.isSymbol()&&oneAgo.toString().equals(".")   ;
        boolean twoNumbers=t.isNumber()&&twoAgo.isNumber()                    ;
        if(lastWasDot&&twoNumbers)                                            {
          iter.previous()                                                     ;
          iter.remove()                                                       ;
          iter.previous()                                                     ;
          iter.remove()                                                       ;
          NumberToken numtok=(NumberToken)twoAgo                              ;
          numtok.value=((NumberToken)twoAgo).value+"."+numtok.value           ;
          twoAgo=null                                                         ;
          oneAgo=null                                                         ;
          continue                                                            ;}}
      twoAgo=oneAgo                                                           ;
      oneAgo=t                                                                ;}}
                                                                              
  public String doIt()                                                        {
    LineTokenizer tokenizer=new LineTokenizer(line)                           ;
    ArrayList<Token>tokens=tokenizer.doIt()                                   ;
    recognizeFloatingPointNumbers(tokens)                                     ;
    implicitCallBrackets(tokens)                                              ;
    return distokenize(tokens)                                                ;}
                                                                              
  public String distokenize(ArrayList<Token>tokens)                           {
    String distokenized=""                                                    ;
    Token lastToken=null                                                      ;
    for(ListIterator<Token>iter=tokens.listIterator();iter.hasNext();)        {
      Token t=iter.next()                                                     ;
      if(lastToken!=null&&lastToken.needsSymbolOrSpace()&&!t.isSymbol())      {
        distokenized+=" "                                                     ;}
      distokenized+=t.toString()                                              ;
      lastToken=t                                                             ;}
    return distokenized                                                       ;}}
