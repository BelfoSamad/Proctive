package net.roeia.proctive.ui.views.viewholders.recyclerviews

import android.graphics.BlurMaskFilter
import android.os.Bundle
import android.view.View
import net.roeia.proctive.databinding.RecyclerviewJournalItemBinding
import net.roeia.proctive.models.entities.todo.Journal
import net.roeia.proctive.base.ui.BaseViewHolder

class JournalViewHolder constructor(private val binding: RecyclerviewJournalItemBinding) :
    BaseViewHolder<Journal>(binding) {

    interface JournalActions : BaseListener {

        fun lockJournal(journal: Journal)

        fun goJournal(journal: Journal)

    }

    override fun onBindViewHolder(
        model: Journal?,
        bundle: Bundle,
        position: Int,
        listener: BaseListener?
    ) {
        val isLocked = bundle.getBoolean("isLocked")
        binding.journal = model
        binding.listener = listener as JournalActions

        binding.journalTitle.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        binding.journalTimestamp.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        binding.journalContent.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        if(isLocked) {
            binding.journalTitle.paint.maskFilter = BlurMaskFilter(binding.journalTitle.textSize / 3, BlurMaskFilter.Blur.NORMAL)
            binding.journalTimestamp.paint.maskFilter = BlurMaskFilter(binding.journalTimestamp.textSize / 3, BlurMaskFilter.Blur.NORMAL)
            binding.journalContent.paint.maskFilter = BlurMaskFilter(binding.journalContent.textSize / 3, BlurMaskFilter.Blur.NORMAL)
            binding.lockJournal.tag = "1"
        } else {
            binding.journalTitle.paint.maskFilter = null
            binding.journalTimestamp.paint.maskFilter = null
            binding.journalContent.paint.maskFilter = null
            binding.lockJournal.tag = "0"
        }

        binding.lockJournal.setOnClickListener {
            if (binding.lockJournal.tag == "0") {
                binding.journalTitle.paint.maskFilter = BlurMaskFilter(binding.journalTitle.textSize / 3, BlurMaskFilter.Blur.NORMAL)
                binding.journalTimestamp.paint.maskFilter = BlurMaskFilter(binding.journalTimestamp.textSize / 3, BlurMaskFilter.Blur.NORMAL)
                binding.journalContent.paint.maskFilter = BlurMaskFilter(binding.journalContent.textSize / 3, BlurMaskFilter.Blur.NORMAL)
                binding.lockJournal.tag = "1"
                listener.lockJournal(model!!)
            } else {
                binding.journalTitle.paint.maskFilter = null
                binding.journalTimestamp.paint.maskFilter = null
                binding.journalContent.paint.maskFilter = null
                binding.lockJournal.tag = "0"
            }
        }
    }

}