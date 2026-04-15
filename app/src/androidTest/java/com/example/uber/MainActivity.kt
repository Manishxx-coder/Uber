package com.example.uber

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UberApp()
        }
    }
}

@Composable
fun UberApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // Login Screen
        composable("login") {
            LoginScreen {
                navController.navigate("home")
            }
        }

        // Home Screen
        composable("home") {
            HomeScreen { fare, vehicle ->
                navController.navigate("confirm/$fare/$vehicle")
            }
        }

        // Confirm Screen
        composable("confirm/{fare}/{vehicle}") { backStackEntry ->
            val fare = backStackEntry.arguments?.getString("fare") ?: "0"
            val vehicle = backStackEntry.arguments?.getString("vehicle") ?: ""

            RideConfirmedScreen(fare, vehicle)
        }
    }
}

//////////////////////////////////////////////////
// 🔐 LOGIN SCREEN
//////////////////////////////////////////////////

@Composable
fun LoginScreen(onLogin: () -> Unit) {

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Login", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}

//////////////////////////////////////////////////
// 🏠 HOME SCREEN (BOOK + VEHICLE + FARE)
//////////////////////////////////////////////////

@Composable
fun HomeScreen(onConfirmRide: (String, String) -> Unit) {

    var pickup by remember { mutableStateOf(TextFieldValue("")) }
    var drop by remember { mutableStateOf(TextFieldValue("")) }
    var distance by remember { mutableStateOf(TextFieldValue("")) }
    var selectedVehicle by remember { mutableStateOf("Bike") }
    var fare by remember { mutableStateOf(0.0) }

    fun calculateFare(): Double {
        val dist = distance.text.toDoubleOrNull() ?: 0.0
        val rate = when (selectedVehicle) {
            "Bike" -> 5
            "Car" -> 10
            "Auto" -> 8
            else -> 5
        }
        return dist * rate
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Map Placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Map View", fontSize = 18.sp)
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp)
        ) {

            Column(modifier = Modifier.padding(16.dp)) {

                Text("Book Ride", fontSize = 20.sp)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pickup,
                    onValueChange = { pickup = it },
                    label = { Text("Pickup Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = drop,
                    onValueChange = { drop = it },
                    label = { Text("Drop Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = distance,
                    onValueChange = { distance = it },
                    label = { Text("Distance (km)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text("Choose Vehicle")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    VehicleButton("Bike", selectedVehicle) {
                        selectedVehicle = "Bike"
                        fare = calculateFare()
                    }

                    VehicleButton("Car", selectedVehicle) {
                        selectedVehicle = "Car"
                        fare = calculateFare()
                    }

                    VehicleButton("Auto", selectedVehicle) {
                        selectedVehicle = "Auto"
                        fare = calculateFare()
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { fare = calculateFare() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Calculate Fare")
                }

                Text("Estimated Fare: ₹$fare", fontSize = 18.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onConfirmRide(fare.toString(), selectedVehicle)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm Ride")
                }
            }
        }
    }
}

@Composable
fun VehicleButton(name: String, selected: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (name == selected) Color.Black else Color.Gray
        )
    ) {
        Text(name, color = Color.White)
    }
}

//////////////////////////////////////////////////
// 🚕 RIDE CONFIRMED SCREEN
//////////////////////////////////////////////////

@Composable
fun RideConfirmedScreen(fare: String, vehicle: String) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🎉 Ride Confirmed!\nVehicle: $vehicle\nFare: ₹$fare\nDriver is on the way 🚗",
            fontSize = 22.sp
        )
    }
}
