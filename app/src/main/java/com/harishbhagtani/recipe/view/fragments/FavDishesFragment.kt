package com.harishbhagtani.recipe.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.harishbhagtani.recipe.application.FavDishApplication
import com.harishbhagtani.recipe.databinding.FragmentFavDishesBinding
import com.harishbhagtani.recipe.model.entities.FavDish
import com.harishbhagtani.recipe.view.activities.MainActivity
import com.harishbhagtani.recipe.view.adapters.FavDishAdapter
import com.harishbhagtani.recipe.viewmodel.FavDishViewModel
import com.harishbhagtani.recipe.viewmodel.FavDishViewModelFactory

class FavDishesFragment : Fragment() {

    private val mFavDishesViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }
    private var _binding: FragmentFavDishesBinding? = null

    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentFavDishesBinding.inflate(inflater, container, false)
        val root: View = mBinding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvFavDish.layoutManager = GridLayoutManager(requireActivity(), 2)
        val adapter = FavDishAdapter(this)
        mBinding.rvFavDish.adapter = adapter


        mFavDishesViewModel.favDishesList.observe(viewLifecycleOwner){
            dishes->
            dishes.let {
                if(it.isNotEmpty()){
                    mBinding.tvErrNoFavDish.visibility = View.GONE
                    adapter.dishesList(it)
                    mBinding.rvFavDish.visibility = View.VISIBLE
                }else{
                    mBinding.rvFavDish.visibility = View.GONE
                    mBinding.tvErrNoFavDish.visibility = View.VISIBLE
                }
            }
        }
    }

    fun dishDetails(dish:FavDish){
        dish.let {
            findNavController().navigate(FavDishesFragmentDirections.actionNavigationFavDishesToNavigationDishDetails(dish))
            if(requireActivity() is MainActivity){
                (activity as MainActivity?)!!.hideBottomNavigationView()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}