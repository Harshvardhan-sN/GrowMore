package my.project.growmore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import my.project.growmore.R
import my.project.growmore.databinding.ItemBoardBinding
import my.project.growmore.models.Board

open class BoardItemsAdapter(private val context: Context,
                             private var list: ArrayList<Board>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            Glide.with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.team_image)
                .into(holder.boardImage)

            holder.textName.text = model.name
            holder.textCreatedBy.text = "Created by: ${model.createdBy}"

            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, model: Board)
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    class MyViewHolder(binding: ItemBoardBinding): RecyclerView.ViewHolder(binding.root){
        val boardImage = binding.myBoardImage
        val textName = binding.tvName
        val textCreatedBy = binding.ivCreatedBy
    }

}