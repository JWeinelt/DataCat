[![Version](https://img.shields.io/badge/version-v1.0.1-blue)]()
[![Build](https://github.com/JWeinelt/DataCat/actions/workflows/maven.yml/badge.svg)](https://github.com/JWeinelt/DataCat/actions/workflows/maven.yml)

This is the ``ui`` module. It contains the core editor and the DataCat flow client.

## Dependencies
- ``dbx`` (DataCat API)
- ``gson`` + ``jackson`` (JSON Parsing)
- ``flatlaf`` (Style)
- ``apache commons-compress`` (File handling)
- ``FifeSoft: spellchecker`` (Editor)
- ``FifeSoft: rsyntaxtextarea`` (Editor)
- ``FifeSoft: autocomplete`` (Editor)
- ``poi-ooxml`` (Reading and creation MS Excel worksheets)
- ``semver4j`` (Version management)
- ``java-keyring`` (Use of the OS' key storage)

## Using the Editor API
The editor provides some API components that are not covered by the DBX API.
You can use it by adding this to your project:
### Maven
```xml
<repositories>
    <repository>
        <name>DataCat Repository</name>
        <id>maven-releases</id>
        <url>https://repo.codeblocksmc.com/repository/maven-releases/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>de.julianweinelt.datacat</groupId>
        <artifactId>editor-api</artifactId>
        <version>${version}</version>
    </dependency>
</dependencies>
```

### Gradle Kotlin DSL
```kotlin
repositories {
    maven {
        url = uri("https://repo.codeblocksmc.com/repository/maven-releases/")
    }
}

implementation("de.julianweinelt.plugins.motd:api:1.0.0")
```

### Gradle Groovy DSL
```groovy
repositories {
    maven {
        url "https://repo.codeblocksmc.com/repository/maven-releases/"
    }
}

implementation 'de.julianweinelt.plugins.motd:api:1.0.0'
```

### Features of the Editor API
As the DBX API covers both the editor and DataCat flow, some features regarding the UI
of the editor are only available in the editor API to split them up. Flow is a command-line-tool,
therefore it doesn't require anything with UI and Java Swing.\
Currently, it's covering these features:
- Creation of new settings pages
- Creation of new Editor Tab types
- Creation of new context menus
- Creation of MenuBar items
- Themes (not recommended)

## Building from source
First of all, you need a copy of the project on your system:
```shell
git clone https://github.com/JWeinelt/DataCat.git
cd DataCat
```

You can compile the editor from source by running
```bash
mvn clean package -pl ui -am
```