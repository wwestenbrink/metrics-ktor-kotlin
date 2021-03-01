package de.wwestenb.metrics.service

import de.wwestenb.metrics.models.Metrics
import de.wwestenb.metrics.models.ProductionLine
import de.wwestenb.metrics.repository.measurementRepository

class NoDataException(line: ProductionLine) : Exception("No data available for production line ${line.line_id}")
class OutdatedTimestampException(timestamp: Long) : Exception("Outdated timestamp $timestamp")

private const val VALID_INTERVAL_MILLIS = 60 * 60 * 1000 // 60 minutes

class MetricsService {
    fun submitMeasurement(line: ProductionLine, timestamp: Long, lineSpeed: Double) {
        validateTimeStamp(timestamp)
        measurementRepository.addMeasurement(line, timestamp, lineSpeed)
    }

    fun getLineMetrics(line: ProductionLine): Metrics {
        val lineSpeeds =
            measurementRepository.listMeasurements(line, System.currentTimeMillis() - VALID_INTERVAL_MILLIS)
        if (lineSpeeds.isEmpty())
            throw NoDataException(line)

        return calculateMetrics(lineSpeeds)
    }

    fun clearOutDatedMeasurements(line: ProductionLine) {
        measurementRepository.clearOutdatedMeasurements(line, System.currentTimeMillis() - VALID_INTERVAL_MILLIS)
    }

    fun clearAllMeasurements() {
        measurementRepository.clearAll()
    }

    private fun validateTimeStamp(timestampMillis: Long) {
        if (timestampMillis < System.currentTimeMillis() - VALID_INTERVAL_MILLIS)
            throw OutdatedTimestampException(timestampMillis)
    }

    private fun calculateMetrics(lineSpeeds: Map<Long, Double>): Metrics {
        var min = Double.MAX_VALUE
        var max = 0.0
        var sum = 0.0
        var n = 0

        for (speed in lineSpeeds.values) {
            n++
            min = min.coerceAtMost(speed)
            max = max.coerceAtLeast(speed)
            sum += speed
        }

        return Metrics(sum / n, max, min)
    }
}

val metricService = MetricsService()