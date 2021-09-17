package com.drozdova.dancersbase.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drozdova.dancersbase.utils.DancerListAdapter.DancerViewHolder
import com.drozdova.dancersbase.database.Dancer
import com.drozdova.dancersbase.databinding.DancerItemBinding

class DancerListAdapter(private val listener: DancerListener)
    : ListAdapter<Dancer, DancerViewHolder>(DANCER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DancerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DancerItemBinding.inflate(layoutInflater, parent, false)
        return DancerViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: DancerViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class DancerViewHolder(private val binding: DancerItemBinding,
                           private val listener: DancerListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dancer: Dancer) {
            binding.dancerNameValue.text = dancer.name
            binding.dancerYearBirthValue.text = dancer.year.toString()
            binding.dancerClubValue.text = dancer.club
            binding.dancerLeagueValue.text = dancer.league

            binding.deleteButton.setOnClickListener {
                listener.deleteDancer(dancer)
            }

            binding.editButton.setOnClickListener {
                listener.updateDancer(dancer)
            }
        }
    }

    companion object {
        private val DANCER_COMPARATOR = object : DiffUtil.ItemCallback<Dancer>() {

            override fun areItemsTheSame(oldItem: Dancer, newItem: Dancer): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Dancer, newItem: Dancer): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.year == newItem.year &&
                        oldItem.club == newItem.club &&
                        oldItem.league == newItem.league
            }
        }
    }
}