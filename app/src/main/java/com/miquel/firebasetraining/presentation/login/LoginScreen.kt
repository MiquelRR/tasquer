package com.miquel.firebasetraining.presentation.login

import android.util.Log
import android.util.Patterns
//import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.util.Logger
import com.miquel.firebasetraining.R
import com.miquel.firebasetraining.ui.theme.Black
import com.miquel.firebasetraining.ui.theme.Pink80
import com.miquel.firebasetraining.ui.theme.Purple40
import com.miquel.firebasetraining.ui.theme.SelectedField
import com.miquel.firebasetraining.ui.theme.UnselectedField

private fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

private fun isValidPassword(password: String): Boolean {
    //the only condition is that the password is greather than 6 chars
    val passwordRegex = "^[^\\s]{6,}$"
    return password.matches(passwordRegex.toRegex())
}


@Composable
fun LoginScreen(auth: FirebaseAuth, navigateBack: () -> Boolean, goToHome: () -> Unit) {
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var email: String by remember { mutableStateOf("")}
    var password: String by remember { mutableStateOf("")}
    var dialogMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column( modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray)
        .padding(horizontal = 32.dp)
    ){

        Spacer(modifier = Modifier.height(48.dp))

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.tasky), contentDescription = "logo")
            Text(text = "Tasquer", color = Color.DarkGray, fontWeight = FontWeight.Bold, fontSize = 40.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            placeholder =  { Text(text = "el teu email" )},
            textStyle = TextStyle(fontSize = 24.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            value = email,
            onValueChange ={
                email=it.trim()
                emailError = !isValidEmail(email) },
            isError = emailError, // Set isError based on emailError state
            supportingText = { // Show error message if emailError is true
                if (emailError) {
                    Text(text = "introdueix un email vàlid", color = Color.Red)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Tab) {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                    false
                }
            ,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            placeholder = { Text(text = "la teua password") },
            textStyle = TextStyle(fontSize = 24.sp),
            visualTransformation = PasswordVisualTransformation(),
            value = password,
            onValueChange = {
                password = it.trim()
                passwordError = !isValidPassword(password)
                            },
            isError = passwordError,
            supportingText = {
                if (passwordError) {
                    Text(text = "almenys 6 caraters", color = Color.Red)
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Tab) {
                        focusManager.moveFocus(FocusDirection.Up)
                    }
                    false
                }
            ,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            Button(colors = ButtonDefaults.buttonColors(containerColor = Pink80),
                onClick = {
                    if(isValidEmail(email) && isValidPassword(password)){
                        auth.signOut()
                        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(){
                            if(it.isSuccessful){
                                Log.d("GOHOME", "Login successful")
                                //Toast.makeText( context , "Login successful", Toast.LENGTH_SHORT).show()
                                goToHome()
                            } else {
                                dialogMessage="Comprova credencials, o obre un nou compte"
                                //Toast.makeText( context , "Login failed >$email< & >$password<", Toast.LENGTH_SHORT).show()
                                Log.d("errorLog", "Login failed >$email< & >$password<")
                            }
                        }
                    }
                }) {
                Text(text = "Log In", color = DarkGray, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Password oblidada",
            color = if(isValidEmail(email)) Purple40 else Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = if (isValidEmail(email)) {
                Modifier.clickable {
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("successLog", "Email sent.")
                                dialogMessage="Comprova el email per a canviar la password"
                                // Optionally, show a success message to the user
                            } else {
                                Log.w("errorLog", "sendPasswordResetEmail:failure", task.exception)
                                dialogMessage="Error al enviar el email"
                                // Optionally, show an error message to the user
                            }
                        }
                }
            } else {
                Modifier
            },
            style = TextStyle(
                textDecoration = if (isValidEmail(email)) TextDecoration.Underline else TextDecoration.None
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Obrir nou comte",
            color = if (isValidEmail(email) && isValidPassword(password)) Purple40 else Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = if(isValidEmail(email) && isValidPassword(password)) {
                Modifier.clickable {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("GOHOME", "JUST CREATED USER")
                                goToHome()
                            } else {
                                dialogMessage="Error al crear el compte"
                                // If sign in fails, display a message to the user.
                                Log.w("errorLog", "createUserWithEmail:failure", task.exception)
                            }
                        }
                }
            } else {
                Modifier
            },
            style = TextStyle(
                textDecoration = if (isValidEmail(email) && isValidPassword(password)) TextDecoration.Underline else TextDecoration.None
            )
        )
    }

    if (dialogMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { dialogMessage = "" },
            title = { Text("Tasquer login") },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = {
                    dialogMessage = ""
                    auth.signOut()
                    //navigateBack() // Call the navigateBack function que fillsdepuis
                }) {
                    Text("Eixir")
                }
            }

        )
    }



}



