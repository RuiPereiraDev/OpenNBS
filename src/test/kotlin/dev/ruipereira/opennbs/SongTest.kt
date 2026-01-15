package dev.ruipereira.opennbs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SongTest {
    @Test
    fun `test default values`() {
        val song = Song(name = "Default")
        assertEquals(Version.V5, song.version)
        assertEquals("", song.author)
        assertEquals("", song.originalAuthor)
        assertEquals("", song.description)
        assertEquals(0, song.length)
        assertEquals(16, song.vanillaInstrumentCount)
        assertFalse(song.autoSaving)
        assertEquals(10, song.autoSavingDuration)
        assertEquals(4, song.timeSignature)
        assertEquals(0, song.minutesSpent)
        assertEquals(0, song.leftClicks)
        assertEquals(0, song.rightClicks)
        assertEquals(0, song.noteBlocksAdded)
        assertEquals(0, song.noteBlocksRemoved)
        assertEquals("", song.sourceFile)
        assertFalse(song.looping)
        assertEquals(0, song.maxLoopCount)
        assertEquals(0, song.loopStartTick)
        assertTrue(song.layers.isEmpty())
        assertTrue(song.customInstruments.isEmpty())
    }

    @Test
    @OptIn(TempoChangeAPI::class)
    fun `test song tempo changes`() {
        val resource = this::class.java.classLoader.getResourceAsStream("tempo.nbs")
        val song = OpenNBS.decode(resource!!)

        assertTrue(song.hasTempoChanger)
        assertEquals(1000, song.getTempoAtTick(-1))
        assertEquals(2000, song.getTempoAtTick(0))
        assertEquals(4000, song.getTempoAtTick(20))
        assertEquals(666, song.getTempoAtTick(80))
        assertEquals(666, song.getTempoAtTick(100))
    }

    @Test
    @OptIn(TempoChangeAPI::class)
    fun `test song no tempo change inst`() {
        val resource = this::class.java.classLoader.getResourceAsStream("tempo.nbs")

        assertNotNull(resource)
        val song = OpenNBS.decode(resource).copy(customInstruments = emptyList())

        assertFalse(song.hasTempoChanger)
        assertEquals(1000, song.getTempoAtTick(0))
    }
}
