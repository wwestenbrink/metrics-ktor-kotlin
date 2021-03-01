package de.wwestenb.metrics

import de.wwestenb.metrics.models.Metrics
import de.wwestenb.metrics.models.ProductionLine
import de.wwestenb.metrics.service.NoDataException
import de.wwestenb.metrics.service.metricService
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MetricServiceTest {
    companion object Properties {
        val validLine = ProductionLine(1)
        const val sixtyMinuteMillis = 60 * 60 * 1000
    }

    @BeforeTest
    internal fun clearAllMetrics() {
        metricService.clearAllMeasurements()
    }

    @Test
    internal fun testSkipOutdatedMetrics() {
        metricService.submitMeasurement(validLine, System.currentTimeMillis() - sixtyMinuteMillis, 1.0)
        Thread.sleep(1)
        assertFailsWith<NoDataException> {
            (metricService.getLineMetrics(validLine))
        }
    }

    @Test
    internal fun testConcurrentAccess() {
        val threadPool = Executors.newFixedThreadPool(10)
        val iterations = 100000L
        val startTimeMillis = System.currentTimeMillis()

        for (counter in 1L..iterations) {
            threadPool.execute {
                try {
                    metricService.submitMeasurement(validLine, startTimeMillis - counter, 0.0 + counter)
                } catch (ex: Exception) {
                    println(ex)
                }
            }

            if (counter % (iterations / 10) == 0L) {
                threadPool.execute {
                    println(metricService.getLineMetrics(validLine))
                }
            }
        }

        threadPool.shutdown()
        threadPool.awaitTermination(10, TimeUnit.SECONDS)

        val expectedMetrics = Metrics(iterations / 2.0 + 0.5, 0.0 + iterations, 1.0)
        assertEquals(expectedMetrics, metricService.getLineMetrics(validLine))
    }
}