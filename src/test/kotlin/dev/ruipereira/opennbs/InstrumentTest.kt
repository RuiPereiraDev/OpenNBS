package dev.ruipereira.opennbs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InstrumentTest {
    @Test
    fun `test default values`() {
        val instrument = Instrument(name = "Default", file = "default.ogg")
        assertEquals(45, instrument.key)
        assertFalse(instrument.pressPianoKey)
    }

    @Test
    fun `test valid instrument`() {
        val instrument = Instrument(name = "Valid", file = "valid.ogg", key = 45, pressPianoKey = false)
        assertEquals("Valid", instrument.name)
        assertEquals("valid.ogg", instrument.file)
        assertEquals(45, instrument.key)
        assertEquals(false, instrument.pressPianoKey)
    }

    @Test
    fun `test invalid key`() {
        assertFailsWith<IllegalArgumentException> {
            Instrument(name = "Invalid Key", file = "invalid.ogg", key = -1)
        }
        assertFailsWith<IllegalArgumentException> {
            Instrument(name = "Invalid Key", file = "invalid.ogg", key = 88)
        }
    }

    @Test
    @OptIn(TempoChangeAPI::class)
    fun `test is tempo changer`() {
        assertFalse(Instrument(name = "Not Tempo Changer", file = "").isTempoChanger())
        assertTrue(Instrument(name = "Tempo Changer", file = "").isTempoChanger())
    }
}
