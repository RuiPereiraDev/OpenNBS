package dev.ruipereira.opennbs

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class OpenNBSTest {
    val testSong = Song(
        name = "Test Song",
        author = "Test Author",
        originalAuthor = "Test Original Author",
        description = "Test Description",
        length = 10,
        tempo = 500,
        vanillaInstrumentCount = 10,
        autoSaving = true,
        autoSavingDuration = 5,
        timeSignature = 3,
        minutesSpent = 100,
        leftClicks = 200,
        rightClicks = 300,
        noteBlocksAdded = 50,
        noteBlocksRemoved = 10,
        sourceFile = "test.mid",
        looping = true,
        maxLoopCount = 10,
        loopStartTick = 1,
        layers = mapOf(
            0 to Layer(
                name = "Layer 1", isLocked = true, volume = 50, panning = 150, notes = mapOf(
                    0 to Note(instrument = 1, key = 46, volume = 90, panning = 200, pitch = 600)
                )
            ),
            1 to Layer(
                name = "Layer 2", notes = mapOf(
                    10 to Note(instrument = 10)
                )
            )
        ),
        customInstruments = listOf(
            Instrument(name = "Test Inst", file = "test.ogg", key = 45, pressPianoKey = true)
        )
    )

    @Test
    fun `test encode and decode V5`() {
        val originalSong = testSong.copy(version = Version.V5)

        val decodedSong = encodeAndDecode(originalSong)
        assertEquals(originalSong, decodedSong)
    }

    @Test
    fun `test encode and decode V4`() {
        val originalSong = testSong.copy(version = Version.V4)

        val decodedSong = encodeAndDecode(originalSong)
        assertEquals(originalSong, decodedSong)
    }

    @Test
    fun `test encode and decode V3`() {
        val originalSong = testSong.copy(version = Version.V3)

        val decodedSong = encodeAndDecode(originalSong)
        assertEquals(originalSong.name, decodedSong.name)
        assertEquals(originalSong.version, decodedSong.version)
        assertEquals(originalSong.length, decodedSong.length)
        assertEquals(originalSong.customInstruments, decodedSong.customInstruments)

        // V3 does not support song looping, maxLoopCount, loopStartTick
        assertEquals(originalSong.vanillaInstrumentCount, decodedSong.vanillaInstrumentCount)
        assertEquals(false, decodedSong.looping)
        assertEquals(0, decodedSong.maxLoopCount)
        assertEquals(0, decodedSong.loopStartTick)

        // V3 does not support layer isLocked
        assertEquals(originalSong.layers[0]?.name, decodedSong.layers[0]?.name)
        assertEquals(false, decodedSong.layers[0]?.isLocked)
        assertEquals(originalSong.layers[0]?.panning, decodedSong.layers[0]?.panning)

        // V3 does not support note volume, panning, pitch
        assertEquals(originalSong.layers[0]?.notes[0]?.instrument, decodedSong.layers[0]?.notes[0]?.instrument)
        assertEquals(100, decodedSong.layers[0]?.notes[0]?.volume)
        assertEquals(100, decodedSong.layers[0]?.notes[0]?.panning)
        assertEquals(0, decodedSong.layers[0]?.notes[0]?.pitch)
    }

    @Test
    fun `test encode and decode V2`() {
        val originalSong = testSong.copy(version = Version.V2)

        val decodedSong = encodeAndDecode(originalSong)
        assertEquals(originalSong.name, decodedSong.name)
        assertEquals(originalSong.version, decodedSong.version)
        assertEquals(originalSong.length, decodedSong.length)
        assertEquals(originalSong.customInstruments, decodedSong.customInstruments)

        // V2 does not support song looping, maxLoopCount, loopStartTick
        assertEquals(originalSong.vanillaInstrumentCount, decodedSong.vanillaInstrumentCount)
        assertEquals(false, decodedSong.looping)
        assertEquals(0, decodedSong.maxLoopCount)
        assertEquals(0, decodedSong.loopStartTick)

        // V2 does not support layer isLocked
        assertEquals(originalSong.layers[0]?.name, decodedSong.layers[0]?.name)
        assertEquals(false, decodedSong.layers[0]?.isLocked)
        assertEquals(originalSong.layers[0]?.panning, decodedSong.layers[0]?.panning)

        // V2 does not support note volume, panning, pitch
        assertEquals(originalSong.layers[0]?.notes[0]?.instrument, decodedSong.layers[0]?.notes[0]?.instrument)
        assertEquals(100, decodedSong.layers[0]?.notes[0]?.volume)
        assertEquals(100, decodedSong.layers[0]?.notes[0]?.panning)
        assertEquals(0, decodedSong.layers[0]?.notes[0]?.pitch)
    }

    @Test
    fun `test encode and decode V1`() {
        val originalSong = testSong.copy(version = Version.V1)

        val decodedSong = encodeAndDecode(originalSong)
        assertEquals(originalSong.name, decodedSong.name)
        assertEquals(originalSong.version, decodedSong.version)
        assertEquals(originalSong.length, decodedSong.length)
        assertEquals(originalSong.customInstruments, decodedSong.customInstruments)

        // V1 does not support song looping, maxLoopCount, loopStartTick
        assertEquals(originalSong.vanillaInstrumentCount, decodedSong.vanillaInstrumentCount)
        assertEquals(false, decodedSong.looping)
        assertEquals(0, decodedSong.maxLoopCount)
        assertEquals(0, decodedSong.loopStartTick)

        // V1 does not support layer isLocked, panning
        assertEquals(originalSong.layers[0]?.name, decodedSong.layers[0]?.name)
        assertEquals(false, decodedSong.layers[0]?.isLocked)
        assertEquals(100, decodedSong.layers[0]?.panning)

        // V1 does not support note volume, panning, pitch
        assertEquals(originalSong.layers[0]?.notes[0]?.instrument, decodedSong.layers[0]?.notes[0]?.instrument)
        assertEquals(100, decodedSong.layers[0]?.notes[0]?.volume)
        assertEquals(100, decodedSong.layers[0]?.notes[0]?.panning)
        assertEquals(0, decodedSong.layers[0]?.notes[0]?.pitch)
    }

    @Test
    fun `test encode and decode classic`() {
        val originalSong = testSong.copy(version = Version.CLASSIC)

        val decodedSong = encodeAndDecode(originalSong)
        assertEquals(originalSong.name, decodedSong.name)
        assertEquals(originalSong.version, decodedSong.version)
        assertEquals(originalSong.length, decodedSong.length)
        assertEquals(originalSong.customInstruments, decodedSong.customInstruments)

        // Classic does not support song vanillaInstrumentCount, looping, maxLoopCount, loopStartTick
        assertEquals(16, decodedSong.vanillaInstrumentCount)
        assertEquals(false, decodedSong.looping)
        assertEquals(0, decodedSong.maxLoopCount)
        assertEquals(0, decodedSong.loopStartTick)

        // Classic does not support layer isLocked, panning
        assertEquals(originalSong.layers[0]?.name, decodedSong.layers[0]?.name)
        assertEquals(false, decodedSong.layers[0]?.isLocked)
        assertEquals(100, decodedSong.layers[0]?.panning)

        // Classic does not support note volume, panning, pitch
        assertEquals(originalSong.layers[0]?.notes[0]?.instrument, decodedSong.layers[0]?.notes[0]?.instrument)
        assertEquals(100, decodedSong.layers[0]?.notes[0]?.volume)
        assertEquals(100, decodedSong.layers[0]?.notes[0]?.panning)
        assertEquals(0, decodedSong.layers[0]?.notes[0]?.pitch)
    }

    @Test
    fun `test file operations`() {
        val tempFile = Files.createTempFile("test_song", ".nbs")
        try {
            val originalSong = Song(name = "File Test")
            OpenNBS.encodeToFile(originalSong, tempFile)
            val decodedSong = OpenNBS.decodeFromFile(tempFile)
            assertEquals(originalSong, decodedSong)
        } finally {
            Files.deleteIfExists(tempFile)
        }
    }

    @Test
    fun `test directory operations`() {
        val tempDir = Files.createTempDirectory("test_song")
        assertFailsWith<IllegalArgumentException> {
            val song = Song(name = "Dir Test")
            OpenNBS.encodeToFile(song, tempDir)
        }
        assertFailsWith<IllegalArgumentException> {
            OpenNBS.decodeFromFile(tempDir)
        }
        Files.deleteIfExists(tempDir)
    }

    @Test
    fun `test v5 song file`() {
        val song = decodeAndEncodeResource("song_v5.nbs")
        assertEquals(Version.V5, song.version)
        assertEquals("Nyan Cat", song.name)
    }

    @Test
    fun `test v4 song file`() {
        val song = decodeAndEncodeResource("song_v4.nbs")
        assertEquals(Version.V4, song.version)
        assertEquals("Nyan Cat", song.name)
    }

    @Test
    fun `test v3 song file`() {
        val song = decodeAndEncodeResource("song_v3.nbs")
        assertEquals(Version.V3, song.version)
        assertEquals("Nyan Cat", song.name)
    }

    @Test
    fun `test v2 song file`() {
        val song = decodeAndEncodeResource("song_v2.nbs")
        assertEquals(Version.V2, song.version)
        assertEquals("Nyan Cat", song.name)
    }

    @Test
    fun `test v1 song file`() {
        val song = decodeAndEncodeResource("song_v1.nbs")
        assertEquals(Version.V1, song.version)
        assertEquals("Nyan Cat", song.name)
    }

    @Test
    fun `test classic song file`() {
        val song = decodeAndEncodeResource("song_v0.nbs")
        assertEquals(Version.CLASSIC, song.version)
        assertEquals("Nyan Cat", song.name)
    }

    private fun encodeAndDecode(song: Song): Song {
        val outputStream = ByteArrayOutputStream()
        OpenNBS.encode(song, outputStream)
        val bytes = outputStream.toByteArray()

        val inputStream = ByteArrayInputStream(bytes)
        return OpenNBS.decode(inputStream)
    }

    private fun decodeAndEncodeResource(name: String): Song {
        val resource = this::class.java.classLoader.getResourceAsStream(name)
        assertNotNull(resource)

        val originalSong = OpenNBS.decode(resource)

        val outputStream = ByteArrayOutputStream()
        OpenNBS.encode(originalSong, outputStream)
        val bytes = outputStream.toByteArray()

        val inputStream = ByteArrayInputStream(bytes)
        val decodedSong = OpenNBS.decode(inputStream)

        assertEquals(originalSong, decodedSong)

        return decodedSong
    }
}
