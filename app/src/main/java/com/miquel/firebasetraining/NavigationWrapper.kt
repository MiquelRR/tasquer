package com.miquel.firebasetraining

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.miquel.firebasetraining.presentation.add.AddScreen
import com.miquel.firebasetraining.presentation.home.HomeScreen
import com.miquel.firebasetraining.presentation.login.LoginScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationWrapper(navHostController: NavHostController, auth: FirebaseAuth) {

    NavHost(navController = navHostController, startDestination = if (auth.currentUser == null) "logIn" else "home") {

        composable("logIn") {
            LoginScreen(auth, navigateBack = { navHostController.popBackStack() },goToHome = { navHostController.navigate("home") })
        }
        composable("home") {
            HomeScreen(auth, navigateBack = { navHostController.navigate("logIn") }, navigateToAdd = { navHostController.navigate("addScreen") })

        }
        composable("addScreen") {

            AddScreen(auth, navigateBack = { navHostController.navigate("home") })

        }
    }

}



