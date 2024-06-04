package com.codewithre.storyapp.view.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codewithre.storyapp.databinding.ActivityDetailBinding
import com.codewithre.storyapp.view.ViewModelFactory
import com.codewithre.storyapp.view.main.MainActivity.Companion.EXTRA_ID

class DetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getStringExtra(EXTRA_ID)
        if (id != null) {
            viewModel.getDetailStories(id)
        }
        viewModel.detailStory.observe(this) {
            binding.apply {
                tvDetailName.text = it?.name ?: "Null"
                tvDetailDescription.text = it?.description ?: "Null"
            }
            Glide.with(this@DetailActivity)
                .load(it?.photoUrl)
                .centerCrop()
                .into(binding.ivDetailPhoto)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }



    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}