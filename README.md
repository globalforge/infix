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

Set up the Build Environment
----------------------------
Install Gradle from https://gradle.org.

Infix is tested with Gradle version 1.11 and it may not work with the latest 2.x.x version.  This would be a great thing to do next.

Build the App
-------------
The first step in the pipeline is to build the data dictionaries.  This is one time operation and should never have to be done again until the FIX community releases another FIX version. Issue the below task and a data dictionary will be build for each standard FIX version.

All commands issued from the project root.  
./gradlew buildXML

The above command will create a series of xml files.  One file per FIX version.  The next step is generate the java source code used by InFIX from the xml files.

./gradlew buildSrc

The above command generates java source code for all the FIX data dictionaries.  The next step is to build the InFIX language recognizer and compile all the source code into a single library called infix-1.0.jar

./gradlew distTar

The above command will create a distribution archive called infix-1.0.tar.  Unpack the tar file and proceed to the next step.

Test the App
-------------
InFIX comes with a sample application.  Take a peek at the README.txt file in the distribution.  See if you can execute the runExample.sh program and apply rules found in the help guide at http://infix.globalforge.com/roadmap.html



