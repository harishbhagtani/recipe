package com.harishbhagtani.recipe.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.harishbhagtani.recipe.R
import com.harishbhagtani.recipe.databinding.ItemDishLayoutBinding
import com.harishbhagtani.recipe.model.entities.FavDish
import com.harishbhagtani.recipe.view.fragments.AllDishesFragment
import com.harishbhagtani.recipe.view.fragments.FavDishesFragment

class FavDishAdapter(private val fragment: Fragment): RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes: List<FavDish> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDishLayoutBinding.inflate(LayoutInflater.from(fragment.context),
            parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)
        holder.tvDishTitle.text = dish.title
        holder.itemView.setOnClickListener {
            if(fragment is AllDishesFragment){
                fragment.dishDetails(dish)
            }else if (fragment is FavDishesFragment){
                fragment.dishDetails(dish)
            }
        }

        if(fragment is AllDishesFragment){
            holder.ibMore.visibility = View.VISIBLE

            holder.ibMore.setOnClickListener {
                var popupMenu = PopupMenu(fragment.context,holder.ibMore)
                popupMenu.menuInflater.inflate(R.menu.menu_adapter, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.action_edit_dish->{
                            fragment.editDish(dish)
                        }
                        R.id.action_delete_dish->{
                            fragment.deleteDish(dish)
                        }
                        else->{
                            Log.i("Error","Menu id not found")
                        }
                    }
                    true
                }
                popupMenu.show()
            }

        }else if(fragment is FavDishesFragment){
            holder.ibMore.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    fun dishesList(list: List<FavDish>){
        dishes = list
        notifyDataSetChanged()
    }

    class ViewHolder(view:ItemDishLayoutBinding): RecyclerView.ViewHolder(view.root) {

        val ivDishImage = view.ivDishImage
        val tvDishTitle = view.tvDishTitle
        val ibMore = view.ibMore


    }
}