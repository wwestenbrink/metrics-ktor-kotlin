package de.wwestenb.metrics

import de.wwestenb.metrics.models.Metrics
import de.wwestenb.metrics.models.ProductionLine
import de.wwestenb.metrics.service.metricService
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals


class MetricApiTests {
    companion object Properties {
        const val validLineId = 1L
        const val invalidLineId = -1
    }

    @BeforeTest
    internal fun clearAllMetrics() {
        metricService.clearAllMeasurements()
    }

    @Test
    internal fun testRetrieveValidLineMetrics() {
        withTestApplication({ module() }) {
            val validLine = ProductionLine(validLineId)
            metricService.submitMeasurement(validLine, System.currentTimeMillis() - 1, 1.0)
            metricService.submitMeasurement(validLine, System.currentTimeMillis(), 2.0)
            val expectedMetric = Metrics(1.5, 2.0, 1.0)

            handleRequest(HttpMethod.Get, "/metrics/$validLineId").apply {
                assertEquals(expectedMetric, Json.decodeFromString(response.content!!))
                assertEquals(HttpStatusCode.OK, response.status())
            }

        }
    }

    @Test
    internal fun testRetrieveEmptyLineMetrics() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/metrics/$validLineId").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }

    @Test
    internal fun testRetrieveInvalidLineMetrics() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/metrics/$invalidLineId").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }
}