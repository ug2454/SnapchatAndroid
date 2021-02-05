package com.example.snapchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.database
    private val myRef = database.reference


    private fun goToActivity() {
        val intent = Intent(this, SecondActivity::class.java)
        finish()
        startActivity(intent)
    }

    fun signUp(view: View) {
        if (!emailEditText?.text.isNullOrBlank()) {
            auth.createUserWithEmailAndPassword(
                emailEditText?.text.toString(),
                passwordEditText?.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Login", "createUserWithEmail:success")
                        val user = auth.currentUser
//                    updateUI(user)
                        myRef.child("users").child(task.result?.user?.uid.toString()).child("email").setValue(emailEditText?.text.toString())
                        goToActivity()
                    } else {

                        // If sign in fails, display a message to the user.
                        Log.w("Login", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, task.exception.toString(),
                            Toast.LENGTH_LONG
                        ).show()
//                    updateUI(null)

                    }

                    // ...
                }
        } else {
            Toast.makeText(applicationContext, "Fields cannot be blank", Toast.LENGTH_SHORT).show()
        }

    }

    fun login(view: View) {
        if (!emailEditText?.text.isNullOrBlank()) {
            auth.signInWithEmailAndPassword(
                emailEditText?.text.toString(),
                passwordEditText?.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Signup", "signInWithEmail:success")
                        val user = auth.currentUser
//                    updateUI(user)
                        goToActivity()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Signup", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, task.exception.toString(),
                            Toast.LENGTH_LONG
                        ).show()
//                    updateUI(null)
                        // ...
                    }

                    // ...
                }
        } else {
            Toast.makeText(applicationContext, "Fields cannot be blank", Toast.LENGTH_SHORT).show()
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            goToActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        emailEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        auth = Firebase.auth


    }
}