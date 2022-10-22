package com.raghav.multipartexample

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raghav.multipartexample.databinding.ItemBinding

class CountryAdapter(val context: Context, var countries: List<Country>) :
    RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countries[position]

        holder.binding.textView.text = country.name
        val imageLinks = country.images.split(",")
        GlideApp.with(context).load(imageLinks[0]).into(holder.binding.imageView1)
        GlideApp.with(context).load(imageLinks[1]).into(holder.binding.imageView2)
        GlideApp.with(context).load(imageLinks[2]).into(holder.binding.imageView3)

        holder.binding.root.setOnClickListener {
            Toast.makeText(context, country.capital, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return countries.size
    }
}