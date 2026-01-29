package com.bongos.todolist

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore

val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "settings.json",
    serializer = SettingsSerializer
)