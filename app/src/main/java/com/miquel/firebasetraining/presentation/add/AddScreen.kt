package com.miquel.firebasetraining.presentation.add

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.data.DataBufferObserver.Observable
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.miquel.firebasetraining.data.Repository
import com.miquel.firebasetraining.data.TaskItem
import com.miquel.firebasetraining.presentation.home.AppTopBar
import com.miquel.firebasetraining.ui.theme.SelectedField
import com.miquel.firebasetraining.ui.theme.UnselectedField


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddScreen(auth: FirebaseAuth, navigateBack: () -> Unit ){
    var description by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var date: Timestamp by remember { mutableStateOf(Timestamp.now()) }
    var timeStr by remember { mutableStateOf("") }

    var dateEdited by remember { mutableStateOf(false) }
    var formMustBeCleared by remember { mutableStateOf(false) }
    fun resetForm() {
        date = Timestamp.now()
        description = ""
        link = ""
        timeStr = ""
        dateEdited = false
        formMustBeCleared = true
    }
    Scaffold (
        topBar = { AppTopBar(auth) },
        bottomBar = {
            ReducedBottomNavigationBar(
                navigateBack = navigateBack,
                addAction = { addTask(timeStr,description, link, auth, date, { resetForm() })},
                clearForm = {resetForm()})
        }

            ){padding ->
                AddTaskForm(
                    dateEdited = dateEdited,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                    , onDescriptionChange = { description = it }
                    , onLinkChange = { link = it }
                    ,onDateChange = { date = it }
                    ,onTimeChange = { timeStr = it }
                    ,onClearForm = {
                        description = ""
                        link = ""
                        timeStr = ""
                        dateEdited = false
                    },
                    onDateEdited = { dateEdited = it }
                )
            }
        }

fun addTask(timeStr: String, description: String, link: String, auth: FirebaseAuth, date: Timestamp, resetForm: () -> Unit){
    val repository: Repository = Repository.getInstance()
    val task = TaskItem(
        duration = timeStr.toInt(),
        description = description,
        link = link,
        email = auth.currentUser?.email,
        isDone = false,
        assignedUser = auth.currentUser?.uid,
        date = date,
    )
    CoroutineScope(Dispatchers.IO).launch {
        repository.addTask(task)
        Log.d("AddTaskForm", "Task added: $task")
    }
    resetForm()
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskForm(
    modifier: Modifier = Modifier, dateEdited: Boolean = false,
    onDescriptionChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    onDateChange: (Timestamp) -> Unit,
    onTimeChange: (String) -> Unit,
    onClearForm: () -> Unit,
    onDateEdited: (Boolean) -> Unit) {

    val datePickerState = rememberDatePickerState()
    val openDialog = remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var timeError by remember { mutableStateOf(false) }

    if (openDialog.value){
        DatePickerDialog(onDismissRequest = { openDialog.value = false },
            confirmButton = {
                Button(onClick = {
                    onDateChange(Timestamp(datePickerState.selectedDateMillis!!/1000, 0))
                    openDialog.value = false
                    onDateEdited(true)
                }) {
                    Text("Ok")
                }
            }) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = modifier
            .background(Color.LightGray)
            .padding(horizontal = 32.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            placeholder = { Text(text = "Descripció") },
            textStyle = TextStyle(fontSize = 24.sp),
            value = description,
            onValueChange = {
                onDescriptionChange(it)
                description = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            placeholder = { Text(text = "Link") },
            textStyle = TextStyle(fontSize = 24.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            value = link,
            onValueChange = {
                onLinkChange(it)
                link = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            //verticalAlignment = Alignment.CenterVertically,
            //horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(onClick = {openDialog.value = true; }) {
                Icon(
                    imageVector = Icons.Filled.DateRange, contentDescription = "calendar", modifier = Modifier.size(48.dp),
                    tint = if (dateEdited) Color.Blue else Color.Red
                )
            }
            Spacer(modifier = Modifier.weight(0.05f))
            TextField(
                placeholder = { Text(text = "temps (minuts)") },
                textStyle = TextStyle(fontSize = 24.sp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = time,
                onValueChange = {
                    onTimeChange(it)
                    timeError = !isValidNumber(it)
                    time = it },
                isError = timeError,
                supportingText = {
                    if (timeError) {
                        Text(text = "El temps ha de ser un número", color = Color.Red)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = UnselectedField,
                    focusedContainerColor = SelectedField,
                )
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

    }


}
@Composable
fun ReducedBottomNavigationBar(
    navigateBack: () -> Unit,
    addAction: () -> Unit,
    clearForm: () -> Unit
) {

    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { addAction(); navigateBack() },
            label = { Text("Afegir") },
            icon = { Icon(Icons.Outlined.Add, contentDescription = "All") }
        )

        NavigationBarItem(
            selected = false            ,
            onClick = {
                navigateBack()
            },
            label = { Text("Eixir") },
            icon = { Icon(Icons.Outlined.ArrowBack, contentDescription = "Exit") }
        )
    }
}
fun isValidNumber(str: String): Boolean {
    return try {
        str.toInt()
        true
    } catch (e: NumberFormatException){
        false
    }
}