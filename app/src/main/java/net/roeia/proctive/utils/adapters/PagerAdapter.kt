package net.roeia.proctive.utils.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import net.roeia.proctive.databinding.BottomSheetTaskBinding
import net.roeia.proctive.databinding.PagePomodoroAverageBinding

class PagerAdapter(private val bindListener: BindListener) :
    RecyclerView.Adapter<PagerAdapter.ViewHolder>() {

    interface BindListener {

        fun bind(holder: PagePomodoroAverageBinding)

        fun bind(holder: BottomSheetTaskBinding)

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> ViewHolder(
                BottomSheetTaskBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> ViewHolder(
                PagePomodoroAverageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) bindListener.bind(holder.taskBinding!!)
        else if (position == 1) bindListener.bind(holder.pomodoroBinding!!)
    }

    override fun getItemCount(): Int = 2

    class ViewHolder : RecyclerView.ViewHolder {

        var pomodoroBinding: PagePomodoroAverageBinding? = null
        var taskBinding: BottomSheetTaskBinding? = null

        constructor(pomodoroBinding: PagePomodoroAverageBinding) : super(pomodoroBinding.root) {
            this.pomodoroBinding = pomodoroBinding
        }
        constructor(taskBinding: BottomSheetTaskBinding) : super(taskBinding.root) {
            this.taskBinding = taskBinding
        }

    }

}