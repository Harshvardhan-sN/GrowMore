package my.project.growmore.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import my.project.growmore.R
import my.project.growmore.databinding.ActivityMainBinding
import my.project.growmore.databinding.NavHeaderMainBinding
import my.project.growmore.firebase.FireStoreClass
import my.project.growmore.models.User

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        const val MY_PROFILE_REQ_CODE: Int = 101
    }

    private var binding: ActivityMainBinding? = null
    private var navViewHeaderBinding: NavHeaderMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val viewHolder = binding?.navView?.getHeaderView(0)
        navViewHeaderBinding = NavHeaderMainBinding.bind(viewHolder!!)

        setUpActionBar()
        binding?.navView?.setNavigationItemSelectedListener(this)

        FireStoreClass().loadUserData(this)

        binding?.includeAppBarLayout?.forMainContentAccess?.btnFloating?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding?.includeAppBarLayout?.toolbarMainActivity)
        binding?.includeAppBarLayout?.toolbarMainActivity?.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        binding?.includeAppBarLayout?.toolbarMainActivity?.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode==MY_PROFILE_REQ_CODE){
            FireStoreClass().loadUserData(this)
        }else{
            Log.e("Cancelled","Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile ->{
                val intent = Intent(this, ProfileActivity::class.java)
                startActivityForResult(intent, MY_PROFILE_REQ_CODE)
            }
            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }



    fun updateNavigationUserDetails(user: User){
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_holder)
            .into(navViewHeaderBinding!!.navUserImage)
        navViewHeaderBinding?.tvUsernameNav?.text = user.name
    }


    private fun toggleDrawer(){
        if(binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }
        else{
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }
        else{
            doubleBackToExit()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}