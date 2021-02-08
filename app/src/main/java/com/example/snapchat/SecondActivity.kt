package com.example.snapchat


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class SecondActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    private val mAuth = FirebaseAuth.getInstance()
    var snapsListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var snaps: ArrayList<DataSnapshot> = ArrayList()


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

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        snapsListView = findViewById(R.id.snapsListView)


        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)

        snapsListView?.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("users")
            .child(mAuth.currentUser?.uid.toString()).child("snaps")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val email = snapshot.child("from").value as String
                    emails.add(email)
                    snaps.add(snapshot)

                    adapter.notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    for((index, snap: DataSnapshot) in snaps.withIndex()){
                        if(snap.key==snapshot.key){
                            emails.removeAt(index)
                            snaps.removeAt(index)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        snapsListView?.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val snapshot = snaps[i]
                println(snapshot)
                val intent = Intent(applicationContext, SnapsActivity::class.java)

                println(snapshot.child("imageUrl").value as String)

                intent.putExtra("imageName", snapshot.child("imageName").value as String)
                intent.putExtra("imageUrl", snapshot.child("imageUrl").value.toString())
//                intent.putExtra("imageURL", "https://firebasestorage.googleapis.com/v0/b/snapchatandroid-b8836.appspot.com/o/images%2Fbd61d061-6d64-4503-a2b9-c585592f87e3.jpg?alt=media&token=f6660cdf-ebf5-4fee-b963-c178eb627f07")
                intent.putExtra("message", snapshot.child("message").value as String)
                intent.putExtra("snapKey", snapshot.key)
                startActivity(intent)


            }
    }






}