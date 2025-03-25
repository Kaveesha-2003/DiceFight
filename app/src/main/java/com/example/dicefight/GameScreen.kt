package com.example.dicefight

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import kotlin.random.Random

@Composable
fun GameScreen(navController: NavHostController, playerWinsState: MutableState<Int>, computerWinsState: MutableState<Int>) {
    var playerDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }
    var computerDice by remember { mutableStateOf(List(5) { Random.nextInt(1, 7) }) }

    var playerScore by remember { mutableStateOf(0) }
    var computerScore by remember { mutableStateOf(0) }

    var tempPlayerScore by remember { mutableStateOf(0) }
    var tempComputerScore by remember { mutableStateOf(0) }

    var selectedTargetScore by remember { mutableStateOf(101) }

    val playerWins by playerWinsState
    val computerWins by computerWinsState

    var showWinnerDialog by remember { mutableStateOf(false) }
    var winnerMessage by remember { mutableStateOf("") }
    var winnerColor by remember { mutableStateOf(Color.Black) }

    var rollCount by remember { mutableStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }

    var selectedDiceIndex by remember { mutableStateOf<Int?>(null) }



    var finalGameOver by remember { mutableStateOf(false) }


    var computerRollCount by remember { mutableStateOf(0) }
    var computerSelectedDice by remember { mutableStateOf(MutableList(5) { false }) }

    var triggerAutoComputerRoll by remember { mutableStateOf(false) }



    fun checkWinner() {
        if (playerScore >= selectedTargetScore || computerScore >= selectedTargetScore) {
            if (playerScore == computerScore) {
                // 🟡 Match tied — show dialog and restart
                winnerMessage = "Match is tied! One more round to break the tie."
                winnerColor = Color.Gray
                showWinnerDialog = true


                // Reset game state (restart round)
                playerScore = 0
                computerScore = 0
                rollCount = 0
                selectedDiceIndex = null

                playerDice = List(5) { Random.nextInt(1, 7) }
                computerDice = List(5) { Random.nextInt(1, 7) }

                return // Stop here
            }

            // ✅ Someone actually won
            gameOver = true
            if (playerScore > computerScore) {
                winnerMessage = "You Win!"
                winnerColor = Color.Green
                playerWinsState.value++
            } else {
                winnerMessage = "You Lost!"
                winnerColor = Color.Red
                computerWinsState.value++
            }

            showWinnerDialog = true

            if (playerWinsState.value == 10 || computerWinsState.value == 5) {
                finalGameOver = true
            }
        }
    }



    fun addScore() {
        if (!gameOver) {
            playerScore += tempPlayerScore
            computerScore += tempComputerScore
            rollCount = 0
            computerRollCount = 0
            computerSelectedDice = MutableList(5) { false }

            selectedDiceIndex = null

            checkWinner()
        }
    }

    fun performComputerRoll() {
        if (computerRollCount >= 3) return

        val scoreDifference = computerScore - playerScore
        val currentSum = computerDice.sum()

        val shouldReroll = when {
            currentSum >= 24 && scoreDifference >= 0 -> false
            currentSum <= 17 && scoreDifference < 0 -> true
            else -> true
        }

        if (computerRollCount > 0 && !shouldReroll) return

        // ✅ SAFELY BUILD THE SELECTION LIST
        val newSelectedDice = computerDice.map { die ->
            when {
                die >= 5 -> true
                scoreDifference < -20 -> false
                die >= 4 && currentSum >= 22 -> true
                else -> false
            }
        }

        // ✅ SAFELY UPDATE DICE BASED ON SELECTION
        computerDice = computerDice.mapIndexed { index, oldValue ->
            if (newSelectedDice[index]) oldValue else Random.nextInt(1, 7)
        }

        computerSelectedDice = newSelectedDice.toMutableList()
        tempComputerScore = computerDice.sum()
        computerRollCount++
    }







    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()

                    .height(105.dp) // Total height for the background line
                    .background(Color(0xFF664FA3)) // 🎨 Line color behind text
            ) {
                Text(
                    text = "DICE FIGHT",
                    fontSize = 35.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center).padding(top = 20.dp)
                )
            }


            // 🏆 Win Counters at Top-Left
            Text(
                text = "H: $playerWins / C: $computerWins",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(46.dp,top=180.dp)
            )

            // 🎯 Scoreboard at Top Right
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top=120.dp,end=30.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text("Score", fontSize = 20.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Human: $playerScore", fontSize = 16.sp, color = Color.Blue)
                    Text("Computer: $computerScore", fontSize = 16.sp, color = Color.Red)
                }
            }

            // 📦 Main Game Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 250.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Target Score Selection
                Text("Select Target Score", fontSize = 18.sp, color = Color.Black)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(101, 201, 301, 401).forEach { target ->
                        Button(
                            onClick = { if (!gameOver) selectedTargetScore = target },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTargetScore == target) Color.Black else Color.Gray
                            ),
                            modifier = Modifier.padding(4.dp),
                            enabled = !gameOver
                        ) {
                            Text(text = "$target")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Computer's Dice
                Text("- Computer -", fontSize = 24.sp, color = Color.Red)
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    computerDice.forEach { diceValue ->
                        Image(
                            painter = painterResource(id = getDiceDrawable(diceValue)),
                            contentDescription = "Computer Dice",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Player's Dice
                Text("- Human -", fontSize = 24.sp, color = Color.Blue)
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    playerDice.forEachIndexed { index, diceValue ->
                        Image(
                            painter = painterResource(id = getDiceDrawable(diceValue)),
                            contentDescription = "Player Dice",
                            modifier = Modifier
                                .size(60.dp)
                                .background(if (selectedDiceIndex == index && rollCount > 0) Color.Yellow else Color.Transparent)

                                .clickable(enabled = rollCount > 0 && !gameOver) {
                                    selectedDiceIndex = if (selectedDiceIndex == index) null else index
                                }


                        )
                    }
                }
                Text("Select to hold a dice after first roll", fontSize = 14.sp, color = Color.Black)

                Spacer(modifier = Modifier.height(30.dp))

                // Roll Button
                Button(
                    onClick = {
                        if (!gameOver && rollCount < 3) {
                            playerDice = playerDice.mapIndexed { index, oldValue ->
                                if (selectedDiceIndex == index && rollCount > 0) oldValue else Random.nextInt(1, 7)
                            }

                            computerDice = List(5) { Random.nextInt(1, 7) }

                            tempPlayerScore = playerDice.sum()
                            tempComputerScore = computerDice.sum()

                            rollCount++

                            if (rollCount == 3) {
                                triggerAutoComputerRoll = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(60.dp),
                    enabled = rollCount < 3 && !gameOver
                ) {
                    Text(text = "Roll", fontSize = 20.sp)
                }

// 🟢 Auto computer finish if rollCount reaches 3
                if (triggerAutoComputerRoll) {
                    LaunchedEffect(triggerAutoComputerRoll) {
                        repeat(3 - computerRollCount) {
                            performComputerRoll()
                            kotlinx.coroutines.delay(300L)
                        }
                        addScore()
                        triggerAutoComputerRoll = false
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

// 🟣 Score Button
                var triggerManualComputerRoll by remember { mutableStateOf(false) }

                Button(
                    onClick = {
                        triggerManualComputerRoll = true
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(60.dp),
                    enabled = rollCount > 0 && rollCount < 3 && !gameOver
                ) {
                    Text(text = "Score", fontSize = 20.sp)
                }

// 🟢 Auto computer finish if user clicks Score early
                if (triggerManualComputerRoll) {
                    LaunchedEffect(triggerManualComputerRoll) {
                        repeat(3 - computerRollCount) {
                            performComputerRoll()
                            kotlinx.coroutines.delay(300L)
                        }
                        addScore()
                        triggerManualComputerRoll = false
                    }
                }
            }
        }
    }

    if (showWinnerDialog) {
        WinnerDialog(
            message = winnerMessage,
            color = winnerColor,
            navController = navController
        )
    }
    // ✅ Final Game Redirection after win count reached
    if (finalGameOver) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            playerWinsState.value = 0
            computerWinsState.value = 0
            navController.navigate("mainScreen")
        }
    }

}

@Composable
fun WinnerDialog(message: String, color: Color, navController: NavHostController) {
    Dialog(onDismissRequest = { }) {
        Surface(shape = RoundedCornerShape(30.dp), color = Color.White) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = message, fontSize = 24.sp, color = color)
                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { navController.navigate("mainScreen") }) {
                    Text(text = "Go Back")
                }
            }
        }
    }
}

// ✅ Dice Value to Drawable Mapper
fun getDiceDrawable(value: Int): Int = listOf(
    R.drawable.dice_1,
    R.drawable.dice_2,
    R.drawable.dice_3,
    R.drawable.dice_4,
    R.drawable.dice_5,
    R.drawable.dice_6
)[value - 1]