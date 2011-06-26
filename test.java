package thejh.prettyJava                                         ;
                                                                 
import java.io.*                                                 ;
                                                                 
public class PrettyJava                                          {
  public static int getIndentation(String line)                  {
    for (int i = 0; i < line.length(); i++)                      {
      char c = line.charAt(i)                                    ;
      if (c != ' ')                                              {
        //return something                                       
        return i                                                 ;}}
                                                                 
    return -1                                                    ;}
                                                                 
  public static void testFor()                                   {
    for (int i = 0; i < 10; i++)                                 {
      System.out.println(i)                                      ;}
    for (int $number1 = 0; $number1 < 10; $number1++)            {
      for (int $number2 = 0; $number2 < 5; $number2++)           {
        System.out.println("foobar")                             ;}}}
                                                                 
  public static void foo(String[] paramNames)                    {
    String[] names = new String[]{"Alice", "Bob", "Evil Eve"};   ;
    for (String name: names)                                     {
      System.out.println(name)                                   ;}
    for (String name: paramNames)                                {
      System.out.println(name)                                   ;}}}
