package com.games.gamezone.Registration

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.games.gamezone.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    private lateinit var etName: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: MaterialButton

    private lateinit var etNameLayout: TextInputLayout
    private lateinit var etAgeLayout: TextInputLayout
    private lateinit var etEmailLayout: TextInputLayout
    private lateinit var etPhoneLayout: TextInputLayout
    private lateinit var etPasswordLayout: TextInputLayout
    private lateinit var etConfirmPasswordLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)

        initViews()
        setupTextWatchers()
        setupClickListeners()
        observeViewModel()
    }

    private fun initViews() {
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        etNameLayout = findViewById(R.id.etNameLayout)
        etAgeLayout = findViewById(R.id.etAgeLayout)
        etEmailLayout = findViewById(R.id.etEmailLayout)
        etPhoneLayout = findViewById(R.id.etPhoneLayout)
        etPasswordLayout = findViewById(R.id.etPasswordLayout)
        etConfirmPasswordLayout = findViewById(R.id.etConfirmPasswordLayout)
    }

    private fun setupTextWatchers() {
        // Clear errors when user starts typing
        etName.addTextChangedListener { etNameLayout.error = null }
        etAge.addTextChangedListener { etAgeLayout.error = null }
        etEmail.addTextChangedListener { etEmailLayout.error = null }
        etPhone.addTextChangedListener { etPhoneLayout.error = null }
        etPassword.addTextChangedListener { etPasswordLayout.error = null }
        etConfirmPassword.addTextChangedListener { etConfirmPasswordLayout.error = null }
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            viewModel.clearError()
            viewModel.register(
                name = etName.text.toString(),
                age = etAge.text.toString(),
                email = etEmail.text.toString(),
                phone = etPhone.text.toString(),
                password = etPassword.text.toString(),
                confirmPassword = etConfirmPassword.text.toString()
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                btnRegister.isEnabled = !state.isLoading
                btnRegister.text = if (state.isLoading) "Registering..." else "Register"

                if (state.errorMessage != null) {
                    showError(state.errorMessage)
                }

                if (state.isSuccess) {
                    Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_LONG).show()
                    // Navigate to next screen or finish activity
                    finish()
                }
            }
        }
    }

    private fun showError(message: String) {
        when {
            message.contains("name", ignoreCase = true) -> etNameLayout.error = message
            message.contains("age", ignoreCase = true) -> etAgeLayout.error = message
            message.contains("email", ignoreCase = true) -> etEmailLayout.error = message
            message.contains("phone", ignoreCase = true) || message.contains("number", ignoreCase = true) -> etPhoneLayout.error = message
            message.contains("password", ignoreCase = true) -> {
                if (message.contains("match", ignoreCase = true)) {
                    etConfirmPasswordLayout.error = message
                } else {
                    etPasswordLayout.error = message
                }
            }
            else -> Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}
