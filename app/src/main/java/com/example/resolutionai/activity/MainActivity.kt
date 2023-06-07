package com.example.resolutionai.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.resolutionai.account.LoginActivity
import com.example.resolutionai.adapter.ResultAdapter
import com.example.resolutionai.databinding.ActivityMainBinding
import com.example.resolutionai.utils.DialogUtils.buildLoadingDialog
import com.example.resolutionai.utils.FirebaseUtils
import com.example.resolutionai.viewmodel.ViewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel
    lateinit var alertDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        variableInit()
        subscribeClickEvents()
        subscribeUi()

    }

    private fun subscribeUi() {
        alertDialog.show()
        viewModel.getResults { task->
            alertDialog.dismiss()
            if (task.isNotEmpty()) {
                binding.noDataTextView.visibility = View.GONE
                binding.rc.visibility = View.VISIBLE
                binding.rc.adapter = ResultAdapter(this@MainActivity, task)
            } else {
                binding.noDataTextView.visibility = View.VISIBLE
                binding.rc.visibility = View.GONE
            }
        }
    }

    private fun subscribeClickEvents() {
        binding.scanQr.setOnClickListener {
            startActivity(Intent(this, QRCodeCameraActivity::class.java))
        }
    }

    private fun variableInit() {
        alertDialog = buildLoadingDialog(this)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]

    }
    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.dismiss()
    }

}