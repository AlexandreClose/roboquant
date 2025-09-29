package org.roboquant.journals

import org.roboquant.common.TimeSeries
import org.roboquant.journals.metrics.AccountMetric
import org.roboquant.journals.metrics.ExposureMetric
import org.roboquant.journals.metrics.PNLMetric
import org.roboquant.journals.metrics.PositionMetric
import org.roboquant.journals.metrics.ReturnsMetric
import kotlin.collections.filter
import kotlin.collections.joinToString
import kotlin.let
import kotlin.text.endsWith
import kotlin.text.startsWith

/**
 * SummaryJournal registers a curated set of core metrics and provides a readable summary via toString().
 *
 * Metrics included:
 * - AccountMetric
 * - ExposureMetric
 * - PNLMetric
 * - PositionMetric
 * - ReturnsMetric
 */
class SummaryJournal : MemoryJournal(
    AccountMetric(),
    ExposureMetric(),
    PNLMetric(),
    PositionMetric(),
    ReturnsMetric()
) {

    private fun lastOrNull(ts: TimeSeries): Double? = if (ts.size > 0) ts.values[ts.size - 1] else null

    private fun last(name: String): Double? = lastOrNull(getMetric(name))

    private fun sumPositionsValue(): Double? {
        val names = getMetricNames().filter { it.startsWith("position.") && it.endsWith(".value") }
        var sum = 0.0
        var found = false
        for (n in names) {
            val v = last(n)
            if (v != null) {
                sum += v
                found = true
            }
        }
        return if (found) sum else null
    }

    override fun toString(): String {
        val parts = mutableListOf<String>()

        // Account metrics
        last("account.equity")?.let { parts.add("equity=$it") }
        last("account.cash")?.let { parts.add("cash=$it") }
        last("account.positions")?.let { parts.add("positions=${it.toLong()}") }
        last("account.buyingpower")?.let { parts.add("buyingpower=$it") }
        last("account.mdd")?.let { parts.add("mdd=$it") }

        // Exposure
        last("exposure.net")?.let { parts.add("exposure.net=$it") }
        last("exposure.gross")?.let { parts.add("exposure.gross=$it") }

        // PNL
        last("pnl.total")?.let { parts.add("pnl.total=$it") }
        last("pnl.unrealized")?.let { parts.add("pnl.unrealized=$it") }
        last("pnl.realized")?.let { parts.add("pnl.realized=$it") }
        last("pnl.mkt")?.let { parts.add("pnl.mkt=$it") }

        // Returns
        last("returns.last")?.let { parts.add("returns.last=$it") }
        last("returns.mean")?.let { parts.add("returns.mean=$it") }
        last("returns.std")?.let { parts.add("returns.std=$it") }
        last("returns.sharperatio")?.let { parts.add("returns.sharpe=$it") }

        // Aggregate from PositionMetric
        sumPositionsValue()?.let { parts.add("positions.value=$it") }

        return parts.joinToString(" ")
    }
}