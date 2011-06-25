**pretty-java** is a quick-and-dirty source-to-source compiler that compiles good-looking code to Java. It doesn't work with an AST or a parser or anything fancy, it just uses regexes and conventions to replace stuff in your code **without trying to understand it**.

Things it already does
======================
Automatic curly block brace and semicolon insertion
---------------------------------------------------
It automatically inserts braces and semicolons. At the right side of your code, not in the middle where it would look bad (actually, like normal java).

Automatic braces for `if`, `for`, `else if` and `while`
-------------------------------------------------------
Every line that begins with one of those keywords will automatically get braces. Example of valid code:

    if foo == 3

Cooler `for` loops
------------------
Some examples of for loops:

Notice how it automatically extracts the type of `name`!

    String[] names = new String[]{"Alice", "Bob", "Evil Eve"}
    for name in names
      System.out.println(name)

Loop through ranges (start is inclusive, end is exclusive):

    for i in [0..array.length]
      System.out.println(i)
