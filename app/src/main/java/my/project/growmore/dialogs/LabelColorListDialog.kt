package my.project.growmore.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import my.project.growmore.R
import my.project.growmore.adapters.LabelColorListItemsAdapter
import my.project.growmore.databinding.DialogListBinding

abstract class LabelColorListDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private var mSelectedColor: String = ""
): Dialog(context) {

    private lateinit var binding: DialogListBinding

    private var adapter: LabelColorListItemsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogListBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView()
    }
    private fun setUpRecyclerView() {
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        binding.rvList.adapter = adapter

        adapter!!.onItemClickListener =
            object : LabelColorListItemsAdapter.OnItemClickListener {
                override fun onClick(position: Int, color: String) {
                    dismiss()
                    onItemSelected(color)

                }
        }
    }

    protected abstract fun onItemSelected(color: String)

}