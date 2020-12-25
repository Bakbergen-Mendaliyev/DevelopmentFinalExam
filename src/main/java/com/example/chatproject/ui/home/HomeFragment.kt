package com.example.chatproject.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatproject.*
import com.example.chatproject.RecycleView.item.PersoonItem
import com.example.chatproject.util.FirestoreUtil
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.support.v4.startActivity

import com.example.chatproject.AppConstants
import com.example.chatproject.Chat

import com.example.chatproject.R



import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
  private lateinit var userListenerRegistration: ListenerRegistration

  private var shouldInitRecycleView = true

  private lateinit var peopleSelection: Section


  private lateinit var homeViewModel: HomeViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    userListenerRegistration = FirestoreUtil.addUsersListener(this.requireActivity(), this::updateRecyclerView)

    homeViewModel =
    ViewModelProviders.of(this).get(HomeViewModel::class.java)
    //val root = inflater.inflate(R.layout.fragment_home, container, false)
    //}
    return inflater.inflate(R.layout.fragment_home, container, false)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    FirestoreUtil.removeListener(userListenerRegistration)
    shouldInitRecycleView = true
  }

private fun updateRecyclerView(items: List<Item>){

  fun init(){
  recycler_view_people.apply {
    layoutManager = LinearLayoutManager(this@HomeFragment.context)
    adapter = GroupAdapter<ViewHolder>().apply {
      peopleSelection = Section(items)
      add(peopleSelection)
      setOnItemClickListener(onItemClick)
    }
  }
    shouldInitRecycleView = false
  }

  fun updateItems() = peopleSelection.update(items)

  if(shouldInitRecycleView)
    init()
  else
    updateItems()
}

  private val onItemClick = OnItemClickListener {item, view ->
    if(item is PersoonItem){
      startActivity<Chat>(AppConstants.USER_NAME to item.person.name,
      AppConstants.USER_ID to item.userId)
    }

  }
}
