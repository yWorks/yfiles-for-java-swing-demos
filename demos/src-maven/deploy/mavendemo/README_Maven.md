# yFiles for Java (Swing) Maven demo

This is a simple demo showing how to set up a yFiles for Java (Swing) project using Apache Maven.

## Prerequisites

* An up-to-date Maven installation. The demo was tested with Maven >= 3.8.
* A JDK version 8 or newer.
* `mvn --version` shows the relevant versions.

## Running during development

* yFiles is delivered as a single JAR file. To use it in a Maven project, the simplest approach is to install it as a Maven dependency into the local repository.
  * In the `lib` folder of the package run: `mvn install:install-file -Dfile="yfiles-for-java-swing.jar" -DgroupId="com.yworks.yfiles" -DartifactId="yfiles-for-java-complete" -Dversion="3.6" -Dpackaging="jar"`
* Copy the license (e.g. `com.yworks.yfiles.java.developmentlicense.xml`) into the `src/main/java` folder of the demo.
  * In the yFiles evaluation package the license file is already provided in this folder.
  * The license is going to be copied during build to the build folder via the `maven-resources-plugin` as configured in the `pom.xml`.
* Run `mvn compile exec:java` in the directory where the pom resides.

## Packaging and Obfuscation

The demo provides configurations for packaging and obfuscation. The whole build (including obfuscation) can be run with

`mvn clean package`

in the directory where the pom resides.

### Packaging

The `maven-assembly-plugin` packages all necessary classes into one big JAR file.

### Obfuscation

Obfuscation is accomplished with yWork's free yGuard tool. yGuard is configured as a maven build plugin and therefore need not be downloaded separately.

See the [yGuard home page](https://www.yworks.com/products/yguard) for additional information.

### Running the obfuscated demo App

After running `mvn clean package`, change to the `target` directory.
Here a `mavendemo-1.0.0-jar-with-dependencies_obfuscated.jar` should have been built.
This Jar file contains all dependencies. To run it from the command line use:

```
java -cp .\mavendemo-1.0.0-jar-with-dependencies_obfuscated.jar mavendemo.MavenDemo
```
