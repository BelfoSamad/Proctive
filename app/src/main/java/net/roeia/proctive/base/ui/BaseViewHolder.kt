package net.roeia.proctive.base.ui

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<MODEL>(b: ViewDataBinding) : RecyclerView.ViewHolder(b.root) {

    interface BaseListener

    abstract fun onBindViewHolder(
        model: MODEL?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    )
}