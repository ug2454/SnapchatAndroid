package com.example.snapchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


@Suppress("DEPRECATION")
class SnapsActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var messageTextView: TextView? = null

    var snapImageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)
        auth = Firebase.auth
        println(intent.getStringExtra("imageUrl"))
        messageTextView = findViewById(R.id.messageTextView)
        snapImageView = findViewById(R.id.viewSnapImageView)

        messageTextView?.text = intent.getStringExtra("message")


        val task = DownloadImage()
        val bitmap: Bitmap
        try {


            bitmap = task.execute(intent.getStringExtra("imageUrl")).get()!!
//            bitmap = task.execute("https://firebasestorage.googleapis.com/v0/b/snapchatandroid-b8836.appspot.com/o/images%2Fbd61d061-6d64-4503-a2b9-c585592f87e3.jpg?alt=media&token=f6660cdf-ebf5-4fee-b963-c178eb627f07").get()!!
            snapImageView?.setImageBitmap(bitmap)
            snapImageView?.scaleType=ImageView.ScaleType.FIT_CENTER
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser?.uid.toString()).child("snaps").child(
            intent.getStringExtra("snapKey").toString()
        ).removeValue()
        FirebaseStorage.getInstance().reference.child("images/${intent.getStringExtra("imageName")}").delete().addOnSuccessListener {
            Log.i("delete","image deleted from storage")
        }.addOnFailureListener{
            Log.i("failed",it.message.toString())
        }
    }

    class DownloadImage : AsyncTask<String?, Void?, Bitmap?>() {


        override fun doInBackground(vararg p0: String?): Bitmap? {
            return try {
                val url = URL(p0[0])
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                val inputStream: InputStream = connection.inputStream
                BitmapFactory.decodeStream(inputStream)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                null
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}