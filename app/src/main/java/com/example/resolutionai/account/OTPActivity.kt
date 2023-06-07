package com.example.resolutionai.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.resolutionai.activity.MainActivity
import com.example.resolutionai.databinding.ActivityOtpactivityBinding
import com.example.resolutionai.utils.DialogUtils.buildLoadingDialog
import com.example.resolutionai.utils.FirebaseUtils.firebaseAuth
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {


    lateinit var binding: ActivityOtpactivityBinding

    private lateinit var dialog: AlertDialog
    private lateinit var verificationId: String
    private lateinit var phonenumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        variableInit()
        subscribeUi()

        val options = getOTP()

        PhoneAuthProvider.verifyPhoneNumber(options)
        subscribeOnClickEvents()

    }


    private fun subscribeOnClickEvents() {
        binding.verify.setOnClickListener {

            val otp = binding.otpEt.editText!!.text.toString()
            if (otp.isNotEmpty()) {
                dialog.show()
                otpVerification(otp)
            } else {
                Toast.makeText(this@OTPActivity, "Enter OTP", Toast.LENGTH_LONG).show()
            }
        }
    }
/*
    function to get otp verification code
    */

    private fun getOTP(): PhoneAuthOptions {
        dialog.show()
        return PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phonenumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this@OTPActivity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    dialog.dismiss()
                    Toast.makeText(this@OTPActivity, "Verification Failed $p0", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    dialog.dismiss()
                    verificationId = p0
                    println("mujeeb $verificationId")
                    dialog.dismiss()
                }

            }).build()
    }


    // function to verify otp
    private fun otpVerification(otp: String) {

        // otp verification
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                dialog.dismiss()
                Toast.makeText(this@OTPActivity, "welcome", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@OTPActivity, MainActivity::class.java))
                finish()

            } else {
                dialog.dismiss()
                Toast.makeText(this@OTPActivity, "${it.exception}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun subscribeUi() {
        binding.numberTv.text = phonenumber
    }

    private fun variableInit() {
        phonenumber = "+91" + intent.getStringExtra("number").toString()
        // loading dialog
        dialog = buildLoadingDialog(this@OTPActivity)
    }


}