# infix
FIX Protocol Message Transformer
--------------------------------
InFIX is a FIX Protocol Message transformer tool that allows financial trading applications to address and transform data contained in a FIX messages in constant time by applying rules defined in an easy to use text language.

This feature allows FIX connectivity hubs, trading systems, and exchanges to define sets of data transformations outside of application code and apply them at runtime. Because data and rules are independent, applications can define new rules dynamically and apply them without a code release.

InFIX greatly reduces the number of code releases needed when on-boarding new FIX clients, reduces the on-boarding time considerably and allows support personnel to establish connectivity with diverse requirements without developer involvement.

Preamble
--------
InFIX is a Java 1.7 application built on an Apple MacBook Air running OS X version 10.10.2 but it should be buildable on any UNIX-like platform.  Work is needed to enhance the build scripts to produce a Windows executable.  Care to help?

The engine behind InFIX is ANTLR4 (http://antlr.org).  With a little work, InFIX can be ported to C#, Python (2 and 3) and at some point in the future C++. The language limitations are due to currently supported code generation targets by Antlr4.

If you care to help with any of the above, why not join the InFIX project as a developer?

Building the app
----------------
Install Gradle from https://gradle.org.

Infix is tested with Gradle version 1.11 and it may not work with the latest 2.x.x version.  This would be a great thing to do next.




