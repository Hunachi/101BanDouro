package com.example.hanah.a101bandouro

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hanah.a101bandouro.model.MemoryItem

class ItemListAdapter(private val context: Context, private val list: MutableList<MemoryItem>, private val callback: (Int) -> Unit)
    : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            callback(holder.adapterPosition)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setVariable(BR.viewModel, list)
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val binding: ViewDataBinding = DataBindingUtil.bind(mView)
    }
}
