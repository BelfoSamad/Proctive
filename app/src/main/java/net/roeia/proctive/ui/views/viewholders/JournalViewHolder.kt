package net.roeia.proctive.ui.views.viewholders

import android.graphics.BlurMaskFilter
import android.os.Bundle
import android.view.View
import net.roeia.proctive.databinding.RecyclerviewJournalItemBinding
import net.roeia.proctive.models.entities.Journal
import net.roeia.proctive.utils.adapters.BasicRecyclerViewAdapter

class JournalViewHolder constructor(private val b: RecyclerviewJournalItemBinding) :
    BasicRecyclerViewAdapter.BaseViewHolder<Journal>(b) {

    interface JournalActions : BasicRecyclerViewAdapter.BaseListener {

        fun lockJournal(journal: Journal)

        fun goJournal(journal: Journal)

    }

    override fun onBindViewHolder(
        model: Journal,
        bundle: Bundle,
        position: Int,
        listener: BasicRecyclerViewAdapter.BaseListener?
    ) {
        val isLocked = bundle.getBoolean("isLocked")
        b.journal = model
        b.listener = listener as JournalActions

        b.journalTitle.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        b.journalTimestamp.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        b.journalContent.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        if(isLocked) {
            b.journalTitle.paint.maskFilter = BlurMaskFilter(b.journalTitle.textSize / 3, BlurMaskFilter.Blur.NORMAL)
            b.journalTimestamp.paint.maskFilter = BlurMaskFilter(b.journalTimestamp.textSize / 3, BlurMaskFilter.Blur.NORMAL)
            b.journalContent.paint.maskFilter = BlurMaskFilter(b.journalContent.textSize / 3, BlurMaskFilter.Blur.NORMAL)
            b.lockJournal.tag = "1"
        } else {
            b.journalTitle.paint.maskFilter = null
            b.journalTimestamp.paint.maskFilter = null
            b.journalContent.paint.maskFilter = null
            b.lockJournal.tag = "0"
        }

        b.lockJournal.setOnClickListener {
            if (b.lockJournal.tag == "0") {
                b.journalTitle.paint.maskFilter = BlurMaskFilter(b.journalTitle.textSize / 3, BlurMaskFilter.Blur.NORMAL)
                b.journalTimestamp.paint.maskFilter = BlurMaskFilter(b.journalTimestamp.textSize / 3, BlurMaskFilter.Blur.NORMAL)
                b.journalContent.paint.maskFilter = BlurMaskFilter(b.journalContent.textSize / 3, BlurMaskFilter.Blur.NORMAL)
                b.lockJournal.tag = "1"
                listener.lockJournal(model)
            } else {
                b.journalTitle.paint.maskFilter = null
                b.journalTimestamp.paint.maskFilter = null
                b.journalContent.paint.maskFilter = null
                b.lockJournal.tag = "0"
            }
        }
    }

}