package my.project.growmore.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import my.project.growmore.R
import my.project.growmore.adapters.CardMemberListAdapter
import my.project.growmore.databinding.ActivityCardDetailsBinding
import my.project.growmore.dialogs.ItemAddMemberDialog
import my.project.growmore.dialogs.LabelColorListDialog
import my.project.growmore.firebase.FireStoreClass
import my.project.growmore.models.Board
import my.project.growmore.models.Card
import my.project.growmore.models.SelectedMembers
import my.project.growmore.models.Task
import my.project.growmore.models.User
import my.project.growmore.utils.Constants

class CardDetailsActivity : BaseActivity() {
    private var binding: ActivityCardDetailsBinding? = null

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailedList: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()
        setUpActionBar()

        binding?.etNameCardDetails?.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if(mSelectedColor.isNotEmpty())
            setColor()
        binding?.btnUpdateCardDetails?.setOnClickListener {
            if(binding?.etNameCardDetails?.text?.isNotBlank()!! &&
                binding?.etNameCardDetails?.text?.isNotEmpty()!!) {
                updateCardDetails()
            } else {
                showToast("Please enter a card name.", this@CardDetailsActivity)
            }
        }
        binding?.tvSelectLabelColor?.setOnClickListener {
            labelColorListDialog()
        }
        binding?.tvSelectMembers?.setOnClickListener {
            itemAddMemberListDialog()
        }
        setUpSelectedMemberList()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_del_card -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun getIntentData() {
        if(intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(
                Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(
                Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(
                Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMembersDetailedList = intent.getParcelableArrayListExtra(
                Constants.BOARD_MEMBERS_LIST)!!
        }
    }
    private fun setUpActionBar() {
        setSupportActionBar(binding?.toolbarCardDetailsActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        supportActionBar?.title = mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardPosition]
            .name
        binding?.toolbarCardDetailsActivity?.setNavigationOnClickListener{ onBackPressed() }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateCardDetails() {
        val card = Card(
            binding?.etNameCardDetails?.text?.toString()!!,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun deleteCard() {
        val cardList: ArrayList<Card> = mBoardDetails
            .taskList[mTaskListPosition].cards
        cardList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskListPosition].cards = cardList

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }


    private fun alertDialogForDeleteCard(title: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete card $title ?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun colorList(): ArrayList<String> {
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#FC4C02")
        colorsList.add("#005066")
        colorsList.add("#006400")
        colorsList.add("#a4161a")
        colorsList.add("#3A136F")
        colorsList.add("#191970")
        colorsList.add("#13232c")
        return colorsList
    }

    private fun setColor() {
        binding?.tvSelectLabelColor?.text = ""
        binding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorListDialog() {
        val colorsList: ArrayList<String> = colorList()
        val listDialog = object : LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }
        listDialog.show()
    }

    private fun itemAddMemberListDialog() {
        var cardAssignedMemberList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
        if(cardAssignedMemberList.size > 0) {
            for(i in mMembersDetailedList.indices) {
                for(j in cardAssignedMemberList) {
                    if(mMembersDetailedList[i].id == j) {
                        mMembersDetailedList[i].selected = true
                    }
                }
            }
        } else {
            for(i in mMembersDetailedList) {
                i.selected = false
            }
        }

        val listDialog = object : ItemAddMemberDialog(
            this@CardDetailsActivity,
            mMembersDetailedList,
            resources.getString(R.string.str_select_member)) {
            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT) {
                    if(!mBoardDetails
                            .taskList[mTaskListPosition]
                            .cards[mCardPosition].assignedTo.contains(user.id)) {
                        mBoardDetails
                            .taskList[mTaskListPosition]
                            .cards[mCardPosition].assignedTo.add(user.id)
                    }
                } else {
                    mBoardDetails
                        .taskList[mTaskListPosition]
                        .cards[mCardPosition].assignedTo.remove(user.id)

                    for(i in mMembersDetailedList) {
                        if(i.id == user.id) {
                            i.selected = false
                        }
                    }
                }
                setUpSelectedMemberList()
            }
        }
        listDialog.show()
    }

    private fun setUpSelectedMemberList() {
        val cardAssignedMemberList =
            mBoardDetails
                .taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        val selectedMemberList: ArrayList<SelectedMembers> = ArrayList()
        for(i in mMembersDetailedList.indices) {
            for(j in cardAssignedMemberList) {
                if(mMembersDetailedList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        mMembersDetailedList[i].id,
                        mMembersDetailedList[i].image
                    )
                    selectedMemberList.add(selectedMember)
                }
            }
        }

        if(selectedMemberList.size > 0) {
            selectedMemberList.add(SelectedMembers("",""))
            binding?.tvSelectMembers?.visibility = View.GONE
            binding?.rvSelectedMembersList?.visibility = View.VISIBLE

            binding?.rvSelectedMembersList?.layoutManager = GridLayoutManager(
                this@CardDetailsActivity, 6
            )
            val adapter = CardMemberListAdapter(
                this@CardDetailsActivity, selectedMemberList)
            binding?.rvSelectedMembersList?.adapter = adapter
            adapter.setOnClickListener(
                object: CardMemberListAdapter.OnClickListener {
                    override fun onClick() {
                        itemAddMemberListDialog()
                    }
                }
            )
        } else {
            binding?.tvSelectMembers?.visibility = View.VISIBLE
            binding?.rvSelectedMembersList?.visibility = View.GONE
        }
    }
}