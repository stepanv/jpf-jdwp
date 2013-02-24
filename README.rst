                      
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

Projects description
--------------------
Currently, two projects are located in this repo:

 1. JDWP (debugee-side) to JPF implementation in project ``gov.nasa.jpf.jdwp``
    
    * still based on GNU Classpath JDWP project (mainly written by Keith Seitz) 
      
      with reimplemented JDWP-to-VM interface

    * following pending changes:
       * complete redesign and 
       * refactor and
       * JDWP 7 specification implementation
       * JDWP BE idea of working redesign

      might lead to complete from GNU-Classpath-JDWP separation
 #. JDI (dubugger-side) to JPF implentation in project ``jdi-test``
    
    * meant as a proof of concept only
    * not well structured, not well designed, (not worth of reading)

JDWP-to-JPF Building and Installing
===================================

Eclipse workspace preparation:
------------------------------

 1. clone ``jdwp`` branch of ``gov.nasa.jpf.core`` and import it into Eclipse
 #. clone this repository and import ``gov.nasa.jpf.jdwp`` into Eclipse

Running:
--------
Currently, only manual JDWP setup is working (e.g. you must start both processes (JPF with JDWP enabled + the debugger) manually.

Create new *Debug/Run Java Application* configuration:
 1. Main class: ``gov.nasa.jpf.jdwp.JDWPRunner``
 #. As Program arguments you're supposed to include standard JPF arguments so that JPF is able to run a main class.
    For example: ``+target=your.package.MainClass +classpath=+,/path/to/the/compiled/classes/bin``
 #. To enable JDWP, add VM property ``jdwp`` with standard JDWP arguments.
    For example: ``-Djdwp=transport=dt_socket,server=y,suspend=y,address=51255``
 #. Run or Debug it
 #. Attach the debugger (assuming you're running JPF as a jdwp server)

Apparently, it's possible to run it without Eclipse, but there is no build system yet.
    

JDI-to-JPF Building and Installing
==================================

**Right now, everything works with Eclipse Juno 20120920-0800 only!**

To prepare Eclipse workspace:
-----------------------------

 1. clone ``com.sun.jdi`` and import it into Eclipse as existing project 
 #. clone ``eclipse-plugin`` branch of ``gov.nasa.jpf.core`` and import it into Eclipse
 #. clone ``jpf-inplace`` branch of eclipse.jdt.debug and import into Eclipse

   a. ``org.eclipse.jdt.debug``
   #. ``org.eclipse.jdt.launching``

 #. clone this repository and import it into Eclipse

To fix Eclipse workspace:
-------------------------

 1. Ensure you have jdk 1.6 installed and available in workspace
 #. Change Missing API baseline Error to Warning in *Preferences -> Plug-in Development -> API Baselines*

To run the setup:
-----------------
 1. Debug one of the plugins as a Eclipse Application (i.e. all the plugins must be enabled in the target Eclipse instance)

   a. Don't forget to increase PermSize: ``-XX:MaxPermSize=256m``

 #. When new Eclipse is running, create a java project and debug it.

