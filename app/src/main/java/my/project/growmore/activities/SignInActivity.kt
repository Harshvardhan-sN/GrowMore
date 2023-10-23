package my.project.growmore.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import my.project.growmore.R
import my.project.growmore.databinding.ActivitySignInBinding
import my.project.growmore.firebase.FireStoreClass
import my.project.growmore.models.User

class SignInActivity : BaseActivity() {
    private var binding: ActivitySignInBinding? = null
    private val RC_SIGN_IN = 123
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
        initGoogleSignIn()
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

    // Function to initialize Google Sign-In
    private fun initGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Set an OnClickListener for your Google Sign-In button
        binding?.google?.setOnClickListener {
            startGoogleSignIn(googleSignInClient)
        }
    }

    // Function to start Google Sign-In
    private fun startGoogleSignIn(googleSignInClient: GoogleSignInClient) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Function to handle the result of Google Sign-In
    private fun handleGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            // Sign in with Firebase using the Google credentials
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        onGoogleSignInSuccess()
                    } else {
                        onGoogleSignInFailure()
                    }
                }
        } catch (e: ApiException) {
            onGoogleSignInFailure()
        }
    }

    // Function to handle a successful Google Sign-In
    private fun onGoogleSignInSuccess() {
        val user = FirebaseAuth.getInstance().currentUser
        startActivity(Intent(this, MainActivity::class.java))
    }

    // Function to handle a failed Google Sign-In
    private fun onGoogleSignInFailure() {
        showError("Failed")
    }

    // Handle the result of an activity, such as Google Sign-In
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            handleGoogleSignInResult(data)
        }
    }

}