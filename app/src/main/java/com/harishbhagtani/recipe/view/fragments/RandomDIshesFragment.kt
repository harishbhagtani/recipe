package com.harishbhagtani.recipe.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.harishbhagtani.recipe.R
import com.harishbhagtani.recipe.application.FavDishApplication
import com.harishbhagtani.recipe.databinding.FragmentDishDetailsBinding
import com.harishbhagtani.recipe.databinding.FragmentRandomDishesBinding
import com.harishbhagtani.recipe.model.entities.FavDish
import com.harishbhagtani.recipe.model.entities.RandomDish
import com.harishbhagtani.recipe.utils.Constants
import com.harishbhagtani.recipe.viewmodel.FavDishViewModel
import com.harishbhagtani.recipe.viewmodel.FavDishViewModelFactory
import com.harishbhagtani.recipe.viewmodel.RandomDishViewModel

class RandomDIshesFragment : Fragment() {

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    private var _binding: FragmentDishDetailsBinding? = null

    private var isSetFavorite = false

    private var mProgressDialog : Dialog? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)
        mRandomDishViewModel.getRandomDishFromAPI()
        funRandomDishViewModelObserver()
    }

    private fun funRandomDishViewModelObserver(){
        mRandomDishViewModel.randomDishResponse.observe(
            viewLifecycleOwner,
            {
                randomDishReponse->
                randomDishReponse?.let {
                    Log.i("NETWORK","Recipe Receieved: ${it.recipes[0]}")
                    setRandomDishResponseInUi(it.recipes[0])
                }
            }
        )

        mRandomDishViewModel.randomDishLoadingError.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    Log.i("NETWROK","Error loading random dish: ${it}")
                }
            }
        )

        mRandomDishViewModel.loadRandomDish.observe(
            viewLifecycleOwner,
            {
                loadRandomDish->
                loadRandomDish?.let {
                    Log.i("NETWORK","Random Dish loading... ${it}")

                    if(loadRandomDish){
                        showCustomProgressDialog()
                    }else{
                        hideCustomDialog()
                    }
                }
            }
        )
    }

    private fun setRandomDishResponseInUi(recipe: RandomDish.Recipe){

        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(mBinding.ivDetailImage)

        if(recipe.title.isNotEmpty()){
            mBinding.tvDetailTitleValue.setText(recipe.title)
        }else{
            mBinding.tvDetailTitleValue.setText("No Title")
        }

        var dishType = "other"

        if(recipe.dishTypes.isNotEmpty()){
            dishType = recipe.dishTypes[0]
            mBinding.tvDetailTypeValue.setText(dishType)
        }

        mBinding.tvDetailCategoryValue.setText("other")

        var ingredients = ""

        for(value in recipe.extendedIngredients){
            if(ingredients.isEmpty()){
                ingredients = value.original
            }else{
                ingredients = ingredients + ", \n" + value.original
            }
        }

        mBinding.tvDetailIngredientsValue.setText(ingredients)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            mBinding.tvDetailCookingInstructionsValue.setText(Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            ))
        }else{
            mBinding.tvDetailCookingInstructionsValue.setText(Html.fromHtml(
                recipe.instructions
            ))
        }

        mBinding.tvDetailCookingTimeValue.setText(recipe.readyInMinutes.toString())

        mBinding.fabAddFavorite.setOnClickListener {

            if(!isSetFavorite){
                val randomDishDetails = FavDish(
                    recipe.image,
                    Constants.DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )

                mFavDishViewModel.insert(randomDishDetails)

                mBinding.fabAddFavorite.setImageDrawable(
                    ContextCompat.getDrawable(requireActivity(),R.drawable.ic_baseline_favorite_24)
                )
                isSetFavorite = true
            }else{
                Toast.makeText(requireActivity(),"Already set to favorite", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideCustomDialog(){
        mProgressDialog?.let {
            it.dismiss()
        }
    }

    private fun showCustomProgressDialog(){
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let {
            it.setContentView(R.layout.dialogue_custom_progress)
            it.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}