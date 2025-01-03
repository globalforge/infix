# InFIX
FIX Protocol Message Transformer
--------------------------------
InFIX is a Java based FIX Protocol Message transformer tool that allows financial trading applications to address and transform data contained in a FIX messages in constant time by applying rules defined in an easy to use text language.

This feature allows FIX connectivity hubs, trading systems, and exchanges to define sets of data transformations outside of application code and apply them at runtime. Because data and rules are independent, applications can define new rules dynamically and apply them without a code release.

InFIX greatly reduces the number of code releases needed when on-boarding new FIX clients, reduces the on-boarding time considerably and allows support personnel to establish connectivity with diverse requirements without developer involvement.

InFIX has also successfully been used in production to increase order flow and allow business with new clients that may not have been possible without it.  This is due to it's ability to transform working order types allowing both parties to conduct business where they might not have been able to.

Preamble
--------
InFIX is a Java 11 application that is known to build on Windows, Linux and OS X but it should be buildable on any UNIX platform.  Java 11 is used because antlr 4.10.1 was compiled with Class File Version 55.0 (Java 11).

Java 11 works with Gradle 5.6.2 but Java 17 requires more recent versions of Gradle which are not compatible with the current build.gradle file.

Porting build.gradle to the latest version of gradle is due for an upgrade.

The engine behind InFIX is ANTLR4 (http://antlr.org).  With a little work, InFIX can be ported to C#, Python (2 and 3) and at some point in the future C++. The language limitations are due to currently supported code generation targets by Antlr4.

Set up the Build Environment
----------------------------
The Infix build will quietly use Gradle 5.6.2 from https://gradle.org. There is no need to download Gradle before building.

Make sure JAVA_HOME points to Java 11


##### JAVA_HOME=C:\Program Files\Java\jdk-11

Build the App
-------------
The first step in the pipeline is to parse the data dictionaries. This will build the data dictionary jar called fix-2.0.jar.

All commands issued from the project root.  

./gradlew parse

The above command will parse the FIX data dictionaries (Quick FIX/J) and generate Java code offering fast lookups of FIX data.

./gradlew build

The above command will build and test the Infix code against fix-2.0.jar and create a library called infix-x.x.x.jar.  infix-x.x.x.jar is dependent upon fix-2.0.jar at both compile time and runtime.

./gradlew distTar

The above command will create distribution containing all the needed runtime jar files and an example application.  Unpack the tar or zip file and proceed to "Test the App" if you want to test the sample application.

./gradlew eclipse

The above command will create an eclipse project that you can import directly using the infix directory created during the clone as the project root.

./gradlew clean; ./gradlew javadoc; ./gradlew parse; ./gradlew build; ./gradlew distTar

The above command does it all.

./gradlew javadocJar

The above command generates a javadoc jar file for infix

./gradlew sourcesJar

The above command gratis a source code jar file for infix.

./gradlew :fix:javadocJar

The above command generates a javadoc jar file for the fix repeating group data dictionary

./gradlew :fix:sourcesJar

The above command generates a source code jar file for the fix repeating group data dictionary.

Test the App
-------------
InFIX comes with a sample application.  Take a peek at the README.txt file in the distribution.  See if you can execute the runExample.sh program and apply rules found in the help guide. \
Documentation: http://ec2-18-212-197-10.compute-1.amazonaws.com/infixweb/roadmap.html \
Sample application: http://ec2-18-212-197-10.compute-1.amazonaws.com/infixweb/sample.jsp.

