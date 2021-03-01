package de.wwestenb.metrics

import de.wwestenb.metrics.models.LineSpeed
import de.wwestenb.metrics.service.metricService
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class LineSpeedApiTests {
    companion object Properties {
        const val sixtyMinuteMillis = 60 * 60 * 1000
    }

    @BeforeTest
    internal fun clearAllMetrics() {
        metricService.clearAllMeasurements()
    }

    @Test
    internal fun testSubmitValidLineSpeed() {
        withTestApplication({ module() }) {
            val validLineSpeed = LineSpeed(1, 99.0, System.currentTimeMillis())

            val call = handleRequest(HttpMethod.Post, "/linespeed/") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(validLineSpeed))
            }

            with(call) {
                assertEquals(HttpStatusCode.Created, response.status())
            }
        }
    }

    @Test
    internal fun testSubmitOutdatedLineSpeed() {
        withTestApplication({ module() }) {
            val outdatedLineSpeed = LineSpeed(1, 99.0, System.currentTimeMillis() - sixtyMinuteMillis)

            val call = handleRequest(HttpMethod.Post, "/linespeed/") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(outdatedLineSpeed))
            }

            with(call) {
                assertEquals(HttpStatusCode.NoContent, response.status())
            }
        }
    }

    @Test
    internal fun testSubmitLineSpeedForUnknownLine() {
        withTestApplication({ module() }) {
            val invalidLineSpeed = LineSpeed(9999999, 99.0, System.currentTimeMillis())

            val call = handleRequest(HttpMethod.Post, "/linespeed/") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(invalidLineSpeed))
            }

            with(call) {
                assertEquals(HttpStatusCode.NotFound, response.status())
            }
        }
    }
}