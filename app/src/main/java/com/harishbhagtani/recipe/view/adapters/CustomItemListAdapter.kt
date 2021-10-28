package com.harishbhagtani.recipe.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.harishbhagtani.recipe.databinding.ItemCustomListBinding
import com.harishbhagtani.recipe.view.activities.AddUpdateDishActivity
import com.harishbhagtani.recipe.view.fragments.AllDishesFragment

class CustomItemListAdapter(private val activity: Activity,private  val fragment: Fragment?,private val listItems: ArrayList<String>, private val selection:String):RecyclerView.Adapter<CustomItemListAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemCustomListBinding =  ItemCustomListBinding
            .inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(itemCustomListBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tv_text.text = item

        holder.itemView.setOnClickListener {
            if(activity is AddUpdateDishActivity){
                activity.selectedListItem(item, selection)
            }
            if(fragment is AllDishesFragment){
                fragment.filterSelection(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    class ViewHolder(view: ItemCustomListBinding):RecyclerView.ViewHolder(view.root){
        val tv_text = view.tvText

    }
}