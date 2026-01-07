package dev.ruipereira.opennbs

/**
 * Represents the version of the OpenNBS file format.
 *
 * @property asInt The integer representation of the version.
 */
public enum class Version(
    public val asInt: Int
) {
    CLASSIC(0), V1(1), V2(2), V3(3), V4(4), V5(5);

    public companion object {
        private val map = entries.associateBy(Version::asInt)

        /**
         * Gets the [Version] from an integer.
         *
         * @param value The integer value.
         * @return The [Version] corresponding to the integer, or null if not found.
         */
        public fun fromInt(value: Int): Version? = map[value]
    }
}
