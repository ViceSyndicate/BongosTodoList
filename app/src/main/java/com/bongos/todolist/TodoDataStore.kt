package com.bongos.todolist

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore

val Context.todoDataStore: DataStore<TodoList> by dataStore(
    fileName = "todo.json",
    serializer = TodoSerializer
)