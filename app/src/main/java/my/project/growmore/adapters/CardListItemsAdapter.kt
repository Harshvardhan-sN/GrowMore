package my.project.growmore.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import my.project.growmore.databinding.ItemCardBinding
import my.project.growmore.models.Card

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(model.labelColor.isNotEmpty()) {
                holder.labelColor.visibility = View.VISIBLE
                holder.labelColor.setBackgroundColor(Color.parseColor(model.labelColor))
            }   else {
                holder.labelColor.visibility = View.GONE
            }

            holder.tvCardName.text = model.name
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
    }
}