package my.project.growmore.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import my.project.growmore.R
import my.project.growmore.databinding.ActivitySignInBinding
import my.project.growmore.firebase.FireStoreClass
import my.project.growmore.models.User

class SignInActivity : BaseActivity() {
    private var binding: ActivitySignInBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setSupportActionBar(binding?.customToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "SIGN IN"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        binding?.customToolBar?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnSignInMain?.setOnClickListener {
            binding?.btnProgressBar?.visibility = View.VISIBLE
            binding?.btnTextView?.visibility = View.GONE
            signInAuth()
        }
    }

    private fun signInAuth(){
        val okCredentials = validateForm()
        if(okCredentials.isBlank()){
            val email = binding?.emailSignInMain?.text.toString().trim{ it<=' '}.toLowerCase()
            val password = binding?.passwordSignInMain?.text.toString().trim{ it<=' '}
            //showCustomProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    FireStoreClass().loadUserData(this)
                }
                else{
                    //hideProgressDialog()
                    binding?.btnProgressBar?.visibility = View.GONE
                    binding?.btnTextView?.visibility = View.VISIBLE
                    showError(task.exception!!.message!!)
                }
            }
        }
        else{
            binding?.btnProgressBar?.visibility = View.GONE
            binding?.btnTextView?.visibility = View.VISIBLE
            showError(okCredentials)
        }
    }

    private fun validateForm(): String{
        var lis: String = ""
        if(binding?.emailSignInMain?.text.toString().isEmpty())    lis = "Email"
        else if(binding?.passwordSignInMain?.text.toString().isEmpty())     lis = "Password"
        return lis
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun signInSuccess(loggedInUser: User){
        //hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}