package com.example.dicerollerapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AchievementSystem {
    private val rollHistory = mutableListOf<Int>() // Store roll history
    private val achievements = mutableStateListOf<String>() // List of unlocked achievements
    val newAchievement = mutableStateOf<String?>(null) // Store the latest unlocked achievement

    // Method to process a new roll
    fun processRoll(roll: Int) {
        rollHistory.add(roll)
        checkAchievements()
    }

    // Method to reset the roll history and achievements
    fun reset() {
        rollHistory.clear()
        achievements.clear()
        newAchievement.value = null
    }

    // Method to check and unlock achievements
    private fun checkAchievements() {
        if (rollHistory.size >= 2) {
            val lastTwo = rollHistory.takeLast(2)

            // Check "Snake Eyes"
            if (lastTwo[0] == 1 && lastTwo[1] == 1) {
                unlockAchievement("Snake Eyes")
            }

            // Check "Lucky Seven"
            if (lastTwo[0] == 6 && lastTwo[1] == 1) {
                unlockAchievement("Lucky Seven")
            }
        }

        // Check "Triple Six"
        if (rollHistory.size >= 3 && rollHistory.takeLast(3).all { it == 6 }) {
            unlockAchievement("Triple Six")
        }

        // Check "Four in a Row"
        if (rollHistory.size >= 4 && rollHistory.takeLast(4).distinct().size == 1) {
            unlockAchievement("Four in a Row")
        }

        // Check "High Roller"
        if (rollHistory.isNotEmpty() && rollHistory.last() == 6) {
            unlockAchievement("High Roller")
        }

        // Check "Low Roller"
        if (rollHistory.isNotEmpty() && rollHistory.last() == 1) {
            unlockAchievement("Low Roller")
        }
    }

    // Method to unlock a new achievement
    private fun unlockAchievement(achievement: String) {
        if (!achievements.contains(achievement)) {
            achievements.add(achievement)
            newAchievement.value = achievement
        }
    }

    // Getter for unlocked achievements
//    fun getAchievements(): SnapshotStateList<String> {
//        return achievements
//    }
}

@Composable
fun AchievementDialog(
    achievement: String,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close", color = MaterialTheme.colorScheme.primary)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.background,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.achievement),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Achievement Unlocked!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = achievement,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.trophy),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer(
                            rotationZ = 10f,
                            scaleX = 1.1f,
                            scaleY = 1.1f
                        )
                )
            }
        }
    )
}
