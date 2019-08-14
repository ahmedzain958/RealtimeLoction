package com.zainco.realtimeloction2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.zainco.realtimeloction2.model.User
import com.zainco.realtimeloction2.utils.Common
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {
    companion object {
        const val MY_REQUEST_CODE = 1000
    }

    lateinit var firebaseUser: FirebaseUser
    lateinit var providers: List<AuthUI.IdpConfig>
    val user_information: DatabaseReference = FirebaseDatabase.getInstance()
        .getReference(Common.USER_INFORMATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Paper.init(this)
        providers = listOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    showSignInOptions()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@MainActivity, "u must accept permission", Toast.LENGTH_SHORT).show()
                }

            }).check()
    }

    private fun showSignInOptions() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build(), MY_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                 firebaseUser = FirebaseAuth.getInstance().currentUser!!
                user_information.orderByKey()
                    .equalTo(firebaseUser.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(this@MainActivity, "onCancelled", Toast.LENGTH_SHORT).show()
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.value == null) {
                                if (!dataSnapshot.child(firebaseUser.uid).exists()) {
                                    Common.loggedUser = User(
                                        firebaseUser.email!!,
                                        firebaseUser.uid
                                    )
                                    user_information.child(Common.loggedUser!!.uid).setValue(
                                        Common.loggedUser
                                    )
                                }
                            } else {
                                Common.loggedUser = dataSnapshot.child(firebaseUser.uid).getValue(User::class.java)!!
                            }
                            Paper.book().write(Common.USER_UID_SAVE_KEY, Common.loggedUser!!.uid)
                            updateToken()
                            setUpUI()
                        }

                    })
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setUpUI() {
        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
        finish()
    }

    private fun updateToken() {
        val tokens = FirebaseDatabase.getInstance()
            .getReference(Common.Tokens)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            tokens.child(firebaseUser.uid)
                .setValue(instanceIdResult.token)
        }.addOnFailureListener {
            Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
        }

    }

}
