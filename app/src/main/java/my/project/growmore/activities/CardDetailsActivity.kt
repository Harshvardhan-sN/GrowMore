package my.project.growmore.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import my.project.growmore.R
import my.project.growmore.databinding.ActivityCardDetailsBinding
import my.project.growmore.models.Board
import my.project.growmore.utils.Constants

class CardDetailsActivity : AppCompatActivity() {
    private var binding: ActivityCardDetailsBinding? = null

    private lateinit var mBoardDetails: Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        getIntentData()
        setUpActionBar()

    }


    private fun getIntentData() {
        if(intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(
                Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(
                Constants.CARD_LIST_ITEM_POSITION, -1)
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
}