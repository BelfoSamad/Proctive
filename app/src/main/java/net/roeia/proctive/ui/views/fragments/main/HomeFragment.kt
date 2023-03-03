package net.roeia.proctive.ui.views.fragments.main

import android.os.Bundle
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
import kotlinx.coroutines.launch
import net.roeia.proctive.R
import net.roeia.proctive.data.Status
import net.roeia.proctive.databinding.FragmentHomeBinding
import net.roeia.proctive.models.entities.Journal
import net.roeia.proctive.ui.viewmodels.main.HomeViewModel
import net.roeia.proctive.ui.views.viewholders.JournalViewHolder
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

    //Data
    private var journalAdapter: BasicRecyclerViewAdapter<Journal, JournalViewHolder>? = null

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
        journalAdapter = BasicRecyclerViewAdapter.Builder(
            itemList = journalList.toMutableList(),
            layoutId = R.layout.recyclerview_journal_item,
            vhClass = JournalViewHolder::class.java,
            bundle = bundle,
            listener = object : JournalViewHolder.JournalActions {
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
        binding.journalList.adapter = journalAdapter
    }

    private fun initClickListeners() {
        //Drawer Menu
        binding.drawermenu.setOnClickListener {
            //TODO: Setup Drawer Menu
        }

        //Profile
        binding.pomodoro.setOnClickListener {
            findNavController().navigate(R.id.go_pomodoro)
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
                journalAdapter?.setBundle(bundle)
                binding.lockJournal.tag = "1"
                binding.lockJournal.setImageResource(R.drawable.unlocked)
            } else {
                bundle.putBoolean("isLocked", false)
                journalAdapter?.setBundle(bundle)
                binding.lockJournal.tag = "0"
                binding.lockJournal.setImageResource(R.drawable.lock)
            }
        }
    }

}