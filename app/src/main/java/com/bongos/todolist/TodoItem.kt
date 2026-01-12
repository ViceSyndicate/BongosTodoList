package com.bongos.todolist

import kotlinx.serialization.Serializable

@Serializable
data class TodoItem(
    val text: String,
    val isDone: Boolean = false
)

@Serializable
data class TodoList(
    val items: List<TodoItem> = emptyList()
)