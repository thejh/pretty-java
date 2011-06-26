package thejh.prettyJava                                                      ;
                                                                              
import java.util.*                                                            ;
                                                                              
class LineWorker                                                              {
  private String line                                                         ;
                                                                              
  public LineWorker(String line)                                              {
    this.line=line                                                            ;}
                                                                              
  public void implicitCallBrackets(ArrayList<Token>tokens)                    {
    while(true)                                                               {
      Token lastToken=null                                                    ;
      for(ListIterator<Token>iter=tokens.listIterator();iter.hasNext();)      {
        Token t=iter.next()                                                   ;
        if(lastToken!=null)                                                   {
          if(lastToken.isVariably()&&t.isWord())                              {
            iter.previous()                                                   ;
            iter.add(new SymbolToken('('))                                    ;
            tokens.add(new SymbolToken(')'))                                  ;
            break                                                             ;}}
        lastToken=t                                                           ;}
      break                                                                   ;}}
                                                                              
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
