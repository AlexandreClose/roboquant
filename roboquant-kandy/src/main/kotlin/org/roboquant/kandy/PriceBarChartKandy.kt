/*
 * Copyright 2020-2025 Neural Layer
 */
package org.roboquant.kandy

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
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
        val times = mutableListOf<Any>()
        val opens = mutableListOf<Double>()
        val highs = mutableListOf<Double>()
        val lows = mutableListOf<Double>()
        val closes = mutableListOf<Double>()
        val volumes = mutableListOf<Double>()
        val dirs = mutableListOf<Int>()

        feed.filter<PriceBar>(timeframe) { it.asset == asset }.forEach { (time, bar) ->
            val direction = if (bar.close >= bar.open) 1 else -1
            val t: Any = if (useTime) time else time.toString()
            val vol = if (bar.volume.isFinite()) bar.volume else 0.0

            times += t
            opens += bar.open
            highs += bar.high
            lows += bar.low
            closes += bar.close
            volumes += vol
            dirs += direction
        }

        return dataFrameOf(
            "time" to times,
            "open" to opens,
            "high" to highs,
            "low" to lows,
            "close" to closes,
            "volume" to volumes,
            "direction" to dirs
        )
    }


    override fun plot(): Plot {
        val df = buildDataFrame()
        return df.plot {
            candlestick(
                x = "time",
                open = "open",
                high="high",
                low="low",
                close="close"
            )
        }
    }

}
