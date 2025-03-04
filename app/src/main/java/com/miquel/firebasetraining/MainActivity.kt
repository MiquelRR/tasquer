package com.miquel.firebasetraining

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth

import com.miquel.firebasetraining.ui.theme.FirebaseTrainingTheme


class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private lateinit var auth : FirebaseAuth
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)


        auth= Firebase.auth
        enableEdgeToEdge()
        setContent {

            FirebaseTrainingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    navHostController = rememberNavController()
                    NavigationWrapper(navHostController, auth)
                }
            }
        }

    }

}
