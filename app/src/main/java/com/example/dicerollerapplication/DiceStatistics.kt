package com.example.dicerollerapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DiceStatistics(history: List<Int>) {
    val totalRolls = history.size
    val average = if (totalRolls > 0) history.average() else 0.0
    val mostRolled = history.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key

    // Card to provide a clean background
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEEEEEE)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Dice Statistics",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Total Rolls
            StatisticRow(label = "Total Rolls", value = "$totalRolls")

            // Average
            StatisticRow(label = "Average", value = average.format(2))

            // Most Rolled
            StatisticRow(label = "Most Rolled", value = mostRolled?.toString() ?: "None")
        }
    }
}

@Composable
fun StatisticRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

// Preview to see the improved UI
@Preview(showBackground = true)
@Composable
fun DiceStatisticsPreview() {
    DiceStatistics(history = listOf(1, 2, 3, 4, 5, 6, 6, 6))
}
