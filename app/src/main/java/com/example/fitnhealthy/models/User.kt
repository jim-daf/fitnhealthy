package com.example.fitnhealthy



data class User(val username: String? = null,
                val email: String? = null,
                val experience: String? = null,
                val file: File? = null,
                val ui_theme_choice: String? = null,
                val age: Long? = null,
                val height: Float? = null,
                val weight: Float? = null,
                val gender: String? = null,
                val Target: String? = null,
                val audioList: ArrayList<String>? =null,

) {
    data class File(
        val fileUrl: String? = null,
        val filename: String? = null

    )

}