package com.dicoding.storysubmission.view.signup

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storysubmission.data.Result
import com.dicoding.storysubmission.databinding.ActivitySignupBinding
import com.dicoding.storysubmission.view.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

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

    // !!-------------------- Signup action --------------------!!
    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.signup(name, email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                            Log.d("Debug: signup", "signup: uploading...")
                        }

                        is Result.Success -> {
                            showToast(result.data.message)
                            showLoading(false)
                            Log.d("Debug: signup", "signup: finish...")

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

                        is Result.Error -> {
                            showToast(result.error)
                            showLoading(false)
                            Log.d("Debug: signup", "signup: error...!!")
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}