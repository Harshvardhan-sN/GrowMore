package my.project.growmore.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import my.project.growmore.databinding.ActivitySplashBinding
import my.project.growmore.firebase.FireStoreClass

class SplashActivity : AppCompatActivity() {

    private var binding: ActivitySplashBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeFace: Typeface = Typeface.createFromAsset(assets, "Marina.ttf")

        binding?.tvSplashScreenTextView?.typeface = typeFace

        Handler().postDelayed({
            val currentUserId = FireStoreClass().getCurrentUUID()
            if(currentUserId.isNotEmpty()){
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(this@SplashActivity, IntroActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 1000)

    }

}