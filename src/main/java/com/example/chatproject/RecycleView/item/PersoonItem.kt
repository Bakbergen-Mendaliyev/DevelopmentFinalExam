package com.example.chatproject.RecycleView.item

import android.content.Context
import com.example.chatproject.R
import com.example.chatproject.glide.GlideApp
import com.example.chatproject.model.User
import com.example.chatproject.util.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.*

class PersoonItem(val person: User, val userId: String, private val context:Context) : Item(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_name.text = person.name
        viewHolder.textView_bio.text = person.bio
        if (person.profilePicturePath != null)
            GlideApp.with(context).load(StorageUtil.pathToReference(person.profilePicturePath))
                .placeholder(R.drawable.avatar)
                .into(viewHolder.imageView_profile_picture)
    }

    override fun getLayout() = R.layout.item_person


}