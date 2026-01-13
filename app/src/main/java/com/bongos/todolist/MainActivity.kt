package com.bongos.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextField
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.map

import com.bongos.todolist.ui.theme.BongosTodoListTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val dataStore by lazy {
        applicationContext.todoDataStore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

            var showDialog by remember { mutableStateOf(false) }
            var todoItems by remember { mutableStateOf<List<TodoItem>>(emptyList()) }
            var scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                dataStore.data.map { it.items }.collect { items ->
                    todoItems = items
                }
            }

            BongosTodoListTheme {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showDialog = true }
                        ) {
                            Text("+")
                        }
                    }
                ) { innerPadding ->

                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        items(todoItems) { item ->
                            Item(
                                item = item,
                                onCheckedChange = { checked ->
                                    scope.launch {
                                        dataStore.updateData { current ->
                                            current.copy(
                                                items = current.items.map {
                                                    if (it == item) it.copy(isDone = checked) else it
                                                }
                                            )
                                        }
                                    }
                                },
                                onDelete = {
                                    scope.launch {
                                        dataStore.updateData { current ->
                                            current.copy(items = current.items - item)
                                        }
                                    }
                                    /*todoItems = todoItems - item*/
                                },
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    if(showDialog) {
                        AddItemDialog(
                            onAdd = { text ->
                                showDialog = false
                                scope.launch {
                                    dataStore.updateData { current ->
                                        current.copy(items = current.items + TodoItem(text))
                                    }
                                }
                            },
                            onDismiss = { showDialog = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddItemDialog(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
        text = {
            TextField(
                value = text,
                onValueChange = {text = it},
                placeholder = { Text("Enter text") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onAdd(text)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun Item(item: TodoItem,
         onCheckedChange: (Boolean) -> Unit,
         onDelete: () -> Unit,
         modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete"
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    textAlign = TextAlign.Center,
                    text = item.text,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Checkbox(
                checked = item.isDone,
                onCheckedChange = onCheckedChange
            )
        }

    }
}