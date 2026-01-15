package dev.ruipereira.opennbs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NoteTest {
    @Test
    fun `test default values`() {
        val note = Note(instrument = 0)
        assertEquals(45, note.key)
        assertEquals(100, note.volume)
        assertEquals(100, note.panning)
        assertEquals(0, note.pitch)
    }

    @Test
    fun `test valid note`() {
        val note = Note(instrument = 0, key = 45, volume = 100, panning = 100, pitch = 0)
        assertEquals(0, note.instrument)
        assertEquals(45, note.key)
        assertEquals(100, note.volume)
        assertEquals(100, note.panning)
        assertEquals(0, note.pitch)
    }

    @Test
    fun `test invalid key`() {
        assertFailsWith<IllegalArgumentException> {
            Note(instrument = 0, key = -1)
        }
        assertFailsWith<IllegalArgumentException> {
            Note(instrument = 0, key = 88)
        }
    }

    @Test
    fun `test invalid volume`() {
        assertFailsWith<IllegalArgumentException> {
            Note(instrument = 0, volume = -1)
        }
        assertFailsWith<IllegalArgumentException> {
            Note(instrument = 0, volume = 101)
        }
    }

    @Test
    fun `test invalid panning`() {
        assertFailsWith<IllegalArgumentException> {
            Note(instrument = 0, panning = -1)
        }
        assertFailsWith<IllegalArgumentException> {
            Note(instrument = 0, panning = 201)
        }
    }

    @Test
    fun `test invalid pitch`() {
        assertFailsWith<IllegalArgumentException> {
            Note(instrument = 0, pitch = -1201)
        }
        assertFailsWith<IllegalArgumentException> {
            Note(instrument = 0, pitch = 1201)
        }
    }

    @Test
    @OptIn(TempoChangeAPI::class)
    fun `test tempo from pitch`() {
        assertEquals(4000, Note(instrument = 0, pitch = -600).tempoFromPitch())
        assertEquals(2000, Note(instrument = 0, pitch = -300).tempoFromPitch())
        assertEquals(0, Note(instrument = 0, pitch = 0).tempoFromPitch())
        assertEquals(2000, Note(instrument = 0, pitch = 300).tempoFromPitch())
        assertEquals(4000, Note(instrument = 0, pitch = 600).tempoFromPitch())
    }
}
