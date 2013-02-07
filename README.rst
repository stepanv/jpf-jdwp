                      
===========================
Java PathFinder JPDA README
===========================

General Information about JPF JPDA 
==================================

All sources here, at BitBucket such as:
 * gov.nasa.jpf.core
 * com.sun.jdi
 * eclipse.jdt.debug
and including this repository are intended for my thesis.

Building and Installing
=======================

**Right now, everything works with Eclipse Juno 20120920-0800 only!**

To prepare Eclipse workspace:
-----------------------------

 1. clone com.sun.jdi and import it into Eclipse as existing project 
 #. clone 'eclipse-plugin' branch of gov.nasa.jpf.core and import it into Eclipse
 #. clone 'jpf-inplace' branch of eclipse.jdt.debug and import into Eclipse
   a. org.eclipse.jdt.debug
   #. org.eclipse.jdt.launching
 #. clone this repository and import it into Eclipse
If you are having problems installing and running JPF
please look at the documentation on the wiki at:

To fix Eclipse workspace:
-------------------------

 1. Ensure you have jdk 1.6 installed and available in workspace
 #. Change Missing API baseline Error to Warning in Preferences -> Plug-in Development -> API Baselines

To run the setup:
-----------------
 1. Debug one of the plugins as a Eclipse Application (i.e. all the plugins must be enabled in the target Eclipse instance)
   a. Don't forget to increase PermSize: -XX:MaxPermSize=256m
 #. When new Eclipse is running, create a java project and debug it.


