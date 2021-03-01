package de.wwestenb.metrics.repository

import de.wwestenb.metrics.models.ProductionLine
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap

class MeasurementRepository {
    private val measurementsByLine: ConcurrentHashMap<ProductionLine, ConcurrentSkipListMap<Long, Double>> =
        ConcurrentHashMap()

    fun addMeasurement(line: ProductionLine, timestamp: Long, lineSpeed: Double) {
        val measurements = measurementsByLine.getOrPut(line, { ConcurrentSkipListMap() })
        measurements[timestamp] = lineSpeed
    }

    fun listMeasurements(line: ProductionLine, sinceTimestamp: Long): Map<Long, Double> {
        val measurements = measurementsByLine[line]
        return measurements?.tailMap(sinceTimestamp) ?: mapOf()
    }

    fun clearOutdatedMeasurements(line: ProductionLine, beforeTimestamp: Long) {
        val measurements = measurementsByLine[line] ?: return
        measurements.headMap(beforeTimestamp, true).clear()
    }

    fun clearAll() = measurementsByLine.clear()
}

val measurementRepository = MeasurementRepository()