package net.roeia.proctive.utils.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import net.roeia.proctive.R
import net.roeia.proctive.models.pojo.TodoChecked


class SubTasksArrayAdapter(private val context: Context, private val itemsList: Map<String, Boolean>) : BaseAdapter() {

    override fun getCount() = itemsList.size

    override fun getItem(position: Int) : TodoChecked = TodoChecked(itemsList.keys.elementAt(position), itemsList.values.elementAt(position))

    override fun getItemId(position: Int) = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val root = LayoutInflater.from(context).inflate(R.layout.list_task_item, parent, false)
        val todo = getItem(position)

        root.findViewById<TextView>(R.id.todo_content).text = todo.todo
        root.findViewById<CheckBox>(R.id.todo_check).isChecked = todo.checked

        return root
    }
}