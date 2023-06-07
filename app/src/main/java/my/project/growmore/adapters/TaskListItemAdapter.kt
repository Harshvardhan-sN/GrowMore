package my.project.growmore.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import my.project.growmore.R
import my.project.growmore.activities.TaskListActivity
import my.project.growmore.databinding.TaskListBinding
import my.project.growmore.models.Task

open class TaskListItemAdapter(private val context: Context,
                               private var list: ArrayList<Task>):
    RecyclerView.Adapter<TaskListItemAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_list, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.8).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(
            (15.toDp().toPx()), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams
        val binding = TaskListBinding.bind(view)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if(position == list.size - 1){
            holder.tvAddTaskList.visibility = View.VISIBLE
            holder.llTaskItem.visibility = View.GONE
        } else {
            holder.tvAddTaskList.visibility = View.GONE
            holder.llTaskItem.visibility = View.VISIBLE
        }
        holder.tvTaskListTitle.text = model.title
        holder.tvAddTaskListBtn.setOnClickListener {
            holder.tvAddTaskList.visibility = View.GONE
            holder.cvAddTaskListName.visibility = View.VISIBLE
        }
        holder.ibCloseWritingListNameBtn.setOnClickListener {
            holder.tvAddTaskList.visibility = View.VISIBLE
            holder.cvAddTaskListName.visibility = View.GONE
            holder.etTaskListName.text.clear()
        }
        holder.ibDoneWritingListNameBtn.setOnClickListener{
            val listName = holder.etTaskListName.text.toString()
            if(listName.isNotEmpty() and listName.isNotBlank()){
                if(context is TaskListActivity){
                    context.createTaskList(listName)
                }
            } else {
                Toast.makeText(context, "Please Enter a List Name", Toast.LENGTH_SHORT).show()
            }
        }
        holder.ibEditListName.setOnClickListener{
            holder.etEditTaskListName.setText(model.title)
            holder.llTitleView.visibility = View.GONE
            holder.cvEditTaskListName.visibility = View.VISIBLE
        }
        holder.ibCloseEditableView.setOnClickListener {
            holder.llTitleView.visibility = View.VISIBLE
            holder.cvEditTaskListName.visibility = View.GONE
            holder.etEditTaskListName.text.clear()
        }
        holder.ibDoneEditListName.setOnClickListener {
            val listName = holder.etEditTaskListName.text.toString()
            if(listName.isNotEmpty() and listName.isNotBlank()){
                if(context is TaskListActivity){
                    context.updateTaskList(position, listName, model)
                }
            } else {
                Toast.makeText(context, "Please Enter a List Name", Toast.LENGTH_SHORT).show()
            }
        }
        holder.ibDeleteList.setOnClickListener {
            alertDialogForDeleteList(position, model.title)
        }

        holder.tvAddCard.setOnClickListener {
            holder.cvAddCard.visibility = View.VISIBLE
            holder.tvAddCard.visibility = View.GONE
        }
        holder.ibCloseCardName.setOnClickListener{
            holder.cvAddCard.visibility = View.GONE
            holder.tvAddCard.visibility = View.VISIBLE
            holder.etCardName.text.clear()
        }
        holder.ibDoneCardName.setOnClickListener{
            val cardName = holder.etCardName.text.toString()
            if(cardName.isNotEmpty() and cardName.isNotBlank()){
                if(context is TaskListActivity){
                    context.addCardToTaskList(position, cardName)
                }
            } else {
                Toast.makeText(context, "Please Enter a Card Name", Toast.LENGTH_SHORT).show()
            }
        }

        holder.rvCardList.layoutManager = LinearLayoutManager(context)
        holder.rvCardList.setHasFixedSize(true)

        val adapter = CardListItemsAdapter(context, model.cards)
        holder.rvCardList.adapter = adapter

        adapter.setOnClickListener(
            object: CardListItemsAdapter.OnClickListener {
                override fun onClick(cardPosition: Int) {
                    if(context is TaskListActivity) {
                        context.cardDetails(holder.adapterPosition, cardPosition)
                    }
                }
            }
        )
    }

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") {dialogInterface, which ->
            dialogInterface.dismiss()
            if(context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No") {dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    // Get Density of Screen and Convert it into Integer.. so that we can see how big the screen is
    // and we change it accordingly
    // Density Pixel to Density Pixel
    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()
    // Pixel from Density Pixel
    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(binding: TaskListBinding): RecyclerView.ViewHolder(binding.root){
        val tvAddTaskList = binding.tvAddTaskList
        val llTaskItem = binding.llTaskItem
        val tvTaskListTitle = binding.tvTaskListTitle
        val tvAddTaskListBtn = binding.tvAddTaskList
        val cvAddTaskListName = binding.cvAddTaskListName
        val ibDoneWritingListNameBtn = binding.ibDoneListName
        val ibCloseWritingListNameBtn = binding.ibCloseListName
        val etTaskListName = binding.etTaskListName
        val ibEditListName = binding.ibEditListName
        val ibDeleteList = binding.ibDeleteList
        val llTitleView = binding.llTitleView
        val etEditTaskListName = binding.etEditTaskListName
        val cvEditTaskListName = binding.cvEditTaskListName
        val ibCloseEditableView = binding.ibCloseEditableView
        val ibDoneEditListName = binding.ibDoneEditListName
        val tvAddCard = binding.tvAddCard
        val cvAddCard = binding.cvAddCard
        val ibCloseCardName = binding.ibCloseCardName
        val etCardName = binding.etCardName
        val ibDoneCardName = binding.ibDoneCardName
        val rvCardList = binding.rvCardList
    }
}