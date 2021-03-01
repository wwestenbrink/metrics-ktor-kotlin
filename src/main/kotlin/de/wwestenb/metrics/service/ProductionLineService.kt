package de.wwestenb.metrics.service

import de.wwestenb.metrics.models.ProductionLine

class UnknownProductionLineException(line_id: Long) : Exception("Unknown production line $line_id")

class ProductionLineService {
    val knownProductionLines = listOf<Long>(1, 2, 3)

    fun lookupLineById(line_id: Long): ProductionLine {
        if (knownProductionLines.contains(line_id))
            return ProductionLine(line_id)
        else
            throw UnknownProductionLineException(line_id)
    }
}

val productionLineService = ProductionLineService()