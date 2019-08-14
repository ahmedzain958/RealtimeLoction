package com.zainco.realtimeloction2

import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mancj.materialsearchbar.MaterialSearchBar
import com.zainco.realtimeloction2.listeners.IFirebaseLoadDone
import com.zainco.realtimeloction2.listeners.IRecyclerItemClickListener
import com.zainco.realtimeloction2.model.MyResponse
import com.zainco.realtimeloction2.model.Request
import com.zainco.realtimeloction2.model.User
import com.zainco.realtimeloction2.remote.IFCMService
import com.zainco.realtimeloction2.utils.Common
import com.zainco.realtimeloction2.viewholder.UserViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_all_people.*
import java.util.*
import kotlin.collections.ArrayList

class AllPeopleActivity : AppCompatActivity(), IFirebaseLoadDone {

    val compositeDisposable = CompositeDisposable()
    lateinit var ifcmService: IFCMService
    override fun onFirebaseLoadUsernameDone(lstEmail: List<String>) {
        suggestList = lstEmail
        material_search_bar.lastSuggestions = lstEmail
    }

    override fun onFirebaseLoadFalied(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    var adapter: FirebaseRecyclerAdapter<User, UserViewHolder>? = null
    var searchAdapter: FirebaseRecyclerAdapter<User, UserViewHolder>? = null
    lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    var suggestList = listOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_people)
        ifcmService = Common.getFCMService()
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
                //close search return default
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
                        showDialogRequest(model)
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
                if (model.email == Common.loggedUser?.email) {
                    viewholder.txt_user_email.text = StringBuilder(model.email).append(" (me) ")
                    viewholder.txt_user_email.setTypeface(viewholder.txt_user_email.typeface, Typeface.ITALIC)
                } else {
                    viewholder.txt_user_email.text = StringBuilder(model.email)
                }
                viewholder.setiRecyclerItemClickListener(object : IRecyclerItemClickListener {
                    override fun onItemClickListener(view: View, position: Int) {
                        showDialogRequest(model)
                    }
                })
            }

        }
        searchAdapter?.startListening()
        recycler_all_people.adapter = searchAdapter

    }

    private fun showDialogRequest(model: User) {
        val mDialogBuilder = AlertDialog.Builder(this, R.style.MyRequestDialog)
        mDialogBuilder.setTitle("Friend Request")
        mDialogBuilder.setMessage("Do you awant to send Friend Request to " + model.email)
        mDialogBuilder.setIcon(R.drawable.ic_account_circle_black_24dp)
        mDialogBuilder.setNegativeButton("cancel") { dialogInterface: DialogInterface, p1 ->
            dialogInterface.dismiss()
        }
        mDialogBuilder.setPositiveButton("SEND") { dialogInterface: DialogInterface, p1 ->
            val acceptList = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser!!.uid!!)
                .child(Common.ACCEPT_LIST)

            acceptList.orderByKey().equalTo(model.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapShot: DataSnapshot) {
                        if (dataSnapShot.value == null) { //not exist this model in the current user friend requests
                            sendFriendRequest(model)
                        } else {
                            Toast.makeText(
                                this@AllPeopleActivity,
                                " u and ${model.email} already are friends",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                })
        }
        val dialog = mDialogBuilder.create()
        dialog.show()
    }

    private fun sendFriendRequest(model: User) {
        val tokens = FirebaseDatabase.getInstance()
            .getReference(Common.Tokens)
        tokens.orderByKey().equalTo(model.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.value == null) { //not exist this model in the current user friend requests
                        Toast.makeText(
                            this@AllPeopleActivity,
                            "token error",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val dataSend: HashMap<String, String> = HashMap()
                        dataSend[Common.FROM_UID] = Common.loggedUser!!.uid!!
                        dataSend[Common.FROM_EMAIL] = Common.loggedUser!!.email!!
                        dataSend[Common.TO_UID] = model.uid!!
                        dataSend[Common.TO_EMAIL] = model.email!!
                        val to = dataSnapShot.child(model.uid!!).getValue(String::class.java)!!
                        val request = Request(to, dataSend)
                        compositeDisposable.add(
                            ifcmService.sendFriendrequestToUser(request)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({ myResponse: MyResponse ->
                                    if (myResponse.success == 1) {
                                        Toast.makeText(
                                            this@AllPeopleActivity,
                                            "request sent succ",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }, {
                                    Toast.makeText(
                                        this@AllPeopleActivity,
                                        it.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }))
                    }
                }
            })
    }
}
