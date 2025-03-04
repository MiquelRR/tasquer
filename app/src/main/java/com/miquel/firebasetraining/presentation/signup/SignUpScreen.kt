package com.miquel.firebasetraining.presentation.signup

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.miquel.firebasetraining.R
import com.miquel.firebasetraining.ui.theme.Black
import com.miquel.firebasetraining.ui.theme.SelectedField
import com.miquel.firebasetraining.ui.theme.UnselectedField

@Composable
fun SignUpScreen(auth: FirebaseAuth, navigateBack: () -> Unit, goToHome: () -> Unit) {

    var email: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Black)
            .padding(horizontal = 32.dp)
    ){
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "back icon",
            tint = White,
            modifier = Modifier
                .padding(vertical = 24.dp)
                .size(24.dp)
                .clickable {
                    navigateBack()
                }
        )
        Text(
            text = "Email",
            color = White,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField,
            )
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text(text = "Password", color = White, fontWeight = FontWeight.Bold, fontSize = 40.sp )
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = UnselectedField,
                focusedContainerColor = SelectedField,
            )
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = {
            auth.signOut()
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(){
                if(it.isSuccessful){
                    Toast.makeText( context , "Create successful", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText( context , "Create failed", Toast.LENGTH_SHORT).show()
                }
                Toast.makeText( context , auth.currentUser?.uid.toString(), Toast.LENGTH_SHORT).show()

            }
        }){
            Text(text = "Sign Up", color = White, fontWeight = FontWeight.Bold)
        }
    }

}