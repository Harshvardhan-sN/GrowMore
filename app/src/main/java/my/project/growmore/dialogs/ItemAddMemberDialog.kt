package my.project.growmore.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import my.project.growmore.R
import my.project.growmore.adapters.ItemAddMemberAdapter
import my.project.growmore.adapters.LabelColorListItemsAdapter
import my.project.growmore.databinding.DialogListBinding
import my.project.growmore.models.User

abstract class ItemAddMemberDialog(
    context: Context,
    private var list: ArrayList<User>,
    private val title: String = ""
): Dialog(context) {

    private lateinit var binding: DialogListBinding

    private var adapter: ItemAddMemberAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogListBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        binding.mainLayoutId.setBackgroundColor(Color.WHITE)
        setUpRecyclerView()
    }
    private fun setUpRecyclerView() {
        binding.tvTitle.text = title
        if(list.size > 0) {
            binding.rvList.layoutManager = LinearLayoutManager(context)
            adapter = ItemAddMemberAdapter(context, list)
            binding.rvList.adapter = adapter
            adapter!!.setOnClickListener(object :
            ItemAddMemberAdapter.OnClickListener {
                override fun onClick(position: Int, user: User, action: String) {
                    dismiss()
                    onItemSelected(user, action )
                }
            })
        }
    }

    protected abstract fun onItemSelected(user: User, action: String)

}