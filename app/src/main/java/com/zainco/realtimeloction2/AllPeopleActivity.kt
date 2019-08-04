package com.zainco.realtimeloction2

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.zainco.realtimeloction2.listeners.IFirebaseLoadDone
import com.zainco.realtimeloction2.listeners.IRecyclerItemClickListener
import com.zainco.realtimeloction2.model.User
import com.zainco.realtimeloction2.utils.Common
import com.zainco.realtimeloction2.viewholder.UserViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.activity_all_people.*

class AllPeopleActivity : AppCompatActivity(), IFirebaseLoadDone {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_people)

        material_search_bar.setCardViewElevation(10)
        material_search_bar.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
            recycler_all_people.addItemDecoration(DividerItemDecoration(this@AllPeopleActivity, this.orientation))
        }
        iFirebaseLoadDone = this
        loadUserList()
        loadSearchData()
    }

    private fun loadSearchData() {
        val lstUserEmail = ArrayList<String>()
        val userList = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
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

    private fun loadUserList() {
        val query = FirebaseDatabase.getInstance().reference.child(Common.USER_INFORMATION)
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
                if (model.email == Common.loggedUser?.email) {
                    viewholder.txt_user_email.setText(StringBuilder(model.email).append(" (me) "))
                    viewholder.txt_user_email.setTypeface(viewholder.txt_user_email.typeface, Typeface.ITALIC)
                } else {
                    viewholder.txt_user_email.setText(StringBuilder(model.email))
                }
                viewholder.setiRecyclerItemClickListener(object : IRecyclerItemClickListener {
                    override fun onItemClickListener(view: View, position: Int) {

                    }
                })
            }

        }
        adapter?.startListening()
        recycler_all_people.adapter = adapter

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

    fun startSearch(text_search: String) {
        val query = FirebaseDatabase.getInstance().reference.child(Common.USER_INFORMATION)
            .orderByChild("name")
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
                if (model.email == Common.loggedUser?.email) {
                    viewholder.txt_user_email.setText(StringBuilder(model.email).append(" (me) "))
                    viewholder.txt_user_email.setTypeface(viewholder.txt_user_email.typeface, Typeface.ITALIC)
                } else {
                    viewholder.txt_user_email.setText(StringBuilder(model.email))
                }
                viewholder.setiRecyclerItemClickListener(object : IRecyclerItemClickListener {
                    override fun onItemClickListener(view: View, position: Int) {

                    }
                })
            }

        }
        searchAdapter?.startListening()
        recycler_all_people.adapter = searchAdapter

    }
}
