package com.harishbhagtani.recipe.view.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.harishbhagtani.recipe.R
import com.harishbhagtani.recipe.application.FavDishApplication
import com.harishbhagtani.recipe.databinding.FragmentDishDetailsBinding
import com.harishbhagtani.recipe.model.entities.FavDish
import com.harishbhagtani.recipe.view.activities.MainActivity
import com.harishbhagtani.recipe.viewmodel.FavDishViewModel
import com.harishbhagtani.recipe.viewmodel.FavDishViewModelFactory
import java.io.IOException

class DishDetailsFragment : Fragment() {

    private lateinit var mBinding : FragmentDishDetailsBinding
    private val mViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    fun setViewValues(args:DishDetailsFragmentArgs){
        args.let {
            try {
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable>{
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("IMAGE", "Load Failed")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.i("IMAGE","Loaded Successfully, assigning pallete")
                            Palette.from(resource!!.toBitmap())
                                .generate {
                                    palette->
                                    palette?.let {
                                        val intColor = it?.vibrantSwatch?.rgb ?: 0
                                        mBinding.clDetailsDish.setBackgroundColor(intColor)
                                        Log.i("IMAGE","Pallete Assigned, Color: $intColor")
                                    }
                                }
                            return false
                        }

                    })
                    .into(mBinding.ivDetailImage)
            }catch (e: IOException){
                e.printStackTrace()
            }

            mBinding.tvDetailTitleValue.setText(it.dishDetails.title)
            mBinding.tvDetailTypeValue.setText(it.dishDetails.type)
            mBinding.tvDetailCategoryValue.setText(it.dishDetails.category)
            mBinding.tvDetailIngredientsValue.setText(it.dishDetails.ingredients)
            mBinding.tvDetailCookingTimeValue.setText(it.dishDetails.cookingTime)
            mBinding.tvDetailCookingInstructionsValue.setText(it.dishDetails.cookingInstructions)

            if(args.dishDetails.favoriteDish){
                mBinding.fabAddFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }else{
                mBinding.fabAddFavorite.setImageResource(R.drawable.ic_favorite_not_set)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailsFragmentArgs by navArgs()
        if(activity is MainActivity){
            (activity as MainActivity).setActionBarTitle(args.dishDetails.title)
        }
        Log.i("DATA",args.dishDetails.toString())
        setViewValues(args)
        mBinding.fabAddFavorite.setOnClickListener {
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish
            mViewModel.update(args.dishDetails)

            Log.i("DATA",args.dishDetails.toString())

            if(args.dishDetails.favoriteDish){
                mBinding.fabAddFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                Toast.makeText(requireContext(),getString(R.string.msg_added_fav), Toast.LENGTH_SHORT).show()
            }else{
                mBinding.fabAddFavorite.setImageResource(R.drawable.ic_favorite_not_set)
                Toast.makeText(requireContext(),getString(R.string.msg_rem_fav), Toast.LENGTH_SHORT).show()
            }
        }
    }
}