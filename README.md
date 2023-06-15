## FatBinary
Gradle plugin than generates a fat binary with all the dependencies of a given Project.

A common use case is to generate tooling from jvm projects that could be portable to ci/local environments.

### Usage
Include plugin in your root build.gradle(kts) file:
```
plugins {
  id("io.github.cdsap.fatbinary") version "1.0"
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

### Implementation
The idea came from the [Diffuse](https://github.com/JakeWharton/diffuse) library where the binary is updated with the java
command execution:
```
binaryFile << "#!/bin/sh\n\nexec java \$JAVA_OPTS -jar \$0 \"\$@\"\n\n"
```

