package my.project.growmore.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.android.gms.common.internal.FallbackServiceBroker
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import my.project.growmore.R
import my.project.growmore.databinding.ActivityProfileBinding
import my.project.growmore.firebase.FireStoreClass
import my.project.growmore.models.User
import my.project.growmore.utils.Constants
import java.io.IOException

class ProfileActivity : BaseActivity() {
    private var binding: ActivityProfileBinding? = null
    private var selectedImageUri: Uri? = null
    private var profileImageUri: String = ""
    private lateinit var userDetails: User
    companion object{
        private const val GALLERY = 1
        private const val PICK_IMAGE_REQ = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.customToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Profile"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        binding?.customToolBar?.setNavigationOnClickListener {
            onBackPressed()
        }

        FireStoreClass().loadUserData(this)
        binding?.profileImage?.setOnClickListener {
            choosePhotoFromGallery()
        }
        binding?.profileUpdateBtn?.setOnClickListener {
            if(selectedImageUri != null) {
                uploadUserImage()
            }
            else{
                showCustomProgressDialog("Please Wait")
                updateProfileData()
            }
        }
    }

    private fun updateProfileData(){
        val userHashMap = HashMap<String, Any>()
        var isChanged = false
        if(profileImageUri.isNotEmpty() && profileImageUri!=userDetails.image){
            userHashMap[Constants.IMAGE] =
                profileImageUri
            isChanged = true
        }
        if(binding?.profileName?.text.toString() != userDetails.name){
            userHashMap[Constants.NAME] =
                binding?.profileName?.text.toString()
            isChanged = true
        }
        if(binding?.profilePhoneNumber?.text.toString() != userDetails.phoneNumber.toString()){
            userHashMap[Constants.PHONENUMBER] =
                binding?.profilePhoneNumber?.text.toString().toLong()
            isChanged = true
        }
        // If there is a change in current data
        if(isChanged)
            FireStoreClass().updateProfileData(this, userHashMap)
        // else we will end the activity
        else {
            hideProgressDialog()
            finish()
        }
    }

    private fun uploadUserImage(){
        showCustomProgressDialog("Please Wait")
        if(selectedImageUri != null){
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "User Image" + System.currentTimeMillis()
                        + "." + getFileExtension(selectedImageUri!!))
            sRef.putFile(selectedImageUri!!).addOnSuccessListener {
                    taskSnapshot ->
                Log.e(
                    "binod",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    profileImageUri = uri.toString()
                    updateProfileData()
                }
            }.addOnFailureListener{
                    exception ->
                showToast("${exception.message}", this@ProfileActivity)
                hideProgressDialog()
            }
        }
    }

    fun getUserDataForProfile(user: User){
        userDetails = user
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_holder)
            .into(binding!!.profileImage)
        binding?.profileName?.setText(user.name)
        binding?.profileEmail?.setText(user.email)
        if(user.phoneNumber != 0L){
            binding?.profilePhoneNumber?.setText(user.phoneNumber.toString())
        }
    }


    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_MEDIA_IMAGES
        ).withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, ProfileActivity.GALLERY)
                }
            }
            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == ProfileActivity.GALLERY){
                if(data != null){
                    val contentUri = data.data
                    selectedImageUri = contentUri!!
                    try{
                        setImageToProfile(contentUri)
                    }   catch (e : IOException){
                        e.printStackTrace()
                        showToast("ajaabsdk", this)
                    }
                }
            }
        }
    }
    private fun setImageToProfile(uri: Uri){
        Glide
            .with(this)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.ic_user_holder)
            .into(binding!!.profileImage)
    }

    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage(
            "It looks like you have turned off the permissions required by the app")
            .setPositiveButton("Go To Settings") {
                    _,_ ->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("Package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }   catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }.show()
    }



    private fun getFileExtension(uri: Uri?): String?{
        return MimeTypeMap.getSingleton().
             getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}