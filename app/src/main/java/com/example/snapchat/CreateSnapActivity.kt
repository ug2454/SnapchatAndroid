package com.example.snapchat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*


class CreateSnapActivity : AppCompatActivity() {


    var createSnapImageView: ImageView? = null
    var messageEditText: EditText? = null
    var pickImageButton: Button? = null
    var nextButton: Button? = null
    var imageName = UUID.randomUUID().toString() + ".jpg"
    var storage = Firebase.storage
    var storageRef = storage.reference


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        createSnapImageView = findViewById(R.id.imageView)
        messageEditText = findViewById(R.id.messageEditText)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun pickImage(view: View) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )

        } else {
            getPhoto();
        }
    }

    fun next(view: View) {
        createSnapImageView?.isDrawingCacheEnabled = true
        createSnapImageView?.buildDrawingCache()
        val bitmap = (createSnapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val data = baos.toByteArray()


        val uploadTask =
            FirebaseStorage.getInstance().reference.child("images").child(imageName).putBytes(data)

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(baseContext, "Failed to upload", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener {
            Toast.makeText(
                baseContext, "Image uploaded",
                Toast.LENGTH_LONG
            ).show()
            var downloadUrl: Uri? = null
            FirebaseStorage.getInstance().reference.child("images")
                .child(imageName).downloadUrl.addOnSuccessListener { it1 ->
                downloadUrl = it1
                Log.i(
                    "url",
                    downloadUrl.toString()
                )
                val intent = Intent(this, ChooseUserActivity::class.java)
                intent.putExtra("imageUrl", downloadUrl.toString())
                intent.putExtra("imageName", imageName)
                intent.putExtra("message", messageEditText?.text.toString())
                startActivity(intent)
            }


        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)


        val selectedImage = data!!.data
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)

                createSnapImageView?.setImageBitmap(bitmap)
                createSnapImageView?.scaleType = ImageView.ScaleType.FIT_CENTER
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}