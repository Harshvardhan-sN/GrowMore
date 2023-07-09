package my.project.growmore.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import my.project.growmore.R
import my.project.growmore.adapters.BoardItemsAdapter
import my.project.growmore.databinding.ActivityMainBinding
import my.project.growmore.databinding.NavHeaderMainBinding
import my.project.growmore.firebase.FireStoreClass
import my.project.growmore.models.Board
import my.project.growmore.models.User
import my.project.growmore.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        const val MY_PROFILE_REQ_CODE: Int = 101
        const val CREATE_BOARD_REQ_CODE: Int = 201
    }

    private var binding: ActivityMainBinding? = null
    private var navViewHeaderBinding: NavHeaderMainBinding? = null

    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val viewHolder = binding?.navView?.getHeaderView(0)
        navViewHeaderBinding = NavHeaderMainBinding.bind(viewHolder!!)

        setUpActionBar()
        binding?.navView?.setNavigationItemSelectedListener(this)

        FireStoreClass().loadUserData(this, true)

        binding?.includeAppBarLayout?.btnFloating?.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, userName)
            startActivityForResult(intent, CREATE_BOARD_REQ_CODE)
        }
    }

    fun populateBoardListToUI(boardList: ArrayList<Board>){
        hideProgressDialog()
        if(boardList.size > 0){
            binding?.includeAppBarLayout?.forMainContentAccess?.recycleViewListBoard?.visibility = View.VISIBLE
            binding?.includeAppBarLayout?.forMainContentAccess?.tvDefultNoCard?.visibility = View.GONE
            binding?.includeAppBarLayout?.forMainContentAccess?.recycleViewListBoard?.layoutManager = LinearLayoutManager(this)
            binding?.includeAppBarLayout?.forMainContentAccess?.recycleViewListBoard?.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardList)
            binding?.includeAppBarLayout?.forMainContentAccess?.recycleViewListBoard?.adapter = adapter

            adapter.setOnClickListener(object: BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        }
        else{
            binding?.includeAppBarLayout?.forMainContentAccess?.recycleViewListBoard?.visibility = View.GONE
            binding?.includeAppBarLayout?.forMainContentAccess?.tvDefultNoCard?.visibility = View.VISIBLE
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
        }
        else if(resultCode==Activity.RESULT_OK && requestCode== CREATE_BOARD_REQ_CODE){
            FireStoreClass().getBoardList(this)
        }
        else{
            Log.e("Cancelled","Cancelled")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
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
        item.isCheckable = false
        return true
    }



    fun updateNavigationUserDetails(user: User, readBoardList: Boolean){
        userName = user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_holder)
            .into(navViewHeaderBinding!!.navUserImage)
        navViewHeaderBinding?.tvUsernameNav?.text = user.name
        if(readBoardList){
            showProgressDialog("")
            FireStoreClass().getBoardList(this)
        }
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