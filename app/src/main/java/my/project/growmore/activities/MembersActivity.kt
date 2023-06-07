package my.project.growmore.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import my.project.growmore.R
import my.project.growmore.adapters.MemberListAdapter
import my.project.growmore.databinding.ActivityMembersBinding
import my.project.growmore.databinding.CustomDialogBinding
import my.project.growmore.firebase.FireStoreClass
import my.project.growmore.models.Board
import my.project.growmore.models.User
import my.project.growmore.utils.Constants

class MembersActivity : BaseActivity() {
    private var binding: ActivityMembersBinding? = null
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesMade: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }
        setUpActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getAssignedMemberListDetails(this, mBoardDetails.assignedTo)
    }

    fun setUpMemberList(list: ArrayList<User>) {
        mAssignedMembersList = list
        hideProgressDialog()
        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)

        val adapter = MemberListAdapter(this, list)
        binding?.rvMembersList?.adapter = adapter
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarMembersActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        supportActionBar?.title = resources.getString(R.string.members)
        binding?.toolbarMembersActivity?.setNavigationOnClickListener{ onBackPressed() }
    }

    fun membersDetails(users: User) {
        mBoardDetails.assignedTo.add(users.id)
        FireStoreClass().assignMemberToBoard(this@MembersActivity, mBoardDetails, users)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_add_member -> {
                showCustomDialog(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCustomDialog(context: Context) {
        val dialogBinding: CustomDialogBinding = CustomDialogBinding.inflate(LayoutInflater.from(context))
        val dialogBuilder = AlertDialog.Builder(context).setView(dialogBinding.root)
        val dialog = dialogBuilder.create()
        // Set click listeners for buttons
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnOk.setOnClickListener {
            val email = dialogBinding.etEmail.text.toString().trim{ it<=' '}.toLowerCase()
            if(email.isNotEmpty() and email.isNotBlank()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FireStoreClass().getMemberDetails(this@MembersActivity, email)
            } else {
                showToast("Please Enter a Email...", this@MembersActivity)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun memberAssignSuccess(user: User) {
        hideProgressDialog()
        mAssignedMembersList.add(user)
        anyChangesMade = true
        setUpMemberList(mAssignedMembersList)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
