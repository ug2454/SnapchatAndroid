package com.example.snapchat


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SecondActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    private fun logOut() {
        auth = Firebase.auth
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.snap -> {
                addSnap()
                true
            }
            R.id.logout -> {
                logOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    }

    private fun addSnap() {
        val intent = Intent(this, CreateSnapActivity::class.java)

        startActivity(intent)
        TODO("check if want to finish activity or not")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }
}