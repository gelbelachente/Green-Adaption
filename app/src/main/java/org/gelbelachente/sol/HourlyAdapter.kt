package org.gelbelachente.sol

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.gelbelachente.sol.databinding.HourlyCardBinding

class HourlyAdapter(private val ctx: Context, val data : Array<Overview>, val select : (Int) -> Unit) : RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>() {

    class HourlyViewHolder(val binding : HourlyCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inf = LayoutInflater.from(ctx)
        return HourlyViewHolder(HourlyCardBinding.inflate(inf,parent,false))
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val cur = data[position]

        holder.binding.hourlyText.text = "${cur.id}:00"
        holder.binding.hourlyColor.setBackgroundResource(cur.assessment.id)
        if(cur.isWind()){
            holder.binding.hourlyWind.visibility = View.VISIBLE
        }
        if(cur.isSun()){
            holder.binding.hourlySun.visibility = View.VISIBLE
        }
        holder.binding.root.setOnClickListener {
            select(position)
            true
        }

    }

    override fun getItemCount() = 24

}