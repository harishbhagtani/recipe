package com.harishbhagtani.recipe.view.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.harishbhagtani.recipe.R
import com.harishbhagtani.recipe.application.FavDishApplication
import com.harishbhagtani.recipe.databinding.ActivityAddUpdateDishBinding
import com.harishbhagtani.recipe.databinding.DialougeCustomImageSelectionBinding
import com.harishbhagtani.recipe.databinding.DialougeCustomListBinding
import com.harishbhagtani.recipe.model.entities.FavDish
import com.harishbhagtani.recipe.utils.Constants
import com.harishbhagtani.recipe.view.adapters.CustomItemListAdapter
import com.harishbhagtani.recipe.viewmodel.FavDishViewModel
import com.harishbhagtani.recipe.viewmodel.FavDishViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding
    private var imagePath: String = ""
    private lateinit var mCustomSelectionDialogue : Dialog
    private var mFavDishDetails: FavDish? = null

    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.imgAddDishImage.setOnClickListener(this)
        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnSaveDish.setOnClickListener(this)

        if(intent.hasExtra(Constants.EXTRA_DISH_DETAILS)){
            mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }

        setupActionBar()

        mFavDishDetails.let {
            if(it != null && it.id != 0){
                imagePath = it.image
                Glide.with(this)
                    .load(imagePath)
                    .centerCrop()
                    .into(mBinding.imgDish)
                mBinding.etTitle.setText(it.title)
                mBinding.etType.setText(it.type)
                mBinding.etCategory.setText(it.category)
                mBinding.etIngredients.setText(it.ingredients)
                mBinding.etCookingTime.setText(it.cookingTime)
                mBinding.etCookingInstructions.setText(it.cookingInstructions)
                mBinding.btnSaveDish.setText(getString(R.string.title_update_dish))
            }
        }
    }

    fun setupActionBar(){
        setSupportActionBar(mBinding.toolbarAddDishActivity)

        if(mFavDishDetails != null && mFavDishDetails!!.id != 0){
            supportActionBar?.let {
                supportActionBar!!.setTitle(getString(R.string.title_edit_dish))
            }
        }else{
            supportActionBar?.let {
                supportActionBar!!.setTitle(getString(R.string.add_dish))
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun showCustomImageSelectionDialouge(){
        val dialog = Dialog(this)
        val binding = DialougeCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        binding.tvDCamera.setOnClickListener {
            Dexter.withContext(this).withPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(
                object : MultiplePermissionsListener{
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if(report.areAllPermissionsGranted()){
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, CAMERA)

                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialougeForPermission()
                    }
                }
            ).onSameThread().check()
            dialog.dismiss()
        }
        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this).withPermission(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
            ).withListener(
                object : PermissionListener{
                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialougeForPermission()
                    }

                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        val galleryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, GALLERY)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        TODO("Not yet implemented")
                    }
                }
            ).onSameThread().check()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showRationalDialougeForPermission(){
        AlertDialog.Builder(this).setMessage("It looks like you have turned off the permission." +
                " Please turn it om to use certain features of the app")
            .setPositiveButton("GO TO SETTINGS", DialogInterface.OnClickListener { dialog, which ->
                try{
                    val intent  = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            })
            .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            }).show()
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.img_add_dish_image->{
                    showCustomImageSelectionDialouge()
                    return
                }
                R.id.et_type->{
                    showCustomItemsDialogue(resources.getString(R.string.title_select_dish_type),
                        Constants.dishTypes(),
                        Constants.DISH_TYPE
                    )
                    return
                }
                R.id.et_category->{
                    showCustomItemsDialogue(resources.getString(R.string.title_select_dish_category)
                        ,Constants.dishCategories(),
                        Constants.DISH_CATEGORY
                    )
                    return
                }
                R.id.et_cooking_time->{
                    showCustomItemsDialogue(resources.getString(R.string.title_select_dish_cooking_time),
                        Constants.cookingTime() ,
                        Constants.DISH_COOKING_TIME
                    )
                    return
                }
                R.id.btn_save_dish->{
                    val title = mBinding.etTitle.text.toString().trim { it <= ' '}
                    val type = mBinding.etType.text.toString().trim { it <= ' '}
                    val category = mBinding.etCategory.text.toString().trim { it <= ' '}
                    val ingredients = mBinding.etIngredients.text.toString().trim { it <= ' '}
                    val cookingTime = mBinding.etCookingTime.text.toString().trim { it <= ' '}
                    val cookingInstructions = mBinding.etCookingInstructions.text.toString().trim { it <= ' '}

                    when{
                        TextUtils.isEmpty(imagePath)->{
                            Toast.makeText(this,
                                getString(R.string.err_string_empty_image),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        TextUtils.isEmpty(title)->{
                            mBinding.etTitle.setError(resources.getString(R.string.err_string_empty_title))
                        }
                        TextUtils.isEmpty(type)->{
                            mBinding.etType.setError(getString(R.string.err_string_empty_type))
                        }
                        TextUtils.isEmpty(category)->{
                            mBinding.etCategory.setError(resources.getString(R.string.err_string_empty_category))
                        }
                        TextUtils.isEmpty(ingredients)->{
                            mBinding.etIngredients.setError(resources.getString(R.string.err_string_empty_Ingredients))
                        }
                        TextUtils.isEmpty(cookingTime)->{
                            mBinding.etCookingTime.setError(resources.getString(R.string.err_string_empty_cooking_time))
                        }
                        TextUtils.isEmpty(cookingInstructions)->{
                            mBinding.etCookingInstructions.setError(resources.getString(R.string.err_string_empty_cooking_instructions))
                        }
                        else->{

                            var dishId = 0
                            var dishImageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                            var isFavorite = false

                            if(mFavDishDetails != null){
                                dishId = mFavDishDetails!!.id
                                isFavorite = mFavDishDetails!!.favoriteDish
                                dishImageSource = mFavDishDetails!!.imageSource
                            }

                            val favDishDetails: FavDish = FavDish(
                            imagePath,
                            dishImageSource,
                            title,
                            type,
                            category,
                            ingredients,
                            cookingTime,
                            cookingInstructions,
                            isFavorite,
                                dishId
                            )

                            if(mFavDishDetails != null){
                                favDishViewModel.update(favDishDetails)
                                Toast.makeText(
                                    this,
                                    "Fav Dish Updated",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.i("DATABASE","Updation successful: ${favDishDetails.toString()}")
                            }else{
                                favDishViewModel.insert(favDishDetails)
                                Toast.makeText(
                                    this,
                                    "Fav Dish Added to Database",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.i("DATABASE","Insertion successful")
                            }
                            finish()
                        }
                    }
                    return
                }
            }
        }
    }

    companion object{
        private const val CAMERA = 1
        private const val GALLERY = 2

        private const val IMAGE_DIRECTORY = "FavDishImages"
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String{
        val contextWrapper = ContextWrapper(applicationContext)
        var file = contextWrapper.getDir(IMAGE_DIRECTORY, MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

        return file.absolutePath

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun showCustomItemsDialogue(title:String, itemList:ArrayList<String>, selection:String){
        mCustomSelectionDialogue = Dialog(this)
        val binding : DialougeCustomListBinding = DialougeCustomListBinding.inflate(layoutInflater)

        mCustomSelectionDialogue.setContentView(binding.root)
        binding.tvDialougeListTitle.text = title

        binding.rvItemList.layoutManager = LinearLayoutManager(this)
        val adapter = CustomItemListAdapter(this,null,itemList, selection)
        binding.rvItemList.adapter = adapter
        mCustomSelectionDialogue.show()
    }

    public fun selectedListItem(item: String, selection:String){
        when(selection){
            Constants.DISH_TYPE->{
                mCustomSelectionDialogue.dismiss()
                mBinding.etType.setText(item)
                return
            }
            Constants.DISH_CATEGORY->{
                mCustomSelectionDialogue.dismiss()
                mBinding.etCategory.setText(item)
                return
            }
            Constants.DISH_COOKING_TIME->{
                mCustomSelectionDialogue.dismiss()
                mBinding.etCookingTime.setText(item)
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                CAMERA->{
                    data?.let {
                        val thumbnail: Bitmap = data.extras!!.get("data") as Bitmap
                        Glide.with(this)
                            .load(thumbnail)
                            .centerCrop()
                            .into(mBinding.imgDish)
                        imagePath = saveImageToInternalStorage(thumbnail)
                        Log.i("IMAGE PATH", imagePath)
                        mBinding.imgAddDishImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))
                    }
                }
                GALLERY->{
                    data?.let {
                        val selectedPhotoUri = data.data
                        Glide.with(this)
                            .load(selectedPhotoUri)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(object : RequestListener<Drawable>{
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    Log.e("GLIDE ERROR:","ERROR LOADING IMAGE")
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    resource.let {
                                        var bitmap: Bitmap = resource!!.toBitmap();
                                        imagePath = saveImageToInternalStorage(bitmap)
                                        Log.i("IMAGE PATH GALLERY: ",imagePath)
                                        return false
                                    }
                                }
                            })
                            .into(mBinding.imgDish)
                        mBinding.imgAddDishImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))
                    }
                }
            }
        }else if(resultCode == RESULT_CANCELED){
            Toast.makeText(this,"Operation Cancelled",Toast.LENGTH_LONG).show()
        }
    }
}