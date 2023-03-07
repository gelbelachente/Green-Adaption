package org.gelbelachente.sol

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import org.gelbelachente.sol.databinding.FragmentHourlyForecastBinding
import java.time.LocalDate
import kotlin.math.roundToInt


class HourlyForecastFragment : Fragment() {

    private lateinit var binding : FragmentHourlyForecastBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHourlyForecastBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val day = this.arguments?.getInt("day") ?: 0
        val date = LocalDate.now().plusDays(day.toLong())
        binding.hourlyDay.text = "${date.dayOfMonth}. ${months[date.monthValue-1]}"
        binding.hourlyClose.setOnClickListener {
            binding.hourlySpec.visibility = View.GONE
        }

        binding.hourlyRecyclerview.adapter = HourlyAdapter(requireContext(),ForecastData.values[day] as Array<Overview>){
            binding.hourlySpec.visibility = View.VISIBLE
            val cur =ForecastData.values[day][it]!!
            binding.hourlyChrono.text = "${cur.id}:00"
            binding.hourlyCo2Text.text = "${(cur.conservativeAverage).roundToInt()} GWH"
            binding.hourlyGreenText.text = "${(cur.greenAverage).roundToInt()} GWH"
            binding.hourlySolarText.text = "${(cur.solarPower.roundToInt())} GWH"
            binding.hourlyWindText.text = "${(cur.onshorePower + cur.offshorePower).roundToInt()} GWH"

        }
        binding.hourlyRecyclerview.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

    }


}