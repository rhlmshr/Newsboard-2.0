package com.newsboard.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.newsboard.R
import com.newsboard.adapters.AppViewPagerAdapter
import com.newsboard.data.models.sources.Source
import com.newsboard.databinding.FragmentHomeBinding
import com.newsboard.ui.content.HomeListFragment
import com.newsboard.utils.base.BaseFragment
import com.newsboard.utils.base.ResponseState
import com.newsboard.utils.tabMenuCategories
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_toolbar.*

/**
 * Root fragment with tabs and drawer navigation.
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var tabsAdapter: AppViewPagerAdapter
    private lateinit var homeViewModel: HomeViewModel
    private var sourcesList: List<Source> = ArrayList()

    override val layoutId: Int
        get() = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sourcesList.isEmpty()) homeViewModel.getSources()
    }

    override fun init() {
        setHasOptionsMenu(true)

        val tabFragments = mutableListOf<Fragment>()

        tabMenuCategories.forEach {
            tabFragments.add(HomeListFragment.newInstance(it))
        }

        tabsAdapter = AppViewPagerAdapter(childFragmentManager, tabFragments, lifecycle)
    }

    override fun initLiveData() {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        homeViewModel.sourcesLiveData.observe(this, Observer {
            if (it is ResponseState.Success) {
                sourcesList = it.output
                val drawerMenu = dataBinding.navSurvey.menu
                it.output.forEach { source ->
                    drawerMenu.add(source.name)
                }
            }
        })
    }

    override fun setupViews() {
        (activity as AppCompatActivity).setSupportActionBar(tl_home)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(
            context?.getDrawable(
                R.drawable.ic_menu
            )
        )

        setupViewPager()

        drawerToggle = ActionBarDrawerToggle(
            activity, dataBinding.drawerLayout,
            R.string.open_drawer, R.string.close_drawer
        )
        dataBinding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    private fun setupViewPager() {
        dataBinding.vpHome.adapter = tabsAdapter

        TabLayoutMediator(tbl_home, dataBinding.vpHome) { tab: TabLayout.Tab, i: Int ->
            tab.text = tabMenuCategories[i]
        }.attach()
    }

    override fun setListeners() {
        dataBinding.navSurvey.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) return true
        return when (item.itemId) {
            R.id.menu_search -> {
                findNavController().navigate(R.id.action_homeFragment_to_sourceArticleListFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val selectedSource = sourcesList[sourcesList.indexOfFirst {
            it.name == item.title
        }]
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToSourceArticleListFragment(
                selectedSource.id,
                selectedSource.name ?: ""
            )
        )
        drawer_layout.closeDrawer(GravityCompat.START, true)
        return true
    }

    override fun onDestroyView() {
        dataBinding.vpHome.adapter = null
        super.onDestroyView()
    }
}