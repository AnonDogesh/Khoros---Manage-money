package com.khoros.app.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Draws an animated donut chart for category spend distribution.
 */
@Composable
fun DonutChart(data: List<Pair<String, Float>>, modifier: Modifier = Modifier) {
    val total = data.sumOf { it.second.toDouble() }.toFloat().coerceAtLeast(1f)
    val palette = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary)
    val progress by animateFloatAsState(targetValue = 1f, animationSpec = tween(800), label = "donut-progress")

    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Box(modifier = modifier.size(220.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Canvas(modifier = Modifier.size(220.dp)) {
                var startAngle = -90f
                data.forEachIndexed { i, (_, value) ->
                    val sweep = (value / total) * 360f * progress
                    drawArc(
                        color = palette[i % palette.size],
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(width = 44f, cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )
                    startAngle += (value / total) * 360f
                }
            }
            Text("₹${"%.2f".format(total)}", style = MaterialTheme.typography.titleLarge)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            data.forEachIndexed { i, (category, _) ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(10.dp).background(palette[i % palette.size], CircleShape))
                    Text(category, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

/**
 * Draws an animated line chart for trend values.
 */
@Composable
fun LineTrendChart(points: List<Pair<Float, Float>>, modifier: Modifier = Modifier) {
    val maxY = points.maxOfOrNull { it.second }?.coerceAtLeast(1f) ?: 1f
    val progress by animateFloatAsState(targetValue = 1f, animationSpec = tween(800), label = "line-progress")
    val pointColor = MaterialTheme.colorScheme.tertiary
    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.fillMaxWidth().height(180.dp).padding(8.dp)) {
        if (points.size < 2) return@Canvas
        val widthStep = size.width / (points.size - 1)
        val path = androidx.compose.ui.graphics.Path()
        points.forEachIndexed { index, (_, y) ->
            val x = index * widthStep
            val yPos = size.height - (y / maxY) * size.height * progress
            if (index == 0) path.moveTo(x, yPos) else path.lineTo(x, yPos)
            drawCircle(pointColor, radius = 7f, center = Offset(x, yPos))
        }
        drawPath(path = path, color = lineColor, style = Stroke(width = 6f))
    }
}
