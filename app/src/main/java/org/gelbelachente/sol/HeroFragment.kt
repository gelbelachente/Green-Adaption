package org.gelbelachente.sol

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.gelbelachente.sol.databinding.FragmentHeroBinding


class HeroFragment : Fragment() {

    private lateinit var binding: FragmentHeroBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHeroBinding.inflate(inflater, container, false)
        displayData()
        return binding.root
    }

    private fun displayData() {
        binding.heroRecyclerview.apply {
            adapter = HeroAdapter(requireContext(), ForecastData.overview) {
                NavHostFragment.findNavController(this@HeroFragment).navigate(R.id.action_heroFragment_to_hourlyForecastFragment,
                        Bundle(1).apply { this.putInt("day", it) })
            }
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }



}