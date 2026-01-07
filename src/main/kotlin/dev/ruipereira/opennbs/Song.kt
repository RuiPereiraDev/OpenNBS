package dev.ruipereira.opennbs

/**
 * Represents a Note Block Song.
 *
 * @property name The name of the song.
 * @property version The NBS version.
 * @property author The author of the song.
 * @property originalAuthor The original author of the song.
 * @property description The description of the song.
 * @property length The length of the song in ticks.
 * @property tempo The tempo of the song (TPS * 100).
 * @property vanillaInstrumentCount The number of built-in instruments before custom instruments begin.
 * @property autoSaving Whether auto-saving is enabled.
 * @property autoSavingDuration The auto-saving duration in minutes.
 * @property timeSignature The time signature of the song.
 * @property minutesSpent The number of minutes spent working on the song.
 * @property leftClicks The number of left clicks.
 * @property rightClicks The number of right clicks.
 * @property noteBlocksAdded The number of note blocks added.
 * @property noteBlocksRemoved The number of note blocks removed.
 * @property sourceFile The source file name.
 * @property looping Whether the song should loop.
 * @property maxLoopCount The maximum number of loops.
 * @property loopStartTick The tick where the loop starts.
 * @property layers The layers of the song.
 * @property customInstruments The custom instruments of the song.
 */
public data class Song(
    val name: String,
    val version: Version = Version.V5,
    val author: String = "",
    val originalAuthor: String = "",
    val description: String = "",
    val length: Int = 0,
    val tempo: Int = 1000,
    val vanillaInstrumentCount: Int = 16,
    val autoSaving: Boolean = false,
    val autoSavingDuration: Int = 10,
    val timeSignature: Int = 4,
    val minutesSpent: Int = 0,
    val leftClicks: Int = 0,
    val rightClicks: Int = 0,
    val noteBlocksAdded: Int = 0,
    val noteBlocksRemoved: Int = 0,
    val sourceFile: String = "",
    val looping: Boolean = false,
    val maxLoopCount: Int = 0,
    val loopStartTick: Int = 0,
    val layers: Map<Int, Layer> = emptyMap(),
    val customInstruments: List<Instrument> = emptyList()
) {
    /**
     * The number of layers in the song.
     */
    val layerCount: Int get() = layers.size

    /**
     * The number of custom instruments in the song.
     */
    val customInstrumentCount: Int get() = customInstruments.size
}
