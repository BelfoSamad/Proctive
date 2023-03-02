package net.roeia.proctive.ui.views.fragments.main

import android.annotation.SuppressLint
import android.opengl.Visibility
import android.os.Bundle
import android.text.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.roeia.proctive.data.Status
import net.roeia.proctive.databinding.FragmentJournalBinding
import net.roeia.proctive.ui.viewmodels.main.JournalViewModel
import kotlin.math.max

@AndroidEntryPoint
class JournalFragment : Fragment() {
    companion object {
        private const val TAG = "JournalFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: JournalViewModel by viewModels()
    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    private val textWatcher = object : TextWatcher {
        var ignore = false
        var dotChange = false
        var charCount = -1

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //Nothing
        }

        override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
            //Show Done when Text Changes
            if ((p0 != null && p0.isNotEmpty()) &&
                (p0.toString() != viewModel.getCurrentTitle() || p0.toString() != viewModel.getCurrentContent()) &&
                binding.journalTitle.text.isNotEmpty() &&
                binding.journalContent.text!!.isNotEmpty()
            ) {
                binding.doneJournal.visibility = VISIBLE
            } else binding.doneJournal.visibility = GONE

            //Ignore Replacing * with dot
            if (charCount > count && p0?.length!! > 0 && p0[p0.length - 1].toString() == "\u25CF") {
                ignore = true
            }

            //Replace * with dot
            if (p0?.length!! > 1 &&
                p0[p0.length - 2].toString() == "*" &&
                p0[p0.length - 1].toString() == " "
            ) dotChange = true
        }

        override fun afterTextChanged(p0: Editable?) {
            charCount = p0!!.length
            if (dotChange) {
                var text = binding.journalContent.text.toString()
                text = text.dropLast(2) + "\u25CF "
                dotChange = false
                binding.journalContent.setText(text)
                binding.journalContent.setSelection(text.length)
                charCount = text.length
            }

            if (ignore) {
                val text = binding.journalContent.text.toString().dropLast(1) + "*"
                ignore = false
                binding.journalContent.setText(text)
                binding.journalContent.setSelection(text.length)
                charCount = text.length
            }
        }
    }

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateJournal.collect {
                    if (it.status == Status.SUCCESS) {
                        binding.journalTitle.setText(it.journal?.title!!)
                        binding.journalContent.setText(it.journal.content!!)
                        if (it.journal.updatedDate != null || it.journal.createdDate != null) {
                            val timeFrom = viewModel.getDateFrom(
                                if (it.journal.updatedDate != null)
                                    it.journal.updatedDate!! else it.journal.createdDate!!
                            )
                            val timeUnit: String
                            val time: Int
                            if (timeFrom.split(":")[4].toInt() < 60) {
                                timeUnit = "min"
                                time = timeFrom.split(":")[4].toInt()
                            } else if (timeFrom.split(":")[3].toInt() < 24) {
                                timeUnit = "h"
                                time = timeFrom.split(":")[3].toInt()
                            } else if (timeFrom.split(":")[2].toInt() < 30) {
                                timeUnit = "day"
                                time = timeFrom.split(":")[2].toInt()
                            } else if (timeFrom.split(":")[1].toInt() < 12) {
                                timeUnit = "month"
                                time = timeFrom.split(":")[1].toInt()
                            } else {
                                timeUnit = "years"
                                time = timeFrom.split(":")[0].toInt()
                            }
                            binding.journalTimestamp.visibility = VISIBLE
                            binding.journalTimestamp.text = "Updated $time $timeUnit ago"
                        }
                        binding.delete.visibility = VISIBLE
                        binding.doneJournal.visibility = GONE
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set EditText Changes
        binding.journalTitle.addTextChangedListener(textWatcher)
        binding.journalContent.addTextChangedListener(textWatcher)

        //Init ClickListeners
        initClickListeners()

        //Observe LiveData
        viewModel.journalDeletedLiveData.observe(viewLifecycleOwner) {
            if (it) binding.delete.visibility = GONE
        }
        viewModel.journalUpdatedLiveData.observe(viewLifecycleOwner) {
            if (it) binding.doneJournal.visibility = GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun initClickListeners() {
        //Back
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        //Delete
        binding.delete.setOnClickListener {
            viewModel.deleteJournal()
        }

        //Done
        binding.doneJournal.setOnClickListener {
            binding.journalTimestamp.text = "Updated 0min ago"
            binding.journalTimestamp.visibility = VISIBLE
            viewModel.addOrUpdate(
                binding.journalTitle.text.toString(),
                binding.journalContent.text.toString()
            )
        }
    }

}