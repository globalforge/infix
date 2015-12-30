# infix
FIX Protocol Message Transformer
--------------------------------
InFIX is a FIX Protocol Message transformer tool that allows financial trading applications to address and transform data contained in a FIX messages in constant time by applying rules defined in an easy to use text language.

This feature allows FIX connectivity hubs, trading systems, and exchanges to define sets of data transformations outside of application code and apply them at runtime. Because data and rules are independent, applications can define new rules dynamically and apply them without a code release.

InFIX greatly reduces the number of code releases needed when on-boarding new FIX clients, reduces the on-boarding time considerably and allows support personnel to establish connectivity with diverse requirements without developer involvement.

Preamble
--------
InFIX is a Java 1.8 application that is known to build on Windows and OS X but it should be buildable on any UNIX platform.

The engine behind InFIX is ANTLR4 (http://antlr.org).  With a little work, InFIX can be ported to C#, Python (2 and 3) and at some point in the future C++. The language limitations are due to currently supported code generation targets by Antlr4.

If you care to help with any of the above, why not join the InFIX project as a developer?

Set up the Build Environment
----------------------------
Install the latest Gradle 2.x.x from https://gradle.org.

Build the App
-------------
The first step in the pipeline is to build the data dictionaries.  This is one time operation and should never have to be done again until the FIX community releases another FIX version. Issue the below task and a data dictionary will be build for each standard FIX version.

All commands issued from the project root.  

./gradlew buildXML

The above command will create a series of xml files.  One file per FIX version.  The next step is generate the java source code used by InFIX from the xml files.

./gradlew buildSrc

The above command generates java source code for all the FIX data dictionaries.  The next step is to build the InFIX language recognizer and compile all the source code into a single library called infix-1.1.jar

./gradlew distTar

The above command will create a distribution archive called infix-1.1.tar.  Unpack the tar or zip file and proceed to the next step.

./gradlew eclipse

The above command will create an eclipse project that you can import directly using the infix directory created during the clone as the project root.  You shouldn't have to fiddle with any of the project settings during the import.  Once imported, all the dependencies should be resolved as long as you followed the previous steps.  There should be no compilation errors.  You may have to disable project specific setting for java and use your workspace settings.  Use Java 8 if possible.

Test the App
-------------
InFIX comes with a sample application.  Take a peek at the README.txt file in the distribution.  See if you can execute the runExample.sh program and apply rules found in the help guide at http://infix.globalforge.com/roadmap.html



