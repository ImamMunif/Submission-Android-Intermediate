package com.dicoding.storysubmission.data.response

import com.google.gson.annotations.SerializedName

data class StoryUploadResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)