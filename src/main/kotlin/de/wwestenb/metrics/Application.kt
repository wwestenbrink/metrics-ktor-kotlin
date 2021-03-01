package de.wwestenb.metrics

import de.wwestenb.metrics.routes.registerLineSpeedRoutes
import de.wwestenb.metrics.routes.registerMetricRoutes
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    registerLineSpeedRoutes()
    registerMetricRoutes()
}
