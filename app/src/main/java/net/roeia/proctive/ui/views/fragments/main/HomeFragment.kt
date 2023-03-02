package net.roeia.proctive.ui.views.fragments.main

import android.graphics.BlurMaskFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.roeia.proctive.R
import net.roeia.proctive.data.Status
import net.roeia.proctive.databinding.FragmentHomeBinding
import net.roeia.proctive.databinding.RecyclerviewJournalItemBinding
import net.roeia.proctive.models.entities.Journal
import net.roeia.proctive.ui.viewmodels.main.HomeViewModel
import net.roeia.proctive.utils.adapters.BasicRecyclerViewAdapter


@AndroidEntryPoint
class HomeFragment : Fragment() {
    companion object {
        private const val TAG = "HomeFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateJournal.collect {
                    if (it.status == Status.SUCCESS)
                        initJournalRecyclerView(it.journalList!!)
                }
            }
        }
        viewModel.fetchJournal()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Init ClickListeners
        initClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun initJournalRecyclerView(journalList: List<Journal>) {
        val bundle = Bundle()
        bundle.putBoolean("isLocked", false)
        val adapter = BasicRecyclerViewAdapter.Builder(
            itemList = journalList.toMutableList(),
            layoutId = R.layout.recyclerview_journal_item,
            vhClass = ViewHolder::class.java,
            bundle = bundle,
            listener = object : ViewHolder.JournalActions {
                override fun lockJournal(journal: Journal) {
                    viewModel.lockJournal(journal)
                }

                override fun goJournal(journal: Journal) {
                    val newBundle = Bundle()
                    newBundle.putLong("journalId", journal.journalId!!)
                    findNavController().navigate(R.id.go_journal, newBundle)
                }

            }
        ).build()
        binding.journalList.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.journalList.adapter = adapter
    }

    private fun initClickListeners() {
        //Drawer Menu
        binding.drawermenu.setOnClickListener {
            //TODO: Setup Drawer Menu
        }

        //Schedule
        binding.schedule.setOnClickListener {
            findNavController().navigate(R.id.go_schedule)
        }

        //Profile
        binding.profile.setOnClickListener {
            findNavController().navigate(R.id.go_profile)
        }

        //Go Journal
        binding.goJournal.setOnClickListener {
            findNavController().navigate(R.id.go_journal)
        }

        //Lock Journal
        binding.lockJournal.setOnClickListener {
            val bundle = Bundle()
            if (binding.lockJournal.tag == "0") {
                bundle.putBoolean("isLocked", true)
                (binding.journalList.adapter as BasicRecyclerViewAdapter<Journal, ViewHolder>).setBundle(bundle)
                binding.lockJournal.tag = "1"
                binding.lockJournal.setImageResource(R.drawable.unlocked)
            } else {
                bundle.putBoolean("isLocked", false)
                (binding.journalList.adapter as BasicRecyclerViewAdapter<Journal, ViewHolder>).setBundle(bundle)
                binding.lockJournal.tag = "0"
                binding.lockJournal.setImageResource(R.drawable.lock)
            }
        }
    }

    fun goHabits(v: View) {
        findNavController().navigate(R.id.go_habit)
    }

    /***********************************************************************************************
     * ************************* RecyclerView Adapter
     */
    class ViewHolder constructor(private val b: RecyclerviewJournalItemBinding) :
        BasicRecyclerViewAdapter.BaseViewHolder<Journal>(b) {

        interface JournalActions : BasicRecyclerViewAdapter.BaseListener {

            fun lockJournal(journal: Journal)

            fun goJournal(journal: Journal)

        }

        override fun onBindViewHolder(
            model: Journal,
            bundle: Bundle,
            position: Int,
            listener: BasicRecyclerViewAdapter.BaseListener
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

}