package my.project.growmore.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import java.util.Date
import java.util.Locale

class CardDetailsActivity : BaseActivity() {
    private var binding: ActivityCardDetailsBinding? = null

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailedList: ArrayList<User>
    private var mSelectedDueDate: Long = 0
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
        mSelectedDueDate = mBoardDetails
            .taskList[mTaskListPosition]
            .cards[mCardPosition].dueDate
        if(mSelectedDueDate > 0) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDate))
            binding?.tvSelectDueDate?.text = selectedDate
        }
        binding?.tvSelectDueDate?.setOnClickListener {
            showDatePicker()
        }
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
            mSelectedColor,
            mSelectedDueDate
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
        val cardAssignedMemberList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
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
                this@CardDetailsActivity, 5
            )
            val adapter = CardMemberListAdapter(
                this@CardDetailsActivity, selectedMemberList, true)
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
    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener{ _, _, monthOfYear, dayOfMonth ->
                val sDayOfMonth = if(dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                val sMonthOfYear = if((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"
                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                binding?.tvSelectDueDate?.text = selectedDate

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)
                mSelectedDueDate = theDate!!.time
            }, year, month, day
        )
        dpd.show()
    }
}