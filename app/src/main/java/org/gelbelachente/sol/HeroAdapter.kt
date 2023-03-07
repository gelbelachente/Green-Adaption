package org.gelbelachente.sol

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.gelbelachente.sol.databinding.OverviewCardBinding
import java.time.LocalDate


class HeroAdapter(private val ctx : Context, var data : Array<Overview?>, val onItemSelect : (Int) -> Unit) : RecyclerView.Adapter<HeroAdapter.HeroViewHolder>() {

    class HeroViewHolder(val binding : OverviewCardBinding) : ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        return HeroViewHolder(OverviewCardBinding.inflate(LayoutInflater.from(ctx),parent,false))
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        holder.binding.heroText.text = "In $position day" + if(position > 1) "s" else ""
        val cur = data[position]
        if(cur != null){
            val date = LocalDate.now().plusDays(cur.id.toLong())
            holder.binding.heroText.text = "${date.dayOfMonth}. ${months[date.monthValue-1]}"
            holder.binding.heroColor.setBackgroundResource(cur.assessment.id)
            if(cur.isSun()){
                holder.binding.heroSun.visibility = View.VISIBLE
            }
            if(cur.isWind()){
                holder.binding.heroWind.visibility = View.VISIBLE
            }
        }

        holder.binding.root.setOnClickListener {
            if(data[position] != null) {
                onItemSelect(position)
            }
        }
    }

    override fun getItemCount() = 3

}