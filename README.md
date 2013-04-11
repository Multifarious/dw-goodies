# dw-goodies
----

This repository collects odds and ends for Dropwizard services that have proven consistently useful.

## Setting Up for the Build Number Command

The `BuildNumber` command requires the following to be additions to the Maven project that is used to build the Dropwizard service in question.

First, the `<scm>...</scm>` section of the POM should be filled in, e.g.:

```xml
<scm>
  <developerConnection>scm:git:git@github.com:Multifarious/dw-goodies.git</developerConnection>
</scm>
```

Second, the `maven-scm-plugin` (provides Git integration), `buildnumber-maven-plugin` (populates Git SHA into `${buildNumber}`), and `maven-jar-plugin` (puts `${buildNumber}` in the JAR manifest) need to all be set up to work together:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-scm-plugin</artifactId>
  <version>1.8.1</version>
  <configuration>
    <connectionType>developerConnection</connectionType>
  </configuration>
</plugin>
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-jar-plugin</artifactId>
  <configuration>
    <archive>
...
    <manifestEntries>
      <Implementation-Build>${buildNumber}</Implementation-Build>
    </manifestEntries>
    </archive>
  </configuration>
</plugin>
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>buildnumber-maven-plugin</artifactId>
  <version>1.2</version>
  <executions>
    <execution>
      <phase>validate</phase>
      <goals>
        <goal>create</goal>
      </goals>
    </execution>
  </executions>
  <!-- alter these to inlfluence how build numbers are applied/enforced w.r.t. local changes and upstream -->
  <configuration>
    <doCheck>false</doCheck>
    <doUpdate>false</doUpdate>
  </configuration>
</plugin>
```

## Access with Maven

### Coordinates

Include the following in your `pom.xml`:

<pre>
&lt;dependency>
  &lt;groupId>io.ifar&lt;/groupId>
  &lt;artifactId>dw-goodies&lt;/artifactId>
  &lt;version>0-SNAPSHOT&lt;/version>
&lt;/dependency>
</pre>

### Snapshots

Snapshots are available from the following Maven repository:

<pre>
&lt;repository>
  &lt;id>multifarious-snapshots&lt;/id>
  &lt;name>Multifarious, Inc. Snapshot Repository&lt;/name>
  &lt;url>http://repository-multifarious.forge.cloudbees.com/snapshot/&lt;/url>
  &lt;snapshots>
    &lt;enabled>true&lt;/enabled>
  &lt;/snapshots>
  &lt;releases>
    &lt;enabled>false&lt;/enabled>
  &lt;/releases>
&lt;/repository>
</pre>

### Releases

None as yet, but when there are, they will be published via Maven Central.
