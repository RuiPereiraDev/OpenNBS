package dev.ruipereira.opennbs

/**
 * Represents a custom instrument in a Note Block Song.
 *
 * @property name The name of the instrument.
 * @property file The file name of the instrument sound.
 * @property key The key of the instrument (0-87).
 * @property pressPianoKey Whether the piano key is pressed when playing the instrument.
 */
public data class Instrument(
    val name: String,
    val file: String,
    val key: Int = 45,
    val pressPianoKey: Boolean = false,
) {
    init {
        require(key in 0..87) { "Key must be between 0 and 87 (standard piano range)" }
    }

    /**
     * Checks if this instrument is a tempo changer.
     *
     * This function is part of the experimental Tempo Change API.
     *
     * @return True if the instrument is a tempo changer, false otherwise.
     */
    @TempoChangeAPI
    public fun isTempoChanger(): Boolean {
        return name == TEMPO_CHANGER_INSTRUMENT_NAME
    }

    public companion object {
        /**
         * The name of the instrument used for tempo changes.
         *
         * This property is part of the experimental Tempo Change API.
         */
        @TempoChangeAPI
        public const val TEMPO_CHANGER_INSTRUMENT_NAME: String = "Tempo Changer"
    }
}
