package my.project.growmore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import my.project.growmore.R
import my.project.growmore.databinding.ItemBoardBinding
import my.project.growmore.databinding.ItemMemberBinding
import my.project.growmore.models.User
import my.project.growmore.utils.Constants

open class MemberListAdapter (
    private val context: Context,
    private val list: ArrayList<User>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder) {
            Glide.with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_holder)
                .into(holder.ivMemberImage)
            holder.tvMemberName.text = model.name
            holder.tvMemberEmail.text = model.email

            if(model.selected) {
                holder.ivSelectedMember.visibility = View.VISIBLE
            }   else {
                holder.ivSelectedMember.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if(onClickListener != null) {
                    if(model.selected) {
                        onClickListener!!.onClick(position, model, Constants.UNSELECT)
                    } else {
                        onClickListener!!.onClick(position, model, Constants.SELECT)
                    }
                }
            }
        }
    }

    class MyViewHolder(binding: ItemMemberBinding): RecyclerView.ViewHolder(binding.root){
        val ivMemberImage = binding.ivMemberImage
        val tvMemberName = binding.tvMemberName
        val tvMemberEmail = binding.tvMemberEmail
        val ivSelectedMember = binding.ivSelectedMember
    }

    interface OnClickListener {
        fun onClick(position: Int, user: User, action: String)
    }
}