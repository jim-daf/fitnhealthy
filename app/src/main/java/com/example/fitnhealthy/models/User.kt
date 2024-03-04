package com.example.fitnhealthy

data class User(val username: String? = null,
                val email: String? = null,
                val experience: String? = null,
                val file: File? = null,
                val ui_theme_choice: String? = null,
                val age: Int? = null,
                val height: Float? = null,
                val weight: Float? = null,
) {
    data class File(
        val fileUrl: String? = null,
        val filename: String? = null

    )

}