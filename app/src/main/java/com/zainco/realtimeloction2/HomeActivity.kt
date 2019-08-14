package com.zainco.realtimeloction2

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.mancj.materialsearchbar.MaterialSearchBar
import com.zainco.realtimeloction2.listeners.IFirebaseLoadDone
import com.zainco.realtimeloction2.listeners.IRecyclerItemClickListener
import com.zainco.realtimeloction2.model.User
import com.zainco.realtimeloction2.service.MyLocationReceiver
import com.zainco.realtimeloction2.utils.Common
import com.zainco.realtimeloction2.viewholder.UserViewHolder
import kotlinx.android.synthetic.main.activity_all_people.material_search_bar
import kotlinx.android.synthetic.main.content_home.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, IFirebaseLoadDone {
    override fun onFirebaseLoadUsernameDone(lstEmail: List<String>) {
        material_search_bar.lastSuggestions = lstEmail
    }

    override fun onFirebaseLoadFalied(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    var adapter: FirebaseRecyclerAdapter<User, UserViewHolder>? = null
    var searchAdapter: FirebaseRecyclerAdapter<User, UserViewHolder>? = null
    lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    val suggestList = listOf<String>()
    lateinit var publicLocation: DatabaseReference
    lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            startActivity(Intent(this, AllPeopleActivity::class.java))
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        val headerView = navView.getHeaderView(0)
        val textLogged = headerView.findViewById<TextView>(R.id.txt_logged_email)
        textLogged.text = Common.loggedUser!!.email



        material_search_bar.setCardViewElevation(10)
        material_search_bar.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val x = 0
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val x = 0
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val suggest = mutableListOf<String>()
                for (search in suggestList) {
                    if (search.toLowerCase().contains(material_search_bar.text.toLowerCase())) {
                        suggest.add(search)
                    }
                }
                material_search_bar.lastSuggestions = suggest
            }

        })
        material_search_bar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onButtonClicked(buttonCode: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    adapter?.let {
                        recycler_friend_list.adapter = it
                    }
                }
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                startSearch(text.toString())
            }

        })

        recycler_friend_list.setHasFixedSize(true)
        with(LinearLayoutManager(this)) {
            recycler_friend_list.layoutManager = this
            recycler_friend_list.addItemDecoration(DividerItemDecoration(this@HomeActivity, this.orientation))
        }

        publicLocation = FirebaseDatabase.getInstance().getReference(Common.PUBLIC_LOCATION)
        updateLocation()

        loadFriendList()
        loadSearchdata()
        iFirebaseLoadDone = this

    }

    private fun loadSearchdata() {
        val lstUserEmail = ArrayList<String>()
        val userList = FirebaseDatabase.getInstance().reference.child(Common.USER_INFORMATION)
            .child(Common.loggedUser!!.uid)
            .child(Common.ACCEPT_LIST)
        userList.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                iFirebaseLoadDone.onFirebaseLoadFalied(databaseError.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { userSnapshot ->
                    val user = userSnapshot.getValue(User::class.java)
                    lstUserEmail.add(user!!.email)
                }
                iFirebaseLoadDone.onFirebaseLoadUsernameDone(lstEmail = lstUserEmail)
            }

        })
    }

    private fun loadFriendList() {
        val query = FirebaseDatabase.getInstance().reference.child(Common.USER_INFORMATION)
            .child(Common.loggedUser!!.uid)
            .child(Common.ACCEPT_LIST)
        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()

        adapter = object : FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_user, parent, false)
                return UserViewHolder(itemView)
            }

            override fun onBindViewHolder(viewholder: UserViewHolder, p1: Int, model: User) {
                viewholder.txt_user_email.setText(StringBuilder(model.email))
                viewholder.setiRecyclerItemClickListener(object : IRecyclerItemClickListener {
                    override fun onItemClickListener(view: View, position: Int) {
                        Common.trackingUser = model
                        startActivity(Intent(this@HomeActivity, TrackingActivity::class.java))
                    }
                })
            }

        }
        adapter?.startListening()
        recycler_friend_list.adapter = adapter
    }

    override fun onStop() {
        adapter?.stopListening()
        searchAdapter?.stopListening()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        adapter?.startListening()
        searchAdapter?.startListening()
    }

    private fun updateLocation() {
        buildLocationRequest()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent())
    }

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(this@HomeActivity, MyLocationReceiver::class.java)
        intent.setAction(MyLocationReceiver.ACTION)
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.smallestDisplacement = 10f
        locationRequest.fastestInterval = 3000
        locationRequest.interval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    fun startSearch(text_search: String) {
        val query = FirebaseDatabase.getInstance().reference.child(Common.USER_INFORMATION)
            .child(Common.loggedUser!!.uid)
            .child(Common.ACCEPT_LIST)
            .orderByChild("email")
            .startAt(text_search)
        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()

        searchAdapter = object : FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_user, parent, false)
                return UserViewHolder(itemView)
            }

            override fun onBindViewHolder(viewholder: UserViewHolder, p1: Int, model: User) {
                viewholder.txt_user_email.text = StringBuilder(model.email)

                viewholder.setiRecyclerItemClickListener(object : IRecyclerItemClickListener {
                    override fun onItemClickListener(view: View, position: Int) {
                        Common.trackingUser = model
                        startActivity(Intent(this@HomeActivity, TrackingActivity::class.java))

                    }
                })
            }

        }
        searchAdapter?.startListening()
        recycler_friend_list.adapter = searchAdapter
    }
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_find_people -> {
                startActivity(Intent(this, AllPeopleActivity::class.java))
            }
            R.id.nav_add_people -> {
                startActivity(Intent(this, FriendRequestActivity::class.java))
            }
            R.id.nav_sign_out -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
