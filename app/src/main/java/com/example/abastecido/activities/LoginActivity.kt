package com.example.abastecido.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.abastecido.R
import com.example.abastecido.databinding.ActivityInventaryBinding
import com.example.abastecido.databinding.ActivityLoginBinding
import com.example.abastecido.test
import com.google.firebase.auth.FirebaseAuth
import java.security.Provider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUp()
    }

    private fun setUp(){
        title = "Log In"

        binding.btConfirm.setOnClickListener {
            if (binding.etUser.text.isNotEmpty() && binding.etPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.etUser.text.toString(), binding.etPassword.text.toString()).addOnCompleteListener {

                    if (it.isSuccessful){
                        showHome(it.result?.user?.email ?:"", ProviderType.BASIC)
                    }else{
                        showAlert()
                    }
                }
            }
        }
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha produccido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val intent = Intent(this, InventaryActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(intent)
    }

}