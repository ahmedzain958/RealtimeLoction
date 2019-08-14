package com.zainco.realtimeloction2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.zainco.realtimeloction2.model.MyLocation
import com.zainco.realtimeloction2.utils.Common

class TrackingActivity : AppCompatActivity(), OnMapReadyCallback, ValueEventListener {
    override fun onCancelled(p0: DatabaseError) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDataChange(p0: DataSnapshot) {
        if (p0.value != null) {
            val location = p0.getValue(MyLocation::class.java)

            val userMarker = LatLng(location!!.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(userMarker).title(Common.trackingUser!!.email)
                .snippet(Common.getDateFormatted(Common.convertTimeStampToDate(location.time))))

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker,16f))
        }
    }

    private lateinit var mMap: GoogleMap
    private lateinit var trackingUserLocation: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerEventRealtime()
    }

    private fun registerEventRealtime() {
        trackingUserLocation = FirebaseDatabase.getInstance()
            .getReference(Common.PUBLIC_LOCATION)
            .child(Common.trackingUser!!.uid)

        trackingUserLocation.addValueEventListener(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.my_uber_style))
    }

    override fun onResume() {
        super.onResume()
        trackingUserLocation.addValueEventListener(this)
    }

    override fun onStop() {
        trackingUserLocation.addValueEventListener(this)
        super.onStop()
    }
}
