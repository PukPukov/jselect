When one project on your machine requires to be build from JDK <22 and other from JDK >= 24, you can't select single JAVA_HOME globally anymore. It gets even worse when some tools like Gradle are not reading JAVA_HOME and requiring you to pass jdk path as their own cli argument.

This project tries to escape jdk version hell. Just write paths to your JDKs in configuration.json and it will generate metric shit ton of shortcuts, including app-specific, which can be used after you place them in any PATH folder like this:

- `jh21 mvn install` sets JAVA_HOME and executes arbitrary command
- `java21 -jar program.jar` calls specific java version
- `gradlew21 build` calls gradlew with specific java

New templates can be easily added, although you will need to recompile JSelect.

## Build
### Requirements
- `mvn` installed
- `JDK 21` installed via JSelect
### Process
`jh21 mvn clean install`

## TODO

- [ ] Generate only OS-relevant shortcuts (no .bat on linux)
- [ ] User-level templates
- [ ] Other programs? I see how it could be used for maven and just for example (the benefit over simple shell aliases can be unification between different machines). But major rework would be needed, e.g. at least template target declaration, extensive configuration/placeholders

## Why not jEnv?

Its too complicated for such a simple task of templating shell scripts. The goal is to just run command with java version in it.