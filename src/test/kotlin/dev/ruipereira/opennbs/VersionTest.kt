package dev.ruipereira.opennbs

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VersionTest {
    @Test
    fun `test version to int`() {
        assertEquals(5, Version.V5.asInt)
        assertEquals(4, Version.V4.asInt)
        assertEquals(3, Version.V3.asInt)
        assertEquals(2, Version.V2.asInt)
        assertEquals(1, Version.V1.asInt)
        assertEquals(0, Version.CLASSIC.asInt)
    }

    @Test
    fun `test version from int`() {
        assertEquals(Version.V5, Version.fromInt(5))
        assertEquals(Version.V4, Version.fromInt(4))
        assertEquals(Version.V3, Version.fromInt(3))
        assertEquals(Version.V2, Version.fromInt(2))
        assertEquals(Version.V1, Version.fromInt(1))
        assertEquals(Version.CLASSIC, Version.fromInt(0))

        assertNull(Version.fromInt(999))
    }
}
