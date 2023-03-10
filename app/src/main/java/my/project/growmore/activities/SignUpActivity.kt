package my.project.growmore.activities

import android.os.Bundle
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import my.project.growmore.R
import my.project.growmore.databinding.ActivitySignUpBinding
import my.project.growmore.firebase.FireStoreClass
import my.project.growmore.models.User

class SignUpActivity : BaseActivity() {
    private var binding: ActivitySignUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setSupportActionBar(binding?.customToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "SIGN UP"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        binding?.customToolBar?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnSignUpMain?.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser(){
        val okCredentials = validateForm()
        if(okCredentials.isBlank()){
            val name = binding?.signUpName?.text.toString().trim{ it<=' '}
            val email = binding?.signUpEmail?.text.toString().trim{ it<=' '}
            val password = binding?.signUpPassword?.text.toString().trim{ it<=' '}

            showCustomProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        //val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, email)
                        FireStoreClass().registerUser(this, user)
                    }
                    else{
                        hideProgressDialog()
                        showError(task.exception!!.message!!)
                    }
                }
        }
        else{
            showError(okCredentials)
        }
    }

    private fun validateForm(): String{
        var lis: String = ""
        if(binding?.signUpName?.text.toString().isEmpty())  lis = "Name"
        else if(binding?.signUpEmail?.text.toString().isEmpty())    lis = "Email"
        else if(binding?.signUpPassword?.text.toString().isEmpty())     lis = "Password"
        return lis
    }

    fun userRegisteredSuccess(){
        showToast("You have successfully registered!!", this)
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}