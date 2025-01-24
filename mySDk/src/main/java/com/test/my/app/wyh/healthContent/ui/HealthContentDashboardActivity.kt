package com.test.my.app.wyh.healthContent.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.CleverTapConstants
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.CleverTapHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.databinding.ActivityHealthContentDashboardBinding
import com.test.my.app.model.wyh.healthContent.GetAllItemsModel
import com.test.my.app.model.wyh.healthContent.GetAllItemsModel.Article
import com.test.my.app.wyh.common.Category
import com.test.my.app.wyh.common.HealthContent
import com.test.my.app.wyh.common.Section
import com.test.my.app.wyh.common.WyhHelper
import com.test.my.app.wyh.healthContent.adapter.HealthBlogsAdapter
import com.test.my.app.wyh.healthContent.adapter.HealthBlogsAdapter.OnBlogsBottomReachedListener
import com.test.my.app.wyh.healthContent.adapter.QuickReadsAdapter
import com.test.my.app.wyh.healthContent.viewmodel.WyhHealthContentViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.full.memberProperties

@AndroidEntryPoint
class HealthContentDashboardActivity : BaseActivity() {

    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var binding: ActivityHealthContentDashboardBinding
    private val wyhHealthContentViewModel: WyhHealthContentViewModel by lazy {
        ViewModelProvider(this)[WyhHealthContentViewModel::class.java]
    }
    private var tab = 0
    var currentPage = 0
    var searchKey = ""
    var category = Constants.BLOGS
    var healthBlogsAdapter: HealthBlogsAdapter? = null
    var quickReadsAdapter: QuickReadsAdapter? = null
    private var categoryList: MutableList<Category> = mutableListOf()

