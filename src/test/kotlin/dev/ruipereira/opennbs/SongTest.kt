package dev.ruipereira.opennbs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
}
