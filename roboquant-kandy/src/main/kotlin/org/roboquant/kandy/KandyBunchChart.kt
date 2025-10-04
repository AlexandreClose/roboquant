/*
 * Copyright 2020-2025 Neural Layer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.roboquant.kandy

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.multiplot.model.PlotBunch

/**
 * Base class for Kandy-based charts in roboquant.
 *
 * This base does not tie to a specific frontend; subclasses are expected to
 * create a DataFrame with the required columns and, if desired, use Kandy to
 * generate a plot that can be rendered in Notebooks or SSR.
 */
abstract class KandyBunchChart {

    var title: String? = null

    /** Width in pixels of the intended rendered chart. */
    var width: Int = 900

    /** Height in pixels of the intended rendered chart. */
    var height: Int = 450

    /**
     * Implementations should build a DataFrame backing the chart. The exact columns are
     * chart-specific. Keeping DataFrame as the interchange aligns with Kandy idioms.
     */
    abstract fun buildDataFrame(): DataFrame<*>

    abstract fun plot(): PlotBunch

    /**
     * Optional: subclasses may override to provide an HTML fragment for rendering
     * the Kandy/Lets-Plot output. The default returns a simple placeholder.
     */
    open fun asHTML(): String {
        val t = title ?: ""
        return """
            <div style=\"font-family: sans-serif;\">
              <div><strong>$t</strong></div>
              <div>Roboquant Kandy chart (data only). Plug into your preferred renderer.</div>
            </div>
        """.trimIndent()
    }
}
