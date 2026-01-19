# OpenNBS

A Kotlin library for reading and writing Note Block Song (.nbs) files.

### Adding OpenNBS to your project

Using the [JitPack] Package Repository.

#### Gradle (Kotlin DSL)
```gradle
repositories {
    maven("https://jitpack.io") {
        name = "jitpack.io"
    }
}

dependencies {
    implementation("dev.ruipereira:opennbs:v1.1.0")
}
```

#### Apache Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.ruipereira</groupId>
        <artifactId>opennbs</artifactId>
        <version>v1.1.0</version>
    </dependency>
</dependencies>
```

### Usage

#### Decoding a file
```kotlin
val path = Path("song.nbs")
val song = OpenNBS.decodeFromFile(path)

println("Song: ${song.name} by ${song.author}")
```

#### Encoding a file
```kotlin
val outputPath = Path("output.nbs")
OpenNBS.encodeToFile(song, outputPath)
```

[JitPack]: https://jitpack.io/#dev.ruipereira/opennbs
