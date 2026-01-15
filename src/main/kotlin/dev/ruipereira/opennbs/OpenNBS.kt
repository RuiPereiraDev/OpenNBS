package dev.ruipereira.opennbs

import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path
import java.util.*
import kotlin.io.path.inputStream
import kotlin.io.path.isRegularFile
import kotlin.io.path.outputStream

/**
 * Utility methods to decode and encode Note Block Song (.nbs) files.
 *
 * This implementation supports versions from Classic up to Version 5,
 * handling the structural changes in headers, note data, and layer properties
 * introduced across the format's history.
 */
public object OpenNBS {
    /** The maximum allowed length for strings to prevent memory exhaustion during parsing. */
    private const val MAX_STRING_LENGTH = 16384

    /**
     * Decodes an NBS file from the given [Path].
     *
     * @param path The path to the .nbs file.
     * @return A [Song] object containing the parsed data.
     * @throws IllegalArgumentException if the path is not a regular file.
     */
    public fun decodeFromFile(path: Path): Song {
        require(path.isRegularFile()) { "the specified path must be a file" }
        path.inputStream().buffered().use { inputStream -> return decode(inputStream) }
    }

    /**
     * Decodes an NBS file from an [InputStream].
     *
     * This method handles the logic of detecting whether a file is in the "Classic"
     * format (where the first two bytes are the song length) or a modern versioned
     * format (where the first two bytes are zero).
     *
     * @param inputStream The stream to read from.
     * @return A [Song] object containing the parsed data.
     */
    public fun decode(inputStream: InputStream): Song {
        val firstTwoBytes = inputStream.readShort()

        val version = if (firstTwoBytes == 0) {
            val version = Version.fromInt(inputStream.readByte())
            require(version != null) { "Invalid or unsupported OpenNBS version" }
            version
        } else Version.CLASSIC

        val vanillaInstrumentCount = if (version >= Version.V1) inputStream.readByte() else 16
        var length = if (version >= Version.V3) inputStream.readShort() else firstTwoBytes

        val layerCount = inputStream.readShort()

        // Read Standard Header Information
        val name = inputStream.readString()
        val author = inputStream.readString()
        val originalAuthor = inputStream.readString()
        val description = inputStream.readString()
        val tempo = inputStream.readShort()
        val autoSaving = inputStream.readBoolean()
        val autoSavingDuration = inputStream.readByte()
        val timeSignature = inputStream.readByte()
        val minutesSpent = inputStream.readInt()
        val leftClicks = inputStream.readInt()
        val rightClicks = inputStream.readInt()
        val noteBlocksAdded = inputStream.readInt()
        val noteBlocksRemoved = inputStream.readInt()
        val sourceFile = inputStream.readString()

        // Read Loop Settings (Introduced in V4)
        val looping = if (version >= Version.V4) inputStream.readBoolean() else false
        val maxLoopCount = if (version >= Version.V4) inputStream.readByte() else 0
        val loopStartTick = if (version >= Version.V4) inputStream.readShort() else 0

        // Read Notes using the jump system
        val layerNotesMap = mutableMapOf<Int, MutableMap<Int, Note>>()

        var tick = -1
        while (true) {
            val jumpsToNextTick = inputStream.readShort()
            if (jumpsToNextTick == 0) break // End of notes
            tick += jumpsToNextTick

            var layer = -1
            while (true) {
                val jumpsToNextLayer = inputStream.readShort()
                if (jumpsToNextLayer == 0) break // End of notes in this tick
                layer += jumpsToNextLayer

                val note = if (version >= Version.V4) Note(
                    instrument = inputStream.readByte(),
                    key = inputStream.readByte(),
                    volume = inputStream.readByte(),
                    panning = inputStream.readByte(),
                    pitch = inputStream.readShort()
                ) else Note(
                    instrument = inputStream.readByte(),
                    key = inputStream.readByte()
                )
                layerNotesMap.getOrPut(layer) { TreeMap(compareBy { it }) }[tick] = note
            }
        }

        // V1 and V2 files length wasn't explicitly defined in the header
        if (version > Version.CLASSIC && version < Version.V3) {
            length = tick
        }

        // Write Layers
        val layers = TreeMap<Int, Layer>(compareBy { it })
        repeat(layerCount) { layer ->
            layers[layer] = Layer(
                name = inputStream.readString(),
                isLocked = if (version >= Version.V4) inputStream.readBoolean() else false,
                volume = inputStream.readByte(),
                panning = if (version >= Version.V2) inputStream.readByte() else 100,
                notes = layerNotesMap[layer] ?: emptyMap()
            )
        }

        // Fallback: If layers aren't defined in the file header but notes exist
        if (layerCount == 0 && layerNotesMap.isNotEmpty()) {
            layerNotesMap.forEach { (layer, notes) ->
                layers[layer] = Layer(name = "", notes = notes)
            }
        }

        // Read Custom Instruments
        val customInstruments = mutableListOf<Instrument>()
        if (inputStream.available() > 0) {
            repeat(inputStream.readByte()) { _ ->
                customInstruments.add(
                    Instrument(
                        name = inputStream.readString(),
                        file = inputStream.readString(),
                        key = inputStream.readByte(),
                        pressPianoKey = inputStream.readBoolean()
                    )
                )
            }
        }

        return Song(
            name, version, author, originalAuthor, description, length, tempo,
            vanillaInstrumentCount, autoSaving, autoSavingDuration, timeSignature,
            minutesSpent, leftClicks, rightClicks, noteBlocksAdded, noteBlocksRemoved,
            sourceFile, looping, maxLoopCount, loopStartTick, layers, customInstruments
        )
    }

