package com.miquel.firebasetraining.presentation.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.miquel.firebasetraining.data.Repository
import com.miquel.firebasetraining.data.TaskItem
import com.miquel.firebasetraining.data.localDateToDate
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(auth: FirebaseAuth, navigateBack: () -> Unit, navigateToAdd: () -> Unit ) {
    val taskViewModel: TaskViewModel = viewModel()


    //Toast.makeText(LocalContext.current, auth.currentUser?.uid, Toast.LENGTH_SHORT).show()
    // Fetch tasks when the composable is first launched
    LaunchedEffect(key1 = auth.currentUser?.uid) {
        auth.currentUser?.uid?.let { taskViewModel.getTasksForUser(it) }
    }

    //var selectedFilter = taskViewModel.filter

    var selectedFilter by remember { mutableStateOf("All") }
    var showExitDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {AppTopBar(auth)},
        bottomBar = {
            BottomNavigationBar(
                selectedFilter,
                onFilterSelected = { filter ->
                taskViewModel.updateFilter(filter)},
                onLogout ={ showExitDialog = true },
                navigateToAdd = navigateToAdd
            )
        }
    )
    { padding ->
        TaskList(taskList = taskViewModel.taskList, modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        )
    }
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Eixir ") },
            text = { Text("Vols eixir del teu comte?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    Log.d("TAG", "HomeScreen before signout : ${auth.currentUser?.uid}")
                    auth.signOut()
                    Log.d("TAG", "HomeScreen after signout : ${auth.currentUser?.uid}")
                    navigateBack() // Call the navigateBack function
                }) {
                    Text("Eixir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskList(taskList: List<TaskItem>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(taskList) { task ->
            TaskItemCard(task = task, onTaskStatusChange = { isChecked ->
                run {
                    task.isDone = isChecked
                    CoroutineScope(Dispatchers.IO).launch {
                        Repository.getInstance().updateTask(task)
                    }
                }
            })
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(auth: FirebaseAuth){

    val viewModel: TaskViewModel = viewModel()
    var taskAmount by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = auth.currentUser?.uid) {
        viewModel.getTasksAmountForUser(auth.currentUser?.uid)
    }

    taskAmount = viewModel.amount

    TopAppBar(
        title = {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = auth.currentUser?.email ?: "Usuario no autenticado",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Tasques: $taskAmount", modifier = Modifier.weight(1f))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
@Composable
fun BottomNavigationBar(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onLogout: () -> Unit,
    navigateToAdd: () -> Unit
) {
    var selection by remember { mutableStateOf("All") }
    NavigationBar {
        NavigationBarItem(
            selected = selection == "Today",
            onClick = {
                selection="Today"
                onFilterSelected(selection)
                      },
            label = { Text("Hui") },
            icon = { Icon(Icons.Outlined.DateRange, contentDescription = "Today") }
        )
        NavigationBarItem(
            selected = selection == "All",
            onClick = {
                selection="All"
                onFilterSelected(selection)
                      },
            label = { Text("Totes") },
            icon = { Icon(Icons.Outlined.Info, contentDescription = "All") }
        )
        NavigationBarItem(
            selected = false            ,
            onClick = { navigateToAdd()},
            label = { Text("Afegir") },
            icon = { Icon(Icons.Outlined.Add, contentDescription = "All") }
        )

        NavigationBarItem(
            selected = false            ,
            onClick = {
               onLogout()
            },
            label = { Text("Eixir") },
            icon = { Icon(Icons.Outlined.ArrowBack, contentDescription = "Exit") }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItemCard(task: TaskItem, onTaskStatusChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    var isChecked by remember { mutableStateOf(task.isDone ?: false) }
    val formattedDate = task.date?.toDate()?.let { date ->
        SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(date)
    } ?: "Sin fecha"

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val stateText = if (isChecked) "Completada  " else "Pendent  "
        val today = localDateToDate(LocalDate.now())
        val delayed = !isChecked && task.date?.toDate()?.before(today) ?: false
        val dateColor : Color = if (delayed) Color.Red else Color.Gray
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formattedDate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = dateColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stateText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Switch(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = it
                        task.isDone=it

                        onTaskStatusChange(it) // Notifica el cambio de estado
                    }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.description ?: "Sin descripción",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))
            Row( verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()){
                Text(
                    text = "Estimat: ${task.duration ?: 0} min",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        task.link?.let { url -> // Verificamos que exista un enlace válido
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent) // Navegamos al navegador
                        }
                    },
                    enabled = !task.link.isNullOrEmpty() // Solo habilitamos si hay enlace
                ) {
                    Text(text = "documents") // Texto del botón
                }
            }

        }
    }
}



