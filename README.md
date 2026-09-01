## FatBinary
Gradle plugin than generates a fat binary with all the dependencies of a given Project.

A common use case is to generate tooling from jvm projects that could be portable to ci/local environments.

### Usage
Include plugin in your root build.gradle(kts) file:
```
plugins {
  id("io.github.cdsap.fatbinary") version "1.1.0"
}
```
Defines the main class of the application and the binary name
```
fatBinary {
    mainClass = "com.example.Main"
    name = "binary"
}
```

Finally execute the binary:
```
./binary
```

### Local CLI matrix
Publish the plugin locally, build the e2e consumer binary, and smoke-test it (TaskReport-style):

```
./scripts/test-cli-matrix.sh
```

To exercise multiple JDKs on your machine, pass their `JAVA_HOME` paths:

```
./scripts/test-cli-matrix.sh "$JAVA_HOME" /path/to/jdk-21 /path/to/jdk-25
```

### Implementation
The idea came from the [Diffuse](https://github.com/JakeWharton/diffuse) library where the binary is updated with the java
command execution:
```
binaryFile << "#!/bin/sh\n\nexec java \$JAVA_OPTS -jar \$0 \"\$@\"\n\n"
```

