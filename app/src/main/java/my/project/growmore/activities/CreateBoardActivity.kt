package my.project.growmore.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import my.project.growmore.R
import my.project.growmore.databinding.ActivityCreateBoardBinding
import my.project.growmore.firebase.FireStoreClass
import my.project.growmore.models.Board
import my.project.growmore.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {
    private var binding: ActivityCreateBoardBinding? = null
    private lateinit var userName: String
    private var selectedImageUri: Uri? = null
    private var mBoardImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.customToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Create Board"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)

        if(intent.hasExtra(Constants.NAME)){
            userName = intent.getStringExtra(Constants.NAME).toString()
        }
        binding?.customToolBar?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.boardImageView?.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }

        binding?.btnCreateBoard?.setOnClickListener {
            if(binding?.editBoardName?.text?.isEmpty() == false
                && binding?.editBoardName?.text?.isBlank() == false){
                binding?.btnProgressBar?.visibility = View.VISIBLE
                binding?.btnTextView?.visibility = View.GONE
                if(selectedImageUri != null)
                    uploadBoardImage()
                else{
                    //showCustomProgressDialog("Creating")
                    createBoard()
                }
            }
            else{
                showToast("Please Enter Name!!", this)
            }
        }
    }

    private val launchGalleryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == Activity.RESULT_OK) {
            selectedImageUri = it.data!!.data
            selectedImageUri?.let { it1 -> setImageToProfile(it1) }
        }
    }

    private fun createBoard(){
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserId())
        val board = Board(
            binding?.editBoardName?.text.toString(),
            mBoardImageURL,
            userName,
            assignedUserArrayList
        )

        FireStoreClass().createBoard(this, board)
    }

    private fun uploadBoardImage(){
        //showCustomProgressDialog("Creating")
        if(selectedImageUri != null){
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "Board Image" + System.currentTimeMillis()
                        + "." + Constants.getFileExtension(this, selectedImageUri!!))
            sRef.putFile(selectedImageUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.e(
                    "binod",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    mBoardImageURL = uri.toString()
                    createBoard()
                }
            }.addOnFailureListener{
                    exception ->
                showToast("${exception.message}", this@CreateBoardActivity)
                binding?.btnProgressBar?.visibility = View.GONE
                binding?.btnTextView?.visibility = View.VISIBLE
                //hideProgressDialog()
            }
        }
    }

    fun boardCreatedSuccess(){
        //hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun setImageToProfile(uri: Uri){
        Glide
            .with(this)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.ic_user_holder)
            .into(binding!!.boardImageView)
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}