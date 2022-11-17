package com.example.storyins.ui.main.detail

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.storyins.R
import com.example.storyins.databinding.ActivityDetailStoryBinding
import com.example.storyins.source.model.local.entity.StoryEntity
import com.example.storyins.utils.Helpers.formatDate


class DetailStoryActivity : AppCompatActivity(), View.OnTouchListener {

    private lateinit var detailStoryBinding: ActivityDetailStoryBinding
    private lateinit var data : StoryEntity

    companion object {
        const val DETAIL_STORY = "DETAIL_STORY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailStoryBinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(detailStoryBinding.root)


        data = intent.getParcelableExtra(DETAIL_STORY)!!

        initView()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(){

        detailStoryBinding.ibBack.setOnClickListener {
            onBackPressed()
        }

        detailStoryBinding.ivStory.setOnTouchListener(this)

        detailStoryBinding.tvName.text = data.name
        detailStoryBinding.tvDescription.text = data.description
        val date = formatDate(data.createdAt)
        detailStoryBinding.tvDate.text = getString(R.string.date , date)

        Glide.with(this)
            .load(data.photoUrl)
            .placeholder(R.drawable.shape_background_image)
            .apply(RequestOptions())
            .into(detailStoryBinding.ivStory)

    }

    override fun onTouch(view: View, me: MotionEvent): Boolean {
        when(me.action){
            MotionEvent.ACTION_UP -> {
                detailStoryBinding.llNameDate.visibility= View.VISIBLE
                detailStoryBinding.ibBack.visibility = View.VISIBLE
                detailStoryBinding.tvDescription.visibility = View.VISIBLE
            }
            MotionEvent.ACTION_DOWN -> {
                detailStoryBinding.llNameDate.visibility= View.GONE
                detailStoryBinding.ibBack.visibility = View.GONE
                detailStoryBinding.tvDescription.visibility = View.GONE
            }
        }
        return true
    }


}