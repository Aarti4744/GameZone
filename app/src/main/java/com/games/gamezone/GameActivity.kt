package com.games.gamezone

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.games.gamezone.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityGameBinding
    private lateinit var gameModel: GameModel
    private var buttons: Array<Button> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize game model
        gameModel = GameModel()

        // Initialize buttons array
        buttons = arrayOf(
            binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6,
            binding.btn7, binding.btn8, binding.btn9
        )

        // Set click listeners on all 9 buttons
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener(this)
            button.tag = index
        }

        binding.btnStartGame.setOnClickListener {
            startGame()
        }

        // Initialize UI
        updateUI()
    }

    override fun onClick(v: View?) {
        val button = v as Button
        val index = button.tag as Int

        if (gameModel.gameStatus == GameStatus.INPROGRESS) {
            handleButtonClick(index)
        }
    }

    private fun handleButtonClick(index: Int) {
        // Check if position is already filled
        if (gameModel.filledPos[index].isNotEmpty()) {
            Toast.makeText(this, "Position already taken!", Toast.LENGTH_SHORT).show()
            return
        }

        // Fill the position with current player
        gameModel.filledPos[index] = gameModel.currentPlayer

        // Update button text
        buttons[index].text = gameModel.currentPlayer
        buttons[index].isEnabled = false

        // Check for winner
        checkForWinner()

        // Check for draw
        if (gameModel.winner.isEmpty() && !gameModel.filledPos.contains("")) {
            gameModel.gameStatus = GameStatus.FINISHED
            gameModel.winner = "Draw"
            updateUI()
            showGameResultDialog()
            return
        }

        // Switch player if game is still in progress
        if (gameModel.gameStatus == GameStatus.INPROGRESS) {
            gameModel.currentPlayer = if (gameModel.currentPlayer == "X") "O" else "X"
            updateUI()
        }
    }

    private fun checkForWinner() {
        val winningCombinations = arrayOf(
            // Rows
            arrayOf(0, 1, 2),
            arrayOf(3, 4, 5),
            arrayOf(6, 7, 8),
            // Columns
            arrayOf(0, 3, 6),
            arrayOf(1, 4, 7),
            arrayOf(2, 5, 8),
            // Diagonals
            arrayOf(0, 4, 8),
            arrayOf(2, 4, 6)
        )

        for (combination in winningCombinations) {
            val pos1 = gameModel.filledPos[combination[0]]
            val pos2 = gameModel.filledPos[combination[1]]
            val pos3 = gameModel.filledPos[combination[2]]

            if (pos1.isNotEmpty() && pos1 == pos2 && pos2 == pos3) {
                gameModel.winner = pos1
                gameModel.gameStatus = GameStatus.FINISHED
                updateUI()
                showGameResultDialog()
                return
            }
        }
    }

    private fun startGame() {
        gameModel = GameModel()
        gameModel.gameStatus = GameStatus.INPROGRESS

        // Reset all buttons
        buttons.forEach { button ->
            button.text = ""
            button.isEnabled = true
            button.backgroundTintList = null
        }

        updateUI()
        Toast.makeText(this, "Game Started! Player ${gameModel.currentPlayer}'s turn", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        when (gameModel.gameStatus) {
            GameStatus.CREATED -> {
                binding.tvGameStatus.text = "Game Ready - Press Start Game"
                binding.btnStartGame.text = "Start Game"
            }
            GameStatus.INPROGRESS -> {
                binding.tvGameStatus.text = "Player ${gameModel.currentPlayer}'s turn"
                binding.btnStartGame.text = "Restart Game"
            }
            GameStatus.FINISHED -> {
                when (gameModel.winner) {
                    "Draw" -> binding.tvGameStatus.text = "Game Draw!"
                    else -> binding.tvGameStatus.text = "Player ${gameModel.winner} Won!"
                }
                binding.btnStartGame.text = "New Game"
                // Disable all buttons
                buttons.forEach { it.isEnabled = false }
            }
            else -> {
                binding.tvGameStatus.text = "Game not started"
            }
        }
    }

    private fun showGameResultDialog() {
        val message = when (gameModel.winner) {
            "Draw" -> "It's a Draw! 🤝"
            else -> "Player ${gameModel.winner} Wins! 🎉"
        }

        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage(message)
            .setPositiveButton("New Game") { _, _ ->
                startGame()
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Exit Game")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("No", null)
            .show()
    }
}