package de.wwestenb.metrics.routes

import de.wwestenb.metrics.models.LineSpeed
import de.wwestenb.metrics.service.OutdatedTimestampException
import de.wwestenb.metrics.service.UnknownProductionLineException
import de.wwestenb.metrics.service.metricService
import de.wwestenb.metrics.service.productionLineService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.lineSpeedRouting() {
    route("/linespeed") {
        post {
            val measurement = call.receive<LineSpeed>()

            try {
                val line = productionLineService.lookupLineById(measurement.line_id)
                metricService.submitMeasurement(line, measurement.timestamp, measurement.speed)
                metricService.clearOutDatedMeasurements(line)
                call.respondText("Line speed stored", status = HttpStatusCode.Created)
            } catch (unknownProductionLineException: UnknownProductionLineException) {
                call.respondText(unknownProductionLineException.message ?: "", status = HttpStatusCode.NotFound)
            } catch (outdatedTimestampException: OutdatedTimestampException) {
                call.respondText(outdatedTimestampException.message ?: "", status = HttpStatusCode.NoContent)
            }
        }
    }
}

fun Application.registerLineSpeedRoutes() {
    routing {
        lineSpeedRouting()
    }
}