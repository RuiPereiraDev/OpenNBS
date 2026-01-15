package dev.ruipereira.opennbs

/**
 * Represents a note in a Note Block Song.
 *
 * @property instrument The instrument ID.
 * @property key The key of the note (0-87).
 * @property volume The volume of the note (0-100).
 * @property panning The panning of the note (0-200, 100 is center).
 * @property pitch The pitch of the note (-1200-1200).
 */
public data class Note(
    val instrument: Int,
    val key: Int = 45,
    val volume: Int = 100,
    val panning: Int = 100,
    val pitch: Int = 0
) {
    init {
        require(key in 0..87) { "Key must be between 0 and 87 (standard piano range)" }
        require(volume in 0..100) { "Volume must be between 0 and 100" }
        require(panning in 0..200) { "Panning must be between 0 and 200" }
        require(pitch in -1200..1200) { "Pitch must be between -1200 and 1200 cents" }
    }

    /**
     * Calculates the tempo from the note's pitch.
     *
     * This function is part of the experimental Tempo Change API.
     *
     * @return The calculated tempo.
     */
    @TempoChangeAPI
    public fun tempoFromPitch(): Int {
        return kotlin.math.abs(pitch) * 100 / 15
    }
}
