package my.project.growmore.activities

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import my.project.growmore.R
import my.project.growmore.databinding.ActivityBaseBinding

open class BaseActivity : AppCompatActivity() {
    private var binding: ActivityBaseBinding? = null
    var doubleBackPressToExit = false
    lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.activity_base)
        binding?.progressBarTEXT?.text = text
        mProgressDialog.show()
    }


    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun getCurrentUserId(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        if(doubleBackPressToExit){
            super.onBackPressed()
            return
        }

        this.doubleBackPressToExit = true
        Toast.makeText(this@BaseActivity,
            "please press back again to exit",
            Toast.LENGTH_SHORT).show()

        Handler().postDelayed({
            doubleBackPressToExit = false
        }, 2000)
    }

    fun showError(message: String){
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this@BaseActivity,
                R.color.p1
            )
        )
        snackBar.show()
    }

    fun showToast(message: String, that: Context){
        Toast.makeText(that, message, Toast.LENGTH_SHORT).show()
    }



    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}