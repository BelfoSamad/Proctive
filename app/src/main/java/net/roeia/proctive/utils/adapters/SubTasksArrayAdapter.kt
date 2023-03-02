package net.roeia.proctive.utils.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import net.roeia.proctive.R
import net.roeia.proctive.models.pojo.TodoChecked

class SubTasksArrayAdapter(
    private val context: Context,
    private val itemsList: MutableMap<String, Boolean>,
    private val isCheckable: Boolean,
    private val listener: SubTaskActions
) : BaseAdapter() {
    companion object {
        private const val TAG = "SubTasksArrayAdapter"
    }

    interface SubTaskActions {

        fun onDeleteSubTask(subtask: String) {
            //Nothing
        }

        fun onCheckSubTask(subtask: String, checked: Boolean) {
            //Nothing
        }

    }

    /***********************************************************************************************
     * ************************* Methods
     */
    override fun getCount() = itemsList.size

    override fun getItem(position: Int): TodoChecked =
        TodoChecked(itemsList.keys.elementAt(position), itemsList.values.elementAt(position))

    override fun getItemId(position: Int) = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val root = LayoutInflater.from(context).inflate(R.layout.list_task_item, parent, false)
        val todo = getItem(position)
        root.findViewById<TextView>(R.id.todo_content).text = todo.todo

        //Delete
        val button = root.findViewById<ImageButton>(R.id.delete_subtask)
        button.setOnClickListener {
            listener.onDeleteSubTask(todo.todo)
            itemsList.remove(todo.todo)
            notifyDataSetChanged()
        }
        if (isCheckable) button.visibility = GONE

        //Checkbox
        val checkbox = root.findViewById<CheckBox>(R.id.todo_check)
        checkbox.isEnabled = isCheckable
        checkbox.isChecked = todo.checked
        checkbox.setOnCheckedChangeListener { _, checked ->  listener.onCheckSubTask(todo.todo, checked)}

        return root
    }
}