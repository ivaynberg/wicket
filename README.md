BRANCH NOTES
=================
Implements Ajax history

Notes:
* `target.setHistoryToken("users/edit/15");` to set the hash
* problem: since ajax pages are now versioned it is easy to evict the version of the page that did the full load - in which case non-ajax links wont work

What is done:
* page versions are no longer frozen during ajax requests - allowing normal page versioning to take place
* page version is sent as part of ajax response
* ajax response sends page version in Wicket-Page-Id header
* wicket allows Wicket-Page-Id header to override page id found in the url
* default ajax behaviors integrate RSH

Todo:
* check if RSH is apache2 compatible, if not find something else
* isolate javascript history SPI and provide default RSH adapter
* need to tweak rsh to make location of blank.html configurable at runtime
* ajax request target needs a #setToken(String token) which will represent the hash, use pageid by default
* for now back button ishandled by a simple full page redirect, this should be hanlded via ajax
  1. client side needs to remember which markup ids were updated as part of history
  2. on back button we invoke a special listener on the page instance pointed to by the *previous page id*
  3. previously updated components ids are passed to the listener
  4. listener constructs an ajax request target and adds the passed in markup ids
  5. hopefully this restores the page state back to what it was
  6. add ability to add javascript to request target that executes on back button
  7. add ability to add javascript to request target that executes after previous page state has been restored
     - this should allow user to augment what wicket does by default to restore state
  8. ability to override how wicket restores state by default by doing something to the ajax request target?
* merge rsh, json, init into a single js file and minify
* handle loading with an initial token like user/edit/16
  * supporting this means that tokens need to be namespaced with some path, just like wicket urls are currently namespaced with wicket/ so we can tell history tokens from regular page anchors
    * when user sets token user/edit/15 it should result in #wicket/user/edit/15
	  * wicket bit should use the same context value url mappers use

Apache Wicket 1.4
=================

This is the readme file for the Apache Wicket project. 

Apache Wicket is an open source, java, component based, web application
framework. With proper mark-up/logic separation, a POJO data model, and a
refreshing lack of XML, Apache Wicket makes developing web-apps simple and
enjoyable again. Swap the boilerplate, complex debugging and brittle code for
powerful, reusable components written with plain Java and HTML.

Apache Wicket can be found at: http://wicket.apache.org and is licensed under
the Apache Software Foundation license, version 2.0.

Contents
--------
 - License
 - Java/Application server requirements
 - What is in this package
 - Getting started
 - Dependencies
 - Building Wicket from source
 - Migrating from 1.3
 - Getting help
 - Cryptographic Software Notice

License
-------

Wicket is distributed under the terms of the Apache Software Foundation
license, version 2.0. The text is included in the file LICENSE in the root
of the project.

Java/Application server requirements
------------------------------------

Wicket requires at least Java 1.5. The application server for running your web
application should adhere to the servlet specification version 2.3 or newer.

What is in this package
-----------------------

The archive you just downloaded and unpacked contains the source code and the
jars of the core projects of Wicket. If you are just starting out, you probably
only need to include wicket-x.jar, where x stands for the version. As a rule,
use just the jars you need.

You will find the source code here:

 - src/
   - wicket
   - wicket-extensions
   - wicket-datetime
   - wicket-ioc
   - wicket-spring
   - wicket-velocity
   - wicket-quickstart
   - wicket-examples
   - wicket-auth-roles
   - wicket-guice
   - wicket-jmx
   - wicket-objectssizeof-agent

Here is a list of projects in this distribution and what they do.

 - wicket: the core project, includes the framework and basic components;
 - wicket-extensions: contains utilities and more specialized components;
 - wicket-auth-roles: a basic authorization package based on roles;
 - wicket-datetime: contains date/ time specific components such as a date picker;
 - wicket-jmx: registers JMX beans for managing things like your Wicket 
		configuration and markup cache;
 - wicket-objectssizeof-agent: utility for making better estimates of object 
		sizes in the JVM - most people probably never need this;
 - wicket-ioc: base project for IoC (aka DI) implementations such as 
		Spring and Guice;
 - wicket-spring: support project for using Spring with Wicket and including 
        Spring managed dependencies through using @SpringBean annotations;
 - wicket-guice: support project for using Google Guice with Wicket;
 - wicket-velocity: contains special components for rendering Velocity templates 
		using Wicket components - most people probably don't need this, but it 
		can be neat when you want to do CMS-like things;
 - wicket-examples: contains a basic component reference and many examples of 
		how to use Wicket and Wicket components, including examples for sub 
		projects such as wicket-spring, wicket-velocity and wicket-auth-roles.

Getting started
---------------

