                      
===========================
Java PathFinder JPDA README
===========================

General Information about JPF JPDA 
==================================

All following repositories at BitBucket:
 * https://bitbucket.org/stepanv/jpf-jdwp
 * https://bitbucket.org/stepanv/jpf-jdwptest
 * https://bitbucket.org/stepanv/jpf-core
 * https://bitbucket.org/stepanv/eclipse-jpf
 * https://bitbucket.org/stepanv/eclipse-jpf-updatesite

are intended for my master thesis https://is.cuni.cz/studium/eng/dipl_st/index.php?doo=detail&did=114827 as well as my Google Summer of Code 2013 project http://www.google-melange.com/gsoc/project/google/gsoc2013/stepanv/21001.

Javadoc ocumentation links:
 * http://stepanv.bitbucket.org/jpf-jdwp/javadoc/
 * http://stepanv.bitbucket.org/eclipse-jpf/javadoc/

Projects description
--------------------

JPDA JDWP backend (debugee-side) for Java PathFinder implementation in JPF project ``jpf-jdwp``.
For the network related layer, it is reused a part of GNU Classpath JDWP project (mainly written by Keith Seitz) .

This ``jpf-jdwp`` project depends on ``jpf-core`` from the repository stepanv/jpf-core which has several modifications in the ``jdwp-v7`` branch.

Environment preparation
=======================
Several steps have to be followed:
 1. Clone ``jdwp-v7`` branch of ``jpf-core`` project from the stepanv/jpf-core repository and enable it in ``site.properties`` (while development of JDWP is in process there are always several modifications that aren't reflected in the default branch of ``jpf-core``)
 #. Clone this repository and locate the ``jpf-jdwp`` project (in the root of the repo)
 #. Add jpf-jdwp to your ``site.properties`` file and enable it as an extension: ::
    jpf-jdwp = /path/to/the/project/jpf-jdwp
    extensions = ${jpf-jdwp}, ... some other extensions ...

Building and Installing
===================================
To build the ``jpf-jdwp`` project use the Ant tool or Eclipse IDE.

Building from command line
--------------------------
Just as with other JPF projects, run ant from the root of the ``jpf-jdwp`` project.

Building from Eclipse IDE
-------------------------

Eclipse workspace preparation:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 1. Import the ``jpf-core`` project from the ``jdwp-v7`` branch of the ``stepanv/jpf-core`` repository into Eclipse
 #. Import the ``jpf-jdwp`` project from this repository into Eclipse


Running:
========
The easiest and the preferred way how to enable the JDWP is using ``eclipse-jpf`` plugin. For further details refer to https://bitbucket.org/stepanv/eclipse-jpf.

Currently, there are 2 methods how to enable JDWP backend in JPF:
 1. Enabling JDWP by adding the JDWPListener in your JPF configuration
 #. Running JDWP directly from it's main class. JDWP takes care of running JPF


Running as a listener
---------------------

To enable JDWP listener, do
 1. Add jpf-jdwp to your ``site.properties`` file and enable it as an extension: ::
    jpf-jdwp = /path/to/the/project/jpf-jdwp
    extensions = ${jpf-jdwp}, ... some other extensions ...
 #. Enable the ``gov.nasa.jpf.jdwp.JDWPListener`` listener in your JPF run configuration (as desribed at JPF wiki http://babelfish.arc.nasa.gov/trac/jpf/wiki/user/run): ::
    +listener=gov.nasa.jpf.jdwp.JDWPListener (overrides)
    ++listener=gov.nasa.jpf.jdwp.JDWPListener, (prepends)
 #. Control the JDWP using the same options/values as when running the standard JDWP agentlib as desribed in http://docs.oracle.com/javase/6/docs/technotes/guides/jpda/conninv.html
    a. Available options are: ``transport``, ``server``, ``address``, ``timeout`` and ``suspend``
    #. All these options are set into one JPF property ``jpf-jdwp.jdwp``
    #. As an example refer to: ::
       +jpf-jdwp.jdwp=transport=dt_socket,server=y,suspend=y,address=8000
 #. You have to setup a debugger side accordingly to what you set in the ``jpf-jdwp.jdwp`` property
    a. Whether the debugger has to listen on a port
    #. or to attach to some address:port

Example
-------
Normally, you would just 
 1. Add following two lines into your ``.jpf`` file (assuming you prepared your environment correctly): ::
    +listener=gov.nasa.jpf.jdwp.JDWPListener
    +jpf-jdwp.jdwp=transport=dt_socket,server=y,suspend=y,address=8000
 #. Attach your debugger to the port 8000
  
    
Running JDWP directly
---------------------
As with a standard Java application you would need to
 1. Run a main class ``gov.nasa.jpf.jdwp.JDWPRunner``
 #. Add ``JDWP`` property ``jpf-jdwp.jdwp`` with standard JDWP arguments.
 #. Tell JPF which application you want to run using the property ``target`` and also to setup a classpath using ``classpath`` property

Example
-------
In Eclipse IDE you would create new *Debug/Run - Java Application* configuration:
 1. Main class: ``gov.nasa.jpf.jdwp.JDWPRunner``
 #. As Program arguments you're supposed to include standard JPF arguments so that JPF is able to run a main class.
    For example: ``+target=your.package.MainClass +classpath=+,/path/to/the/compiled/classes/bin``
 #. To enable JDWP, add JPF property ``jpf-jdwp.jdwp`` with standard JDWP arguments.
    For example (to start JDWP agent at localhost:51255): ``+jpf-jdwp.jdwp=transport=dt_socket,server=y,suspend=y,address=51255``
 #. Run or Debug it

Now, Attach the debugger (assuming you're running JPF as a jdwp server ) by using *Remote Java Application* from the *Debug Configuration* wizzard.

Apparently, it's possible to run it without Eclipse, but there is no build system yet.


Full working example in Eclipse:
================================

The first simple example is to run ``my.packagge.MainClass`` that is included in the JDWP project.

To enable JDWP in the application
---------------------------------
In Eclipse, create new *Debug/Run - Java Application* configuration that will run JPF and the program in it:
 1. Main class: ``gov.nasa.jpf.jdwp.JDWPRunner``
 #. As *Program arguments* set (do not substitute the placeholder/variable - Eclipse will do it for you automatically): ``+target=my.packagge.MainClass +classpath=+,${workspace_loc:jpf-jdwp/build/examples}``
 #. Enable JDWP by adding one more thing to *Program arguments*: ``+jpf-jdwp.jdwp=transport=dt_socket,server=y,suspend=y,address=8000`` 
 #. Run it (you can also Debug it but that means you will debug JPF itself (including JDWP implementaion) too). It will stay suspended until you attach a debugger.

To debug it
-----------
Create new *Debug - Remote Java Application* configuration that will attach the debugger to the application that is about to start.
 1. Put a breakpoint into the ``my.packagge.MainClass`` so that it gets suspended when the breakpoint is hit
 #. Connection Properties stay defualt: Host ``localhost`` and Port ``8000``
 #. Debug it

About
==================================

Websites
--------
Bitbucket links: http://stepanv.bitbucket.org

GSoC 2013 JPDA for JPF main page: http://stepanv.bitbucket.org/gsoc2013

Author
------
Stepan Vavra <vavra.stepan AT gmail.com> http://cz.linkedin.com/in/stepanvavra