    private val blogDetailsResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Utilities.printData("OnActivityResult",result)
            if ( result.data != null && result.data!!.hasExtra(Constants.IS_CHANGED) && result.data!!.hasExtra(Constants.CATEGORY) ) {
                val isChanged = result.data!!.extras!!.getBoolean(Constants.IS_CHANGED,false)
                val category = result.data!!.extras!!.getString(Constants.CATEGORY,Constants.BLOGS)
                Utilities.printLogError("isChanged--->$isChanged")
                Utilities.printLogError("category--->$category")
                if ( isChanged && category == Constants.QUICK_READS ) {
                    //binding.tabLayout.getTabAt(3)!!.select()
                    stopShimmer()
                    binding.rvBlogs.visibility = View.GONE
                    binding.layoutSearchBlog.visibility = View.GONE
                    wyhHealthContentViewModel.callGetAllItemsApi(currentPage,this@HealthContentDashboardActivity)
                }
            }
        }
    }

    override fun getViewModel(): BaseViewModel = wyhHealthContentViewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityHealthContentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallBack)
        try {
            setUpToolbar()
            initialise()
            setClickable()
            registerObserver()
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }

    private fun initialise() {
        categoryList = WyhHelper.getHealthContentCategoryList(this)
        startShimmer()
        for (category in categoryList) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(category.name))
        }
        healthBlogsAdapter = HealthBlogsAdapter(this,this,blogsListener)
        binding.rvBlogs.adapter = healthBlogsAdapter

        quickReadsAdapter = QuickReadsAdapter(this,this)
        binding.rvQuickReads.adapter = quickReadsAdapter

        //binding.tabLayout.getTabAt(tab)!!.select()
        wyhHealthContentViewModel.callGetAllBlogsApi(currentPage,this@HealthContentDashboardActivity)
        CleverTapHelper.pushEvent(this@HealthContentDashboardActivity, CleverTapConstants.BLOG_ALL_BLOGS_SCREEN)
    }

    private fun setClickable() {

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tb: TabLayout.Tab?) {
                tab = tb!!.position
                category = categoryList[tb.position].code
                currentPage = 0
                searchKey = ""
                Utilities.printLogError("Category--->$category")
                startShimmer()
                binding.edtSearchBlog.clearFocus()
                healthBlogsAdapter!!.clearAdapterList()
                quickReadsAdapter!!.clearAdapterList()
                when(category) {
                    Constants.BLOGS -> {
                        binding.rvQuickReads.visibility = View.GONE
                        binding.layoutSearchBlog.visibility = View.VISIBLE
                        wyhHealthContentViewModel.callGetAllBlogsApi(currentPage,this@HealthContentDashboardActivity)
                        CleverTapHelper.pushEvent(this@HealthContentDashboardActivity, CleverTapConstants.BLOG_ALL_BLOGS_SCREEN)
                    }
                    Constants.VIDEOS -> {
                        binding.rvQuickReads.visibility = View.GONE
                        binding.layoutSearchBlog.visibility = View.VISIBLE
                        wyhHealthContentViewModel.callGetAllVideosApi(currentPage,this@HealthContentDashboardActivity)
                        CleverTapHelper.pushEvent(this@HealthContentDashboardActivity, CleverTapConstants.BLOG_ALL_VIDEOS_SCREEN)
                    }
                    Constants.AUDIOS -> {
                        binding.rvQuickReads.visibility = View.GONE
                        binding.layoutSearchBlog.visibility = View.VISIBLE
                        wyhHealthContentViewModel.callGetAllAudiosApi(currentPage,this@HealthContentDashboardActivity)
                        CleverTapHelper.pushEvent(this@HealthContentDashboardActivity, CleverTapConstants.BLOG_ALL_AUDIOS_SCREEN)
                    }
                    Constants.QUICK_READS -> {
                        stopShimmer()
                        binding.rvBlogs.visibility = View.GONE
                        binding.layoutSearchBlog.visibility = View.GONE
                        wyhHealthContentViewModel.callGetAllItemsApi(currentPage,this@HealthContentDashboardActivity)
                        CleverTapHelper.pushEvent(this@HealthContentDashboardActivity, CleverTapConstants.BLOG_ALL_QUICK_READS_SCREEN)
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
        })

        binding.edtSearchBlog.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                try {
                    Utilities.printLogError("Category--->$category")
                    Utilities.printLogError("SearchKey--->$editable")
                    if (!binding.edtSearchBlog.isPerformingCompletion) {
                        if (editable.toString().length >= 3) {
                            searchKey = editable.toString()
                            currentPage = 0
                            healthBlogsAdapter!!.clearAdapterList()
                            searchBlog()
                        }
                        if (editable.toString() == "") {
                            searchKey = ""
                            currentPage = 0
                            healthBlogsAdapter!!.clearAdapterList()
                            searchBlog()
                        }
                    }
/*                    if (editable.toString() == "") {
                        searchKey = ""
                        currentPage = 0
                        healthBlogsAdapter!!.clearAdapterList()
                        searchBlog()
                    }*/
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

    private fun searchBlog() {
        when(category) {
            Constants.BLOGS -> {
                wyhHealthContentViewModel.callGetAllBlogsApi(currentPage,this@HealthContentDashboardActivity,searchKey)
            }
            Constants.VIDEOS -> {
                wyhHealthContentViewModel.callGetAllVideosApi(currentPage,this@HealthContentDashboardActivity,searchKey)
            }
            Constants.AUDIOS -> {
                wyhHealthContentViewModel.callGetAllAudiosApi(currentPage,this@HealthContentDashboardActivity,searchKey)
            }
        }
    }

    private fun registerObserver() {
        wyhHealthContentViewModel.getAllBlogs.observe(this) {}
        wyhHealthContentViewModel.getAllVideos.observe(this) {}
        wyhHealthContentViewModel.getAllAudios.observe(this) {}
        wyhHealthContentViewModel.getAllItems.observe(this) {}
        wyhHealthContentViewModel.getWyhBlog.observe(this) {}
    }

    fun updateContentList( contentList:MutableList<HealthContent> ) {
        Utilities.printLogError("Category--->$category")
        Utilities.printLogError("CurrentPage--->$currentPage")
        Utilities.printLogError("ContentListSize($category)--->${contentList.size}")
        //Utilities.printData("ContentList",contentList)
        healthBlogsAdapter!!.updateData(contentList)
        val finalList =  healthBlogsAdapter!!.blogList
        if ( finalList.isNotEmpty() ) {
            binding.rvBlogs.visibility = View.VISIBLE
            binding.layoutNoHealthBlogs.visibility = View.GONE
        } else {
            binding.rvBlogs.visibility = View.GONE
            binding.layoutNoHealthBlogs.visibility = View.VISIBLE
        }
    }

    fun renderQuickReadSection( data:GetAllItemsModel.QuickReadData ) {
        try {
            val blogList : MutableList<Section> = mutableListOf()
            val kClass = data::class
            kClass.memberProperties
                .filter { it.name != "allBlogs" && it.name != "allVideo" }
                .forEach { property ->
                    val value = property.getter.call(data)
                    blogList.add(Section(
                        property.name,
                        if (value is List<*>) value.filterIsInstance<Article>() else emptyList()
                    ) )
                }
            Utilities.printData("combinedList",blogList)
            quickReadsAdapter!!.updateData(blogList.filter { it.sectionList.isNotEmpty() }.toMutableList())

            if ( blogList.isNotEmpty() ) {
                binding.rvQuickReads.visibility = View.VISIBLE
                binding.layoutNoHealthBlogs.visibility = View.GONE
            } else {
                binding.rvQuickReads.visibility = View.GONE
                binding.layoutNoHealthBlogs.visibility = View.VISIBLE
            }
            // Dynamically collect all blog lists
            /*            val combinedList = GetAllItemsModel.QuickReadData::class.memberProperties.flatMap { property ->
                            val value = property.get(data)
                            if (value is List<*>) value.filterIsInstance<Article>() else emptyList()
                        }*/
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }

    private val blogsListener = object : OnBlogsBottomReachedListener {
        override fun onBottomReached(position: Int) {
            currentPage += 1
            if ( !Utilities.isNullOrEmpty(binding.edtSearchBlog.text.toString()) ) {
                searchKey = binding.edtSearchBlog.text.toString()
            }  else {
                searchKey = ""
            }
            when(category) {
                Constants.BLOGS -> {
                    wyhHealthContentViewModel.callGetAllBlogsApi(currentPage,this@HealthContentDashboardActivity,searchKey)
                }
                Constants.VIDEOS -> {
                    wyhHealthContentViewModel.callGetAllVideosApi(currentPage,this@HealthContentDashboardActivity,searchKey)
                }
                Constants.AUDIOS -> {
                    wyhHealthContentViewModel.callGetAllAudiosApi(currentPage,this@HealthContentDashboardActivity,searchKey)
                }
            }
        }
    }

    fun viewHealthContentDetail( blog:HealthContent ) {
        val intent = Intent(this,HealthContentDetailsActivity::class.java)
        HealthContentDetailsActivity.category = category
        HealthContentDetailsActivity.healthContent = blog
        HealthContentDetailsActivity.healthContentList = healthBlogsAdapter!!.blogList
        blogDetailsResultLauncher.launch(intent)
        /*        HealthContentDetailsActivity.category = category
                HealthContentDetailsActivity.healthContent = blog
                HealthContentDetailsActivity.healthContentList = healthBlogsAdapter!!.blogList
                openAnotherActivity(destination = NavigationConstants.HEALTH_CONTENT_DETAILS_ACTIVITY) {
                    //putString(Constants.CATEGORY,category)
                }*/
    }

    fun getBlogFromArticleCode( articleCode:String ) {
        wyhHealthContentViewModel.callGetBlogApi(articleCode,this)
    }

    private fun startShimmer() {
        binding.layoutShimmer.startShimmer()
        binding.layoutShimmer.visibility = View.VISIBLE
    }

    fun stopShimmer() {
        binding.layoutShimmer.stopShimmer()
        binding.layoutShimmer.visibility = View.GONE
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = "Health Library"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)
        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressedCallBack.handleOnBackPressed()
        }
        binding.toolBarView.imgHelp.setImageResource(R.drawable.ic_close)
        binding.toolBarView.imgHelp.visibility = View.GONE
        binding.toolBarView.imgHelp.setOnClickListener {
            finish()
        }
    }

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            //routeToHomeScreen()
            finish()
        }
    }

    /*    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_health_content_dashboard)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }*/

}