package com.harishbhagtani.recipe.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.harishbhagtani.recipe.R
import com.harishbhagtani.recipe.application.FavDishApplication
import com.harishbhagtani.recipe.databinding.DialougeCustomListBinding
import com.harishbhagtani.recipe.databinding.FragmentAllDishesBinding
import com.harishbhagtani.recipe.model.entities.FavDish
import com.harishbhagtani.recipe.utils.Constants
import com.harishbhagtani.recipe.view.activities.AddUpdateDishActivity
import com.harishbhagtani.recipe.view.activities.MainActivity
import com.harishbhagtani.recipe.view.adapters.CustomItemListAdapter
import com.harishbhagtani.recipe.view.adapters.FavDishAdapter
import com.harishbhagtani.recipe.viewmodel.FavDishViewModel
import com.harishbhagtani.recipe.viewmodel.FavDishViewModelFactory
import com.harishbhagtani.recipe.viewmodel.FavDishesFragment

class AllDishesFragment : Fragment() {

    private lateinit var homeViewModel: FavDishesFragment
    private lateinit var mBinding: FragmentAllDishesBinding

    private lateinit var mFavDishAdapter: FavDishAdapter
    private lateinit var mCustomListDialogue: Dialog

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAllDishesBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(),2)
        mFavDishAdapter = FavDishAdapter(this)
        mBinding.rvDishesList.adapter = mFavDishAdapter

        mFavDishViewModel.allDataList.observe(viewLifecycleOwner){
            dishes->
            dishes.let {
                if(it.isNotEmpty()){
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                    mFavDishAdapter.dishesList(it)
                }else{
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun dishDetails(favDish: FavDish){
        findNavController().navigate(AllDishesFragmentDirections.actionNavigationAllDishesToNavigationDishDetails(
            favDish
        ))

        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    fun editDish(favDish: FavDish){
        val intent = Intent(requireActivity(),AddUpdateDishActivity::class.java)
        intent.putExtra(Constants.EXTRA_DISH_DETAILS,favDish)
        startActivity(intent)
    }

    fun deleteDish(deleteDish: FavDish){
        val builder = AlertDialog.Builder(requireActivity())
            .setMessage(R.string.message_delete_dish)
            .setTitle(R.string.title_delete_dish)
            .setCancelable(false)
            .setIcon(android.R.drawable.ic_delete)
            .setPositiveButton(resources.getString(R.string.lbl_yes),object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    mFavDishViewModel.delete(deleteDish)
                    Toast.makeText(requireContext(),getString(R.string.message_dish_deleted),Toast.LENGTH_SHORT).show()
                    Log.i("DATABASE", getString(R.string.message_dish_deleted))
                }
            })
            .setNegativeButton(resources.getString(R.string.lbl_no), object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0.let {
                        it!!.dismiss()
                    }
                }
            })

        builder.create().show()
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_dish->{
                startActivity(Intent(requireActivity(),AddUpdateDishActivity::class.java))
                return true
            }
            R.id.action_filter->{
                filterDishesListDialogue()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun filterDishesListDialogue(){
        mCustomListDialogue = Dialog(requireActivity())
        val binding =  DialougeCustomListBinding.inflate(layoutInflater)
        mCustomListDialogue.setContentView(binding.root)
        binding.tvDialougeListTitle.setText(getString(R.string.title_select_item_to_filter))
        val dishTypes = Constants.dishTypes()
        dishTypes.add(0,Constants.ALL_ITEMS)
        binding.rvItemList.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CustomItemListAdapter(requireActivity(),this,dishTypes,Constants.FILTER_SELECTION)
        binding.rvItemList.adapter = adapter
        mCustomListDialogue.show()
    }

    fun filterSelection(filterItemSelection:String){
        mCustomListDialogue.dismiss()
        Log.i("FilterSelection", filterItemSelection)
        if(filterItemSelection == Constants.ALL_ITEMS){
            mFavDishViewModel.allDataList.observe(viewLifecycleOwner){
                if(it.isNotEmpty()){
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                    mFavDishAdapter.dishesList(it)
                }else{
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }else{
            mFavDishViewModel.filterList(filterItemSelection).observe(viewLifecycleOwner){
                dishes->
                dishes.let {
                    if(it.isNotEmpty()){
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE
                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mFavDishAdapter.dishesList(it)
                    }else{
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                        mBinding.rvDishesList.visibility = View.GONE
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}