    /**
     * Encodes a [Song] object and saves it to a file [Path].
     *
     * @param song A [Song] object.
     * @param path The path to the .nbs file.
     * @throws IllegalArgumentException if the path is not a regular file.
     */
    public fun encodeToFile(song: Song, path: Path) {
        require(path.isRegularFile()) { "the specified path must be a file" }
        path.outputStream().buffered().use { outputStream -> encode(song, outputStream) }
    }

    /**
     * Encodes a [Song] into an [OutputStream] using the specified NBS version.
     *
     * If the specified version is lower than the song's native version, advanced
     * data (like panning or pitch) may be truncated to fit the older format.
     *
     * @param song The song to encode.
     * @param outputStream The stream to write to.
     * @param version The target NBS version (defaults to the song's current version).
     */
    public fun encode(song: Song, outputStream: OutputStream, version: Version = song.version) {
        if (version >= Version.V1) {
            outputStream.writeShort(0)
            outputStream.writeByte(version.asInt)
            outputStream.writeByte(song.vanillaInstrumentCount)
            if (version >= Version.V3) outputStream.writeShort(song.length)
        } else outputStream.writeShort(song.length)

        // Write Standard Header Information
        outputStream.writeShort(song.layerCount)
        outputStream.writeString(song.name)
        outputStream.writeString(song.author)
        outputStream.writeString(song.originalAuthor)
        outputStream.writeString(song.description)
        outputStream.writeShort(song.tempo)
        outputStream.writeBoolean(song.autoSaving)
        outputStream.writeByte(song.autoSavingDuration)
        outputStream.writeByte(song.timeSignature)
        outputStream.writeInt(song.minutesSpent)
        outputStream.writeInt(song.leftClicks)
        outputStream.writeInt(song.rightClicks)
        outputStream.writeInt(song.noteBlocksAdded)
        outputStream.writeInt(song.noteBlocksRemoved)
        outputStream.writeString(song.sourceFile)

        // Write Loop Settings (Introduced in V4)
        if (version >= Version.V4) {
            outputStream.writeBoolean(song.looping)
            outputStream.writeByte(song.maxLoopCount)
            outputStream.writeShort(song.loopStartTick)
        }

        // Write Notes using the jump system
        val sortedLayers = song.layers.toSortedMap()

        var jumpsToNextTick = 0
        repeat(song.length + 1) { i ->
            jumpsToNextTick++

            if (sortedLayers.values.any { layer -> layer.notes[i] != null }) {
                outputStream.writeShort(jumpsToNextTick)
                jumpsToNextTick = 0

                var lastLayerId = -1
                for ((layerId, layer) in sortedLayers) {
                    val note = layer.notes[i]
                    if (note != null) {
                        outputStream.writeShort(layerId - lastLayerId)
                        lastLayerId = layerId

                        outputStream.writeByte(note.instrument)
                        outputStream.writeByte(note.key)
                        if (version >= Version.V4) {
                            outputStream.writeByte(note.volume)
                            outputStream.writeByte(note.panning)
                            outputStream.writeShort(note.pitch)
                        }
                    }
                }

                outputStream.writeShort(0) // Tick-layer jump terminator
            }
        }
        outputStream.writeShort(0) // Total note data terminator

        // Write Layers
        for (layer in sortedLayers.values) {
            outputStream.writeString(layer.name)
            if (version >= Version.V4) outputStream.writeBoolean(layer.isLocked)
            outputStream.writeByte(layer.volume)
            if (version >= Version.V2) outputStream.writeByte(layer.panning)
        }

        // Write Custom Instruments
        if (song.customInstrumentCount > 0) {
            outputStream.writeByte(song.customInstrumentCount)
            for (instrument in song.customInstruments) {
                outputStream.writeString(instrument.name)
                outputStream.writeString(instrument.file)
                outputStream.writeByte(instrument.key)
                outputStream.writeBoolean(instrument.pressPianoKey)
            }
        }
    }

    private fun InputStream.readByte(): Int {
        return read() and 0xFF
    }

    private fun OutputStream.writeByte(value: Int) {
        write(value)
    }

    private fun InputStream.readShort(): Int {
        val b1 = read() and 0xFF
        val b2 = read() and 0xFF
        return b1 or (b2 shl 8)
    }

    private fun OutputStream.writeShort(value: Int) {
        write(value and 0xFF)
        write(value shr 8 and 0xFF)
    }

    private fun InputStream.readInt(): Int {
        val b1 = read() and 0xFF
        val b2 = read() and 0xFF
        val b3 = read() and 0xFF
        val b4 = read() and 0xFF
        return (b4 shl 24) or (b3 shl 16) or (b2 shl 8) or b1
    }

    private fun OutputStream.writeInt(value: Int) {
        write(value and 0xFF)
        write(value shr 8 and 0xFF)
        write(value shr 16 and 0xFF)
        write(value shr 24 and 0xFF)
    }

    private fun InputStream.readBoolean(): Boolean {
        return readByte() != 0
    }

    private fun OutputStream.writeBoolean(value: Boolean) {
        writeByte(if (value) 1 else 0)
    }

    private fun InputStream.readString(): String {
        val length = readInt()
        if (length <= 0) return ""
        if (length > MAX_STRING_LENGTH) throw IllegalArgumentException("String too long: $length")
        val bytes = ByteArray(length)
        var n = 0
        while (n < length) {
            val count = read(bytes, n, length - n)
            if (count < 0) break
            n += count
        }
        return String(bytes, Charsets.UTF_8)
    }

    private fun OutputStream.writeString(value: String) {
        val bytes = value.toByteArray(Charsets.UTF_8)
        writeInt(bytes.size)
        write(bytes)
    }
}
