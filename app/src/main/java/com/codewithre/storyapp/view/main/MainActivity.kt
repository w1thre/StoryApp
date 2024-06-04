package com.codewithre.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.codewithre.storyapp.R
import com.codewithre.storyapp.adapter.StoryAdapter
import com.codewithre.storyapp.data.remote.response.ListStoryItem
import com.codewithre.storyapp.databinding.ActivityMainBinding
import com.codewithre.storyapp.view.ViewModelFactory
import com.codewithre.storyapp.view.createstory.CreateStoryActivity
import com.codewithre.storyapp.view.detail.DetailActivity
import com.codewithre.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    private val storyAdapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        setupRecyclerView()
        refreshData()

        viewModel.errMsg.observe(this) { err ->
            err?.let {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.listStory.observe(this) { stories ->
            storyAdapter.submitList(stories)
            showLoading(false)
            showNotFound(stories.isNullOrEmpty())
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_add -> {
                    val intent = Intent(this@MainActivity, CreateStoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_lang -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                R.id.menu_logout -> {
                    viewModel.logout()
                    true
                }
                else -> false
            }
        }

    }

    private fun refreshData() {
        binding.swipeToRefresh.setOnRefreshListener {
            viewModel.getStories()
            Toast.makeText(this, getString(R.string.success_update_data), Toast.LENGTH_SHORT).show()
            binding.swipeToRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)
        binding.rvStory.adapter = storyAdapter

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(EXTRA_ID, data.id)
                startActivity(intent)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showNotFound(isEmpty: Boolean) {
        binding.tvNotFound.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}