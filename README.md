**pretty-java** is a quick-and-dirty source-to-source compiler that compiles good-looking code to Java. It doesn't work with an AST or a parser or anything fancy, it just uses regexes, a tokenizer and conventions to replace stuff in your code **without really trying to understand everything**.

Things it already does
======================
Automatic curly block brace and semicolon insertion
---------------------------------------------------
It automatically inserts braces and semicolons. At the right side of your code, not in the middle where it would look bad (actually, like normal java).

Implicit calls
==============
If you write a method name (actually, a word that isn't a keyword and begins with a small letter) followed by another non-symbol and non-keyword, an opening bracket will be inserted between the two and a closing bracket will be appended to the end ot the line. Valid code:

    System.out.println Integer.parseInt "100"

Automatic braces for `if`, `for`, `else if` and `while`
-------------------------------------------------------
Every line that begins with one of those keywords will automatically get braces. Example of valid code:

    if foo == 3
      System.out.println "foo is three!"

Cooler `for` loops
------------------
Some examples of for loops:

Notice how it automatically extracts the type of `name`!

    String[] names = new String[]{"Alice", "Bob", "Evil Eve"}
    for name in names
      System.out.println name

Loop through ranges (start is inclusive, end is exclusive):

    for i in [0..array.length]
      System.out.println(i)

Loop through ranges without an index variable (jump in the air ten times):

    for [0..10]
      jumpIn theAir