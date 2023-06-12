package my.project.growmore.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.project.growmore.activities.TaskListActivity
import my.project.growmore.databinding.ItemCardBinding
import my.project.growmore.models.Card
import my.project.growmore.models.SelectedMembers

open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(model.labelColor.isNotEmpty()) {
                holder.labelColor.visibility = View.VISIBLE
                holder.labelColor.setBackgroundColor(Color.parseColor(model.labelColor))
            }   else {
                holder.labelColor.visibility = View.GONE
            }
            holder.tvCardName.text = model.name

            if((context as TaskListActivity).mAssignedMembersDetailList.size > 0) {
                val selectedMemberList: ArrayList<SelectedMembers> = ArrayList()
                for(i in context.mAssignedMembersDetailList.indices) {
                    for(j in model.assignedTo) {
                        if(context.mAssignedMembersDetailList[i].id == j) {
                            val selectedMembers = SelectedMembers(
                                context.mAssignedMembersDetailList[i].id,
                                context.mAssignedMembersDetailList[i].image
                            )
                            selectedMemberList.add(selectedMembers)
                        }
                    }
                }
                if(selectedMemberList.size > 0) {
                    if(selectedMemberList.size == 1 && selectedMemberList[0].id == model.createdBy) {
                        holder.rvCardSelectedMemberList.visibility = View.GONE
                    } else {
                        holder.rvCardSelectedMemberList.visibility = View.VISIBLE

                        holder.rvCardSelectedMemberList.layoutManager =
                            GridLayoutManager(context, 4)
                        val adapter = CardMemberListAdapter(context, selectedMemberList, false)
                        holder.rvCardSelectedMemberList.adapter = adapter
                        adapter.setOnClickListener(
                            object: CardMemberListAdapter.OnClickListener {
                                override fun onClick() {
                                    if(onClickListener != null){
                                        onClickListener!!.onClick(position)
                                    }
                                }
                            }
                        )
                    }
                }   else {
                    holder.rvCardSelectedMemberList.visibility = View.GONE
                }
            }

            holder.itemView.setOnClickListener {
                if(onClickListener != null) {
                    onClickListener!!.onClick(position)
                }
            }
        }
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    interface OnClickListener{
        fun onClick(position: Int)
    }
    class MyViewHolder(binding: ItemCardBinding): RecyclerView.ViewHolder(binding.root){
        val tvCardName = binding.tvCardName
        val tvMemberName = binding.tvMemberName
        val labelColor = binding.viewLabelColor
        val rvCardSelectedMemberList = binding.rvCardSelectedMembersList
    }
}