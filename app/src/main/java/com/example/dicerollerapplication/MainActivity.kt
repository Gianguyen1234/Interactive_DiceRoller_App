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
    val diceRollHistory = remember { mutableStateListOf<Int>() } // To store the roll history
    val rotationAngle = remember { Animatable(0f) }
    val translationX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val imageResource = when (result) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        else -> R.drawable.dice_6
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), // Add some padding to avoid content sticking to edges
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Space content between top and bottom
    ) {
        // Top: History Section
        DiceRollHistory(history = diceRollHistory)

        // Bottom: Dice Image and Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 100.dp), // Add space at the bottom
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dice Image with Shake and Rotation Animations
            Image(
                painter = painterResource(imageResource),
                contentDescription = result.toString(),
                modifier = Modifier
                    .graphicsLayer(
                        translationX = translationX.value, // Apply shake animation
                        rotationY = rotationAngle.value   // Apply rotation animation
                    )
                    .size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Roll Button
            Button(
                onClick = {
                    // Play the sound
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.prepare()
                    }
                    mediaPlayer.start()

                    // Start animations: Shake and Rotation
                    coroutineScope.launch {
                        // Shake animation
                        launch {
                            translationX.animateTo(
                                targetValue = 50f, // Move to the right
                                animationSpec = tween(durationMillis = 100)
                            )
                            translationX.animateTo(
                                targetValue = -50f, // Move to the left
                                animationSpec = tween(durationMillis = 100)
                            )
                            translationX.animateTo(
                                targetValue = 0f, // Return to center
                                animationSpec = tween(durationMillis = 100)
                            )
                        }

                        // Rotation animation
                        rotationAngle.animateTo(
                            targetValue = 360f, // Full rotation
                            animationSpec = tween(durationMillis = 500) // Animation duration
                        )
                        rotationAngle.snapTo(0f) // Reset rotation

                        // Generate new dice result
                        result = (1..6).random()
                        diceRollHistory.add(result) // Add the result to the history
                    }
                }
            ) {
                Text(stringResource(R.string.roll))
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Reset Button
            Button(
                onClick = {
                    diceRollHistory.clear() // Clear the history
                }
            ) {
                Text("Reset History")
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
