package com.bongos.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.flow.map

import com.bongos.todolist.ui.theme.BongosTodoListTheme
import kotlinx.coroutines.launch

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.Brightness5

@OptIn(ExperimentalMaterial3Api::class)
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
            var isDarkMode by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                dataStore.data.map { it.items }.collect { items ->
                    todoItems = items
                }
            }

            BongosTodoListTheme(darkTheme = isDarkMode) {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Bongos Todo List") },
                            actions = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    if (isDarkMode)
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Brightness5,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.Brightness3,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                    }

                                    Switch(
                                        checked = isDarkMode,
                                        onCheckedChange = { isDarkMode = it }
                                    )
                                }
                            }
                        )
                    },
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
fun Item(
    item: TodoItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (item.isDone) 0.6f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "alpha animation")

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row (
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete"
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = item.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (item.isDone)
                            TextDecoration.LineThrough
                        else
                            TextDecoration.None
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.Center,
                    maxLines = Int.MAX_VALUE,
                    modifier = Modifier.graphicsLayer {
                        // Animate scale slightly when checked
                        scaleX = if (item.isDone) 0.98f else 1f
                        scaleY = if (item.isDone) 0.98f else 1f
                    }
                )
            }
            Checkbox(
                checked = item.isDone,
                onCheckedChange = onCheckedChange
            )
        }

    }
}