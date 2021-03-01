package de.wwestenb.metrics.routes

import de.wwestenb.metrics.service.NoDataException
import de.wwestenb.metrics.service.UnknownProductionLineException
import de.wwestenb.metrics.service.metricService
import de.wwestenb.metrics.service.productionLineService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.metricRouting() {
    route("/metrics") {
        get("{id}") {
            val lineId = call.parameters["id"]?.toLong() ?: 0

            try {
                val line = productionLineService.lookupLineById(lineId)
                val metrics = metricService.getLineMetrics(line)
                call.respond(metrics)
            } catch (unknownProductionLineException: UnknownProductionLineException) {
                call.respondText(unknownProductionLineException.message ?: "", status = HttpStatusCode.NotFound)
            } catch (noDataException: NoDataException) {
                call.respondText(noDataException.message ?: "", status = HttpStatusCode.NotFound)
            }
        }
    }
}


fun Application.registerMetricRoutes() {
    routing {
        metricRouting()
    }
}