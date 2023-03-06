package net.roeia.proctive.ui.views.fragments.startup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.R
import net.roeia.proctive.base.ui.BaseRecyclerViewAdapter
import net.roeia.proctive.databinding.FragmentOnboardingBinding
import net.roeia.proctive.models.pojo.Hype
import net.roeia.proctive.ui.views.viewholders.viewpagers.OnBoardingViewHolder

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {
    companion object {
        private const val TAG = "OnBoardingFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Init ViewPager
        initViewPager()

        //Init ClickListener
        binding.next.setOnClickListener {
            if (it.tag == 1) {
                findNavController().navigate(R.id.start_register)
            } else {
                val pos = binding.viewpager.currentItem + 1
                binding.viewpager.currentItem = pos
                if (pos == resources.getStringArray(R.array.page_titles).size - 1) {
                    (it as TextView).text = "Start"
                    it.tag = 1
                }
            }
        }
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun initViewPager() {
        val typedArray = resources.obtainTypedArray(R.array.page_images)
        val imagesList = mutableListOf<Int>()
        for (i in 0 until typedArray.indexCount) {imagesList.add(typedArray.getResourceId(i, 0))}

        val adapter = BaseRecyclerViewAdapter.Builder(
            layoutId = R.layout.page_hype,
            vhClass = OnBoardingViewHolder::class.java,
            bundle = Bundle(),
            listener = null,
            itemList = resources.getStringArray(R.array.page_titles).mapIndexed{index, item ->
                Hype(item, resources.getStringArray(R.array.page_descriptions)[index], imagesList[index])
            }.toMutableList()
        ).build()
        binding.viewpager.adapter = adapter
        typedArray.recycle()

        //Init TabLayout
        TabLayoutMediator(binding.tablayout, binding.viewpager) { _, _ ->
        }.attach()
    }
}