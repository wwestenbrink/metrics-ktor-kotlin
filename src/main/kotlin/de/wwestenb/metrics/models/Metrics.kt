package de.wwestenb.metrics.models

import kotlinx.serialization.Serializable

@Serializable
data class Metrics(val avg: Double, val max: Double, val min: Double)