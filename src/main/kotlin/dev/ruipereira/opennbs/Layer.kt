package dev.ruipereira.opennbs

/**
 * Represents a layer in a Note Block Song.
 *
 * @property name The name of the layer.
 * @property isLocked Whether the layer is locked.
 * @property volume The volume of the layer (0-100).
 * @property panning The panning of the layer (0-200, 100 is center).
 * @property notes The notes in the layer, mapped by tick.
 */
public data class Layer(
    val name: String,
    val isLocked: Boolean = false,
    val volume: Int = 100,
    val panning: Int = 100,
    val notes: Map<Int, Note> = emptyMap()
) {
    init {
        require(volume in 0..100) { "Volume must be between 0 and 100" }
        require(panning in 0..200) { "Panning must be between 0 and 200" }
    }
}
