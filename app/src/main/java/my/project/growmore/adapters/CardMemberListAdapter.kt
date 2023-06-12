package my.project.growmore.adapters;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import my.project.growmore.R
import my.project.growmore.databinding.ItemCardSelectedMemberBinding
import my.project.growmore.databinding.ItemMemberBinding
import my.project.growmore.models.SelectedMembers

open class CardMemberListAdapter(
        private val context: Context,
        private val list: ArrayList<SelectedMembers>,
        private val assignMembers: Boolean)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        private var onClickListener: OnClickListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return MyViewHolder(
                        ItemCardSelectedMemberBinding.inflate(
                                LayoutInflater.from(
                                        parent.context
                                ), parent, false
                        ))
        }

        override fun getItemCount(): Int {
                return list.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val model = list[position]
                if(holder is MyViewHolder) {
                        if(position == list.size - 1 && assignMembers) {
                                holder.ivAddMember.visibility = View.VISIBLE
                                holder.ivSelectedMemberImage.visibility = View.GONE
                        } else {
                                holder.ivAddMember.visibility = View.GONE
                                holder.ivSelectedMemberImage.visibility = View.VISIBLE

                                Glide
                                        .with(context)
                                        .load(model.image)
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_user_holder)
                                        .into(holder.ivSelectedMemberImage)
                        }

                        holder.itemView.setOnClickListener {
                                if(onClickListener != null) {
                                        onClickListener!!.onClick()
                                }
                        }
                }
        }

        fun setOnClickListener(onClickListener: OnClickListener) {
                this.onClickListener = onClickListener
        }

        interface OnClickListener {
                fun onClick()
        }

        class MyViewHolder(binding: ItemCardSelectedMemberBinding): RecyclerView.ViewHolder(binding.root) {
                val ivAddMember = binding.ivAddMember
                val ivSelectedMemberImage = binding.ivSelectedMemberImage
        }
}
