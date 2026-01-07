package dev.ruipereira.opennbs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LayerTest {
    @Test
    fun `test default values`() {
        val layer = Layer(name = "Default")
        assertFalse(layer.isLocked)
        assertEquals(100, layer.volume)
        assertEquals(100, layer.panning)
        assertTrue(layer.notes.isEmpty())
    }

    @Test
    fun `test valid layer`() {
        val layer = Layer(name = "Valid", volume = 100, panning = 100, isLocked = false, notes = emptyMap())
        assertEquals("Valid", layer.name)
        assertEquals(100, layer.volume)
        assertEquals(100, layer.panning)
        assertEquals(false, layer.isLocked)
        assertTrue(layer.notes.isEmpty())
    }

    @Test
    fun `test invalid volume`() {
        assertFailsWith<IllegalArgumentException> {
            Layer(name = "", volume = -1)
        }
        assertFailsWith<IllegalArgumentException> {
            Layer(name = "", volume = 101)
        }
    }

    @Test
    fun `test invalid panning`() {
        assertFailsWith<IllegalArgumentException> {
            Layer(name = "", panning = -1)
        }
        assertFailsWith<IllegalArgumentException> {
            Layer(name = "", panning = 201)
        }
    }
}
