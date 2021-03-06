-injars build/distributions/infix-1.1/infix-1.1.jar
-outjars build/distributions/infix-1.1/infix-1.1-pub.jar

-libraryjars /Library/Java/JavaVirtualMachines/jdk1.7.0_55.jdk/Contents/Home/jre/lib/rt.jar
-libraryjars build/distributions/infix-1.1/antlr-4.7.2-complete.jar
-libraryjars build/distributions/infix-1.1/slf4j-api-1.7.7.jar
-libraryjars build/distributions/infix-1.1/slf4j-simple-1.7.7.jar

-optimizationpasses 9
-mergeinterfacesaggressively
-printmapping proguard-map.txt
-overloadaggressively
-keeppackagenames com.globalforge.infix.antlr.**,com.globalforge.infix.api.**
-keepattributes Exceptions, InnerClasses, Signature, SourceFile, LineNumberTable 


-keep class com.globalforge.infix.api.** {
    public <methods>;
}

-keep class com.globalforge.infix.example.** {
    public <methods>;
}

-keep class com.globalforge.infix.FIX*Mgr {
    public <methods>;
}

-keep class com.globalforge.infix.FixMessageMgr {
    public <methods>;
}

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Also keep - Database drivers. Keep all implementations of java.sql.Driver.
-keep class * extends java.sql.Driver
