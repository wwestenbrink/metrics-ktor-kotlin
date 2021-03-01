package de.wwestenb.metrics.models

import kotlinx.serialization.Serializable

@Serializable
data class LineSpeed(val line_id: Long, val speed: Double, val timestamp: Long)