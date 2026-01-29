package com.bongos.todolist

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val isDarkTheme: Boolean = false
)