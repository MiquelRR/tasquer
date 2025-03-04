package com.miquel.firebasetraining.presentation.home

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.miquel.firebasetraining.data.Repository
import com.miquel.firebasetraining.data.TaskItem


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen6(auth: FirebaseAuth, navigateBack: () -> Unit) {
    val context = LocalContext.current
    if (auth.currentUser != null) {
        Toast.makeText(context, "Benvingut ${auth.currentUser!!.uid}", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Tu qui eres?", Toast.LENGTH_SHORT).show()
        navigateBack()
    }
    val db : FirebaseFirestore = Repository.getInstance().db
    val today: Timestamp = Timestamp.now()
    val newTask = TaskItem(
        date = today,
        description = "Hola",
        isDone = false,
        assignedUser = auth.currentUser!!.uid,
        duration = 100,
        email = auth.currentUser!!.email
    )
    Toast.makeText(context, "$newTask", Toast.LENGTH_SHORT).show()

    Button(onClick =  {
        val list=db.collection("tasks").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("aris", "${document.id} => ${document.data}")
                }
            }
            .addOnCompleteListener{
                Log.d("aris", "Completed ????")
            }
            .addOnFailureListener { exception ->
                Log.w("aris", "Error getting documents.", exception)
            }

        db.collection("tasks").add(newTask)
            .addOnSuccessListener {
                Log.d("aris", "DocumentSnapshot added with ID: ${it.id}")
            }
            .addOnFailureListener { e ->
                Log.w("aris", "Error adding document", e)
            }
    }){
        Text("Afegir")
    }
}