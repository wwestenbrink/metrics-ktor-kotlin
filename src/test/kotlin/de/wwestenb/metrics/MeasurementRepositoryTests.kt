package de.wwestenb.metrics

import de.wwestenb.metrics.models.ProductionLine
import de.wwestenb.metrics.repository.measurementRepository
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

class MeasurementRepositoryTests {
    companion object Properties {
        val validLine = ProductionLine(1)
        const val sixtyMinuteMillis = 60 * 60 * 1000
    }

    @BeforeTest
    internal fun clearAllMetrics() {
        measurementRepository.clearAll()
    }

    @Test
    internal fun testClearOutdatedMetrics() {
        val currentTimeMillis = System.currentTimeMillis()

        measurementRepository.addMeasurement(validLine, currentTimeMillis - sixtyMinuteMillis, 1.0)
        measurementRepository.clearOutdatedMeasurements(validLine, currentTimeMillis - sixtyMinuteMillis)

        assertTrue(
            measurementRepository.listMeasurements(validLine, currentTimeMillis - sixtyMinuteMillis).isEmpty()
        )
    }
}