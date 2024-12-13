package com.example.dicerollerapplication

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dicerollerapplication.ui.theme.DiceRollerApplicationTheme
import kotlinx.coroutines.launch
import kotlin.Int

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceRollerApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DiceRollerApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun DiceWithButtonAndImage(modifier: Modifier = Modifier) {
    var result by remember { mutableIntStateOf(1) }
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.dice_roll) }
    val diceRollHistory = remember { mutableStateListOf<Int>() }
    val rotationAngle = remember { Animatable(0f) }
    val translationX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    // Instantiate AchievementSystem
    val achievementSystem = remember { AchievementSystem() }
    val newAchievement = achievementSystem.newAchievement.value

    val imageResource = when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    // Dialog for new achievements
    // Show Achievement Dialog if a new achievement is unlocked
    newAchievement?.let { achievement ->
        AchievementDialog(
            achievement = achievement,
            onDismissRequest = { achievementSystem.newAchievement.value = null }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp, top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // Top: Statistics Section
        DiceStatistics(history = diceRollHistory)

        // Middle: History Section
        DiceRollHistory(history = diceRollHistory)

        // Bottom: Dice Image and Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(imageResource),
                contentDescription = result.toString(),
                modifier = Modifier
                    .graphicsLayer(
                        translationX = translationX.value,
                        rotationY = rotationAngle.value
                    )
                    .size(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Roll Button
                Button(
                    onClick = {
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.stop()
                            mediaPlayer.prepare()
                        }
                        mediaPlayer.start()

                        coroutineScope.launch {
                            launch {
                                translationX.animateTo(
                                    targetValue = 50f,
                                    animationSpec = tween(durationMillis = 100)
                                )
                                translationX.animateTo(
                                    targetValue = -50f,
                                    animationSpec = tween(durationMillis = 100)
                                )
                                translationX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 100)
                                )
                            }

                            rotationAngle.animateTo(
                                targetValue = 360f,
                                animationSpec = tween(durationMillis = 500)
                            )
                            rotationAngle.snapTo(0f)

                            result = (1..6).random()
                            diceRollHistory.add(result)

                            // Process roll in AchievementSystem
                            achievementSystem.processRoll(result)
                        }
                    }
                ) {
                    Text(stringResource(R.string.roll))
                }

                // Reset Button
                Button(
                    onClick = {
                        diceRollHistory.clear()
                        achievementSystem.reset()
                    }
                ) {
                    Text("Reset History")
                }
            }
        }
    }
}


@Composable
fun DiceRollerApp(modifier: Modifier = Modifier) {
    DiceWithButtonAndImage(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)

    )
}

@Preview(showBackground = true)
@Composable
fun DiceRollerAppPreview() {
    DiceRollerApplicationTheme {
        DiceWithButtonAndImage(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)

        )
    }
}
