/*
 * Copyright 2020-2025 Neural Layer
 */
package org.roboquant.kandy

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.statistics.kandy.layers.candlestick
import org.roboquant.common.Asset
import org.roboquant.common.PriceBar
import org.roboquant.common.Timeframe
import org.roboquant.feeds.Feed
import org.roboquant.feeds.filter

/**
 * Kandy-based candlestick + volume chart for a single asset.
 *
 * Produces a DataFrame with columns:
 * - time (Instant or String depending on [useTime])
 * - open, high, low, close (Double)
 * - volume (Double)
 * - direction (Int) 1 for up, -1 for down
 */
class PriceBarChartKandy(
    private val feed: Feed,
    private val asset: Asset,
    private val timeframe: Timeframe = Timeframe.INFINITE,
    private val useTime: Boolean = true
) : KandyChart() {

    override fun buildDataFrame(): DataFrame<*> {
        val rows = feed.filter<PriceBar>(timeframe) { it.asset == asset }.map { (time, bar) ->
            val direction = if (bar.close >= bar.open) 1 else -1
            val t: Any = if (useTime) time else time.toString()
            val vol = if (bar.volume.isFinite()) bar.volume else 0.0
            mapOf(
                "time" to t,
                "open" to bar.open,
                "high" to bar.high,
                "low" to bar.low,
                "close" to bar.close,
                "volume" to vol,
                "direction" to direction
            )
        }
        return rows.toDataFrame()
    }

    override fun plot(): Plot {
        val df = buildDataFrame()
        return df.plot {
            candlestick(
                x = "month",
                open = "open",
                high="high",
                low="low",
                close="close"
            )
        }
    }

}