The Wicket project has several projects where you can learn from, and get
started quickly:

 - wicket-examples:

    shows all components in short usage examples, also available live on:
    http://www.wicketstuff.org/wicket14

 - wicket-quickstart:

    provides a skeleton project for use in NetBeans, Eclipse, IntelliJ IDEA
    and other major IDE's, without having to configure anything yourself. You
    can copy'n'paste the examples from the website into your pages and see
    them running on your own box.

 - qwicket (http://www.antwerkz.com/qwicket):

    Qwicket is a quickstart application for the wicket framework. Its intent
    is to provide a rapid method for creating a new wicket project with the
    basic infrastructure in place so that you can quickly get to the meat of
    your application rather than mucking with the plumbing of a wicket
    application. Currently, the system only supports spring and hibernate
    built with ant.

 - AppFuse light - Wicket edition (https://appfuse-light.dev.java.net/)

    AppFuse Light is a can all do it all quickstart setup for almost all
    possible permutations for building Java web applications and ORM
    technologies. It features over 60 downloads and combines each available
    web application framework with Hibernate, iBatis, JDO (JPOX), OJB and
    Spring JDBC.

Dependencies
------------

The easiest way of getting the dependencies of your Wicket based projects right 
is to use Apache Maven (http://maven.apache.org) with your projects and include
the wicket dependencies you want is outlined in the wicket-quickstart. 
Maven will then take care of including the appropriate dependencies.

If you do not want to use maven, here is a break down of the dependencies you need.

 - wicket and wicket-extensions:

	You only need to include the Servlet API (2.3, just for compiling) and 
	the SLF4J logging implementation you want. You cannot use Wicket without 
	adding a SLF4J logging implementation to your classpath. Most people use 
	log4j. If you do, just include slf4j-log4j12.jar on your classpath to get 
	Wicket to use log4j too. If you want to use commons-logging or JDK14 
	logging or something else, please see the SLF4J site (http://www.slf4j.org/)
	for more information.

	As the following projects all depend on wicket, they inherit these dependencies.

 - wicket-datetime:

 	Joda-Time 1.4 (http://joda-time.sourceforge.net/)

 - wicket-velocity:
 	
	Apache Velocity 1.4 (http://velocity.apache.org/) and it's 
	dependencies (it ships a velocity-deps jar for convenience)

 - wicket-ioc:

	gclib nodep 2.1.3 (http://cglib.sourceforge.net/) and 
	asm 1.5.3 (http://asm.objectweb.org/)

 - wicket-spring:

 	wicket-ioc and Spring (http://www.springframework.org/) and
 	it's dependencies

 - wicket-guice:

	Google Guice (http://code.google.com/p/google-guice/).

 - wicket-examples:

 	All of the above.

Building Wicket from source
---------------------------

The Wicket distribution contains the final Wicket jar. You can use this
directly in your applications. The Wicket project also uploads the source 
and JavaDoc jars as well as the final jar to the Maven repository used by
the Maven build tool. So there is actually no specific need to build Wicket
yourself from the distribution.

Building using maven 2, change the working directory to src and either do:

 - mvn package

    creates wicket-x.y.z.jar in target/ subdirectory.

 - mvn install

    creates wicket-x.y.z.jar in target/ subdirectory and installs the file
    into your local Maven repository for use in other projects.

Migrating from 1.3
------------------

This file is a copy of the migration guide from available on our Wiki:

    http://cwiki.apache.org/WICKET/migrate-14.html
    
Getting help
------------

 - Read the online documentation available on our website
   (http://wicket.apache.org)

 - Read the migration guide (migrate-14.html)

 - Read the mailing archives available on Nabble, GMane and Apache

 - Send a complete message containing your problem, stacktrace and problem
   you're trying to solve to the user list (users@wicket.apache.org)

 - Ask a question on IRC at freenode.net, channel ##wicket


Cryptographic Software Notice
-----------------------------

This distribution includes cryptographic software.  The country in which you currently reside may
have restrictions on the import, possession, use, and/or re-export to another country, of encryption
software.  BEFORE using any encryption software, please check your country's laws, regulations and
policies concerning the import, possession, or use, and re-export of encryption software, to 
see if this is permitted.  See http://www.wassenaar.org for more information.

The U.S. Government Department of Commerce, Bureau of Industry and Security (BIS), has classified
this software as Export Commodity Control Number (ECCN) 5D002.C.1, which includes information security
software using or performing cryptographic functions with asymmetric algorithms.  The form and manner
of this Apache Software Foundation distribution makes it eligible for export under the License Exception
ENC Technology Software Unrestricted (TSU) exception (see the BIS Export Administration Regulations, 
Section 740.13) for both object code and source code.

The following provides more details on the included cryptographic software:

For encoding HTTP URL data (see org.apache.wicket.protocol.http.request.CryptedUrlWebRequestCodingStrategy) 
Wicket requires the Java Cryptography extensions (http://java.sun.com/javase/technologies/security/). Wicket
does not include these libraries itself, but is designed to use them.
