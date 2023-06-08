package my.project.growmore.adapters

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import my.project.growmore.databinding.ItemLabelColorBinding
import my.project.growmore.databinding.ItemMemberBinding

class LabelColorListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<String>,
    private val mSelectedColor: String
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemLabelColorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if(holder is MyViewHolder) {
            holder.viewMain.setBackgroundColor(Color.parseColor(item))
            if(item == mSelectedColor) {
                holder.ivSelectedColor.visibility = View.VISIBLE
            }   else {
                holder.ivSelectedColor.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                if(onItemClickListener != null) {
                    onItemClickListener!!.onClick(position, item)
                }
            }
        }
    }

    class MyViewHolder(binding: ItemLabelColorBinding): RecyclerView.ViewHolder(binding.root){
        val viewMain = binding.viewMain
        val ivSelectedColor = binding.ivSelectedColor
    }

    interface OnItemClickListener {
        fun onClick(position: Int, color: String)
    }
}