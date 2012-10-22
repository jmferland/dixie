ABOUT

This is a small and incomplete project I started in 2009 to learn more about
web development in Java. Basic features include 

Today (Oct 2012) there are a lot of things I would
do differently, such as, use Hibernate to replace JDBC CRUD code and schema
generation, an explicit service layer, more (any?) tests, better structured
Java classes, and more.

But this still serves as a decent, albiet old, example of trying to gain
experience with:

* Stripes web framework
* FreeMarker Templating engine
  (src/main/webapp/WEB-INF/ftl)
* Image manipulation (for captcha and thumbnail generation)
* JDBC
* JEE
* Writing scripts
* Stored procedures
* Layered design (e.g. DAO, service layers)
* Maven

DATABASE SETUP

Requires mysql-server

1. Create database and user  
     mysql -u root -p
     CREATE DATABASE dixie;
     GRANT EXECUTE \
       ON dixie.* \
       TO 'dixie_sproc'@'localhost' \
       IDENTIFIED BY 'dixie_sproc';

2. Create the schema, add stored procedures, and add triggers by running
     $> cd src/main/resources/sql
     $> ./update.sh

3. If you chose a different username, password, db name, etc. update
     ./src/main/webapp/META-INF/context.xml

MVN

Requires mvn

1. Make sure the stripesstuff-0.1.jar is installed into a local maven
   repository as it doesn't have a hosted one:

     mvn install:install-file -Dfile=jars/stripesstuff-0.1.jar \
       -DgroupId=org.stripesstuff.plugin.security \
       -DartifactId=stripesstuff -Dversion=0.1 -Dpackaging=jar
 
2. Build eclipse projects

     mvn eclipse:eclipse

   To also download available sources for libraries, instead run:

     mvn eclipse:eclipse -DdownloadSources=true

3. Build the war file

     mvn package

   (or just use the dixie.war)

DEPLOY

1. Install Tomcat 6.0
   
     http://apache.parentingamerica.com/tomcat/tomcat-6/v6.0.36/bin/apache-tomcat-6.0.36.tar.gz

2. Drop the war (dixie.war or target/dixie.war) into the directory
   $CATALINA_HOME/webapps

3. Put the MySQL connector into the Tomcat lib folder

    cp jars/mysql-connector-java-5.1.22-bin.jar $CATALINA_HOME/lib/.

4. Start Tomcat 6.0

    ./$CATALINA_HOME/bin/startup.sh
