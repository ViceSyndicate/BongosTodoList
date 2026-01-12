package com.bongos.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign

import com.bongos.todolist.todoDataStore;
import com.bongos.todolist.TodoItem;
import com.bongos.todolist.ui.theme.BongosTodoListTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

            var showDialog by remember { mutableStateOf(false) }
            var todoItems by remember { mutableStateOf<List<TodoItem>>(emptyList()) }

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
                                    todoItems = todoItems.map {
                                        if (it == item) it.copy(isDone = checked) else it
                                    }
                                },
                                onDelete = {
                                    todoItems = todoItems - item
                                },
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    if(showDialog) {
                        AddItemDialog(
                            onAdd = { text ->
                                showDialog = false
                                todoItems = todoItems + TodoItem(text)
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