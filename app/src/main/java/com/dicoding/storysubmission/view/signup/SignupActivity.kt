package com.dicoding.storysubmission.view.signup

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storysubmission.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

        // binding to access the EditText
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!isValidEmail(s.toString())) {
                    binding.emailEditText.error = "Must be in valid email format"
                } else {
                    binding.emailEditText.error = null
                }
            }

            override fun afterTextChanged(s: Editable) {
                // No action needed here
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        //  val emailPattern = "^A-Za-z([@]{1})(.{1,})(\\.)(.{1,})"
        //  val emailPattern = "^[A-Za-z]+@[A-Za-z]+\\.[A-Za-z]{2,}"
        //  val emailPattern = "^[A-Za-z0-9]+@[A-Za-z]+\\.[A-Za-z]{2,}"
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(?:\\.[\\p{L}]{2,})?\$"
        return email.matches(emailPattern.toRegex())
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()

            AlertDialog.Builder(this).apply {
                setTitle("Success!")
                setMessage("Account created successfully")
                setPositiveButton("Continue log in") { _, _ ->
                    finish()
                }
                create()
                show()
            }
        }
    }
}