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

Set up the Build Environment
----------------------------
Install the latest Gradle 2.x.x from https://gradle.org.

Build the App
-------------
The first step in the pipeline is to parse the data dictionaries.

All commands issued from the project root.  

./gradlew parseFIX

The above command will parse the FIX data dictionaries (Quick FIX/J) and generate Java code offering fast lookups of FIX data.

./gradlew buildFIX

The above command will compile all the FIX data source code into a seperate library called fix-2.0.jar

./gradlew build

The above command will build and test the Infix code against fix-2.0.jar and create a library called infix-2.0.jar.  infix-2.0.jar is dependent upon fix-2.0.jar at both compile time and run time.

./gradlew distTar

The above command will create distribution containing all the needed runtime jar files and an example application.  Unpack the tar or zip file and proceed to "Test the App" if you want to test the sample application.

./gradlew eclipse

The above command will create an eclipse project that you can import directly using the infix directory created during the clone as the project root.

Test the App
-------------
InFIX comes with a sample application.  Take a peek at the README.txt file in the distribution.  See if you can execute the runExample.sh program and apply rules found in the help guide at http://infix.globalforge.com/roadmap.html

