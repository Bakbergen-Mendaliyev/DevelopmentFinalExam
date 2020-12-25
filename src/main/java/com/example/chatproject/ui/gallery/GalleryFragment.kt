package com.example.chatproject.ui.gallery

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.chatproject.R
import com.example.chatproject.glide.GlideApp
import com.example.chatproject.ui.home.HomeViewModel
import com.example.chatproject.util.FirestoreUtil
import com.example.chatproject.util.StorageUtil
import kotlinx.android.synthetic.main.fragment_gallery.*
import java.io.ByteArrayOutputStream

class GalleryFragment : Fragment() {

  private lateinit var galleryViewModel: GalleryViewModel

  private val RC_SELECT_IMAGE = 2
  private lateinit var selectedImageBytes: ByteArray
  private var pictureJustChanged = false


  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? { galleryViewModel =
    ViewModelProviders.of(this).get(GalleryViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_gallery, container, false)
    val textView: TextView = root.findViewById(R.id.text_gallery)
    val imageView2: ImageView = root.findViewById(R.id.imageView2)
    val btn_save: Button = root.findViewById(R.id.btn_save)
    val text1: TextView = root.findViewById(R.id.textView19)
    val text2: TextView = root.findViewById(R.id.textView20)
    val text3: TextView = root.findViewById(R.id.textView22)
    val editText1: EditText = root.findViewById(R.id.editTextTextPersonName5)
    val editText2: EditText = root.findViewById(R.id.editTextTextPersonName7)
    galleryViewModel.text.observe(viewLifecycleOwner, Observer {
      textView.text = it
    })

    view.apply {
      imageView2.setOnClickListener {
        val intent = Intent().apply {
          type = "image/*"
          action = Intent.ACTION_GET_CONTENT
          putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        }
        startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
      }
      btn_save.setOnClickListener {
        if (::selectedImageBytes.isInitialized)
          StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
            FirestoreUtil.updateCurrentUser(
              editTextTextPersonName5.text.toString(),
              editTextTextPersonName7.text.toString(), imagePath
            )
          }
        else
          FirestoreUtil.updateCurrentUser(
            editTextTextPersonName5.text.toString(),
            editTextTextPersonName7.text.toString(), null
          )
      }

    }
    return root
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
           data != null && data.data != null ){
      val selectedImagePath = data.data
      val selectedImageBmp = MediaStore.Images.Media
        .getBitmap(activity?.contentResolver,selectedImagePath)

      val outputStream = ByteArrayOutputStream()
      selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
      selectedImageBytes = outputStream.toByteArray()

      GlideApp.with(this)
        .load(selectedImageBytes)
        .into(imageView2)

       pictureJustChanged = true
    }
  }

  override fun onStart() {
    super.onStart()
    FirestoreUtil.getCurrentUser { user ->
      if (this@GalleryFragment.isVisible){
        editTextTextPersonName5.setText(user.name)
        editTextTextPersonName7.setText(user.bio)
       if(!pictureJustChanged && user.profilePicturePath != null)
          GlideApp.with(this)
            .load(StorageUtil.pathToReference(user.profilePicturePath))
            .placeholder(R.drawable.avatar)
            .into(imageView2)
      }
    }
  }
}