package com.example.resolutionai.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.resolutionai.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    var number: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        subscribeCLickEvent()

    }

    private fun subscribeCLickEvent() {
        binding.getOtp.setOnClickListener {
            goToGetOTP()
        }
    }

    private fun goToGetOTP() {
        number = binding.numberEt.editText!!.text.toString()
        if (checkEditText()) {
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra("number", number)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "enter correct number", Toast.LENGTH_SHORT).show()
        }
    }

    // function to check edittext is empty or not
    fun checkEditText(): Boolean {

        if (number.isNotEmpty() && number.length == 10) {
            return true
        }
        return false
    }
}