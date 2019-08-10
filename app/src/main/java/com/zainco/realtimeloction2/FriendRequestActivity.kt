package com.zainco.realtimeloction2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.mancj.materialsearchbar.MaterialSearchBar
import com.zainco.realtimeloction2.listeners.IFirebaseLoadDone
import com.zainco.realtimeloction2.model.User
import com.zainco.realtimeloction2.utils.Common
import com.zainco.realtimeloction2.viewholder.FriendRequestViewHolder
import kotlinx.android.synthetic.main.activity_all_people.*

class FriendRequestActivity : AppCompatActivity(), IFirebaseLoadDone {
    override fun onFirebaseLoadUsernameDone(lstEmail: List<String>) {
        material_search_bar.lastSuggestions = lstEmail
    }

    override fun onFirebaseLoadFalied(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    var adapter: FirebaseRecyclerAdapter<User, FriendRequestViewHolder>? = null
    var searchAdapter: FirebaseRecyclerAdapter<User, FriendRequestViewHolder>? = null
    lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    val suggestList = listOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)

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
                        recycler_all_people.adapter = it
                    }
                }
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                startSearch(text.toString())
            }

        })

        recycler_all_people.setHasFixedSize(true)
        with(LinearLayoutManager(this)) {
            recycler_all_people.layoutManager = this
            recycler_all_people.addItemDecoration(DividerItemDecoration(this@FriendRequestActivity, this.orientation))
        }
        iFirebaseLoadDone = this
        loadfriendRequestList()
        loadSearchData()
    }

    private fun loadSearchData() {
        val query = FirebaseDatabase.getInstance().reference.child(Common.USER_INFORMATION)
            .child(Common.loggedUser.uid)
            .child(Common.FRIEND_REQUEST)

        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()
        searchAdapter = object : FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_firend_request, parent, false)
                return FriendRequestViewHolder(itemView)
            }

            override fun onBindViewHolder(viewholder: FriendRequestViewHolder, p1: Int, model: User) {
                viewholder.txt_user_email.setText(model.email)
                viewholder.btn_accept.setOnClickListener {
                    deleteFriendRequest(model, false)
                    addToAcceptList(model)
                    addUserToFriendContact(model)
                }
                viewholder.btn_decline.setOnClickListener {
                    deleteFriendRequest(model, true)
                }
            }

        }
        searchAdapter?.startListening()
        recycler_all_people.adapter = searchAdapter
    }

    private fun startSearch(text: String) {


    }

    private fun loadfriendRequestList() {
        val query = FirebaseDatabase.getInstance().reference.child(Common.USER_INFORMATION)
            .child(Common.loggedUser.uid)
            .child(Common.FRIEND_REQUEST)

        val options = FirebaseRecyclerOptions.Builder<User>()
            .setQuery(query, User::class.java)
            .build()
        adapter = object : FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_firend_request, parent, false)
                return FriendRequestViewHolder(itemView)
            }

            override fun onBindViewHolder(viewholder: FriendRequestViewHolder, p1: Int, model: User) {
                viewholder.txt_user_email.setText(model.email)
                viewholder.btn_accept.setOnClickListener {
                    deleteFriendRequest(model, false)
                    addToAcceptList(model)
                    addUserToFriendContact(model)
                }
                viewholder.btn_decline.setOnClickListener {
                    deleteFriendRequest(model, true)
                }
            }

        }
        adapter?.startListening()
        recycler_all_people.adapter = adapter
    }

    private fun addUserToFriendContact(model: User) {
        val acceptList = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
            .child(model.uid)
            .child(Common.ACCEPT_LIST)
            .child(model.uid)
            .setValue(Common.loggedUser)
    }

    private fun addToAcceptList(model: User) {
        FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
            .child(Common.loggedUser.uid!!)
            .child(Common.ACCEPT_LIST)
            .child(model.uid)
            .setValue(model)
    }

    private fun deleteFriendRequest(model: User, isShownMessage: Boolean) {
        val friendrequest = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
            .child(Common.loggedUser.uid)
            .child(Common.FRIEND_REQUEST)

        friendrequest.child(model.uid)
            .removeValue()
            .addOnSuccessListener {
                if (isShownMessage)
                    Toast.makeText(this, "Remove", Toast.LENGTH_SHORT).show()

            }

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
}
