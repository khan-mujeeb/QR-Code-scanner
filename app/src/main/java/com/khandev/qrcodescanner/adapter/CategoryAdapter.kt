package com.khandev.qrcodescanner.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.khandev.qrcodescanner.R
import com.khandev.qrcodescanner.utlis.Categories

// Adapter class for displaying categories
class CategoryAdapter(
    private val categories: List<String>,
    private val onCategorySelected: (String) -> Unit // Callback for selection
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    private var selectedCategory: String = Categories.ALL // Default selected category


    init {
        // Trigger initial filtering for the default category
        onCategorySelected(selectedCategory)
    }

    // ViewHolder class to hold views for each item
    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // Inflate the item view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cateogry_itemview, parent, false)
        return CategoryViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        val category = categories[position]
        // highlight selected category
        if (selectedCategory == category) {
            holder.categoryName.setBackgroundColor(holder.itemView.resources.getColor(R.color.mint_green))
        } else {
            holder.categoryName.setBackgroundColor(holder.itemView.resources.getColor(R.color.platinium))
        }

        // Set category name
        holder.categoryName.text = category

        holder.itemView.setOnClickListener {
            selectedCategory = category // Update selected category
            notifyDataSetChanged() // Refresh the list
            onCategorySelected(category) // Notify the activity
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}
