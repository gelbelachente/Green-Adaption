package org.gelbelachente.sol

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.gelbelachente.sol.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {

    private lateinit var binding : FragmentWelcomeBinding
    private var isReady = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWelcomeBinding.inflate(inflater,container,false)

        loadData()

        binding.welcomeToForecast.setOnClickListener {
            if(isReady){
                NavHostFragment.findNavController(this).navigate(R.id.action_welcomeFragment_to_heroFragment)
            }
        }
        return binding.root
    }

    private fun setReady(){
        isReady = true
        //invoke ui change
    }

    private fun loadData(main : Boolean = true) {

        val azimuthAsset = requireActivity().assets.open("sun_azimuth_angles")
        val elevationAsset = requireActivity().assets.open("sun_elevation_angles")

        val preferences = requireActivity().getSharedPreferences("green_adapation_data", Context.MODE_PRIVATE)

        val queue = (requireActivity() as MainActivity).queue
        queue.start()

        var backup = false

        val weatherApi = WeatherApi(lifecycleScope, queue, preferences)
        weatherApi.loadStaticAssets(azimuthAsset,elevationAsset)
        weatherApi.updateData() {
            if(it){ //backup available
                Toast.makeText(requireContext(),"Keine Verbindung. Greife auf Backup zurück!",Toast.LENGTH_SHORT).show()
                backup = true
            }else {
                //wait until connection is available
                val mat = MaterialAlertDialogBuilder(requireContext()).setTitle("No Wi-Fi Connection!")
                    .setPositiveButton("Retry") { a, b ->
                        a.cancel()
                        loadData(false)
                    }.show()
            }
        }


        if (!main) {
            //no recursive callbacks
            return
        }

        lifecycleScope.launch {
            while (true) {
                if (WeatherData.count.get() == WeatherData.places.size) {
                    if (ForecastData.overview[0] != null) {
                        break
                    }
                    ForecastCore(requireContext()).predict()
                    if(!backup){
                        weatherApi.saveOfflineData()
                    }
                    break
                }
                delay(50)
            }
        }.invokeOnCompletion {
            setReady()
        }
    }

}