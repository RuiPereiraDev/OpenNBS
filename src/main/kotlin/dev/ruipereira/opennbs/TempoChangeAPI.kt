package dev.ruipereira.opennbs

/**
 * Marks declarations that are experimental and related to Tempo Changes.
 *
 * This API is subject to change in future versions.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This API is experimental. It could change in the future without notice."
)
public annotation class TempoChangeAPI
