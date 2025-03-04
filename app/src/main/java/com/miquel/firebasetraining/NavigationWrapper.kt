package com.miquel.firebasetraining

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.miquel.firebasetraining.presentation.add.AddScreen
import com.miquel.firebasetraining.presentation.home.HomeScreen
import com.miquel.firebasetraining.presentation.initial.InitialScreen
import com.miquel.firebasetraining.presentation.login.LoginScreen
import com.miquel.firebasetraining.presentation.signup.SignUpScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationWrapper(navHostController: NavHostController, auth: FirebaseAuth) {
    val destination: String = if (auth.currentUser != null) "home" else "logIn"
    //Log.d("NavigationWrapper", auth.currentUser!!.uid)
    NavHost(navController = navHostController, startDestination = destination) {
        composable("initial") {
            InitialScreen(navigateToLogin = { navHostController.navigate("logIn") },
                navigateToSignUp = { navHostController.navigate("signUp") })
        }
        composable("logIn") {
            LoginScreen(auth, navigateBack = { navHostController.popBackStack() },goToHome = { navHostController.navigate("home") })
        }
        composable("signUp") {
            SignUpScreen(auth, navigateBack = { navHostController.popBackStack() }, goToHome = { navHostController.navigate("home") })
        }
        composable("home") {
            HomeScreen(auth, navigateBack = { navHostController.navigate("logIn") }, navigateToAdd = { navHostController.navigate("addScreen") })
        }
        composable("addScreen") {
            AddScreen(auth, navigateBack = { navHostController.navigate("home") })
        }
    }

}



