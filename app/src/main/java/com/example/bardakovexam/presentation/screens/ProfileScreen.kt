package com.example.bardakovexam.presentation.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bardakovexam.presentation.navigation.navRoutes
import com.example.bardakovexam.presentation.viewModels.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    var editMode by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val user = viewModel.user.value
    val errorMessage = viewModel.errorMessage.value
    val displayName = listOf(user.firstname, user.lastname).filter { !it.isNullOrBlank() }.joinToString(" ").ifBlank { "User" }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.user.value = user.copy(photo = uri.toString())
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bitmap = bmp
    }

    Box(modifier = Modifier.fillMaxSize().background(AppBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 18.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Профиль", color = AppText, fontSize = 22.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Box(
                    modifier = Modifier.size(36.dp).background(AppBlue, CircleShape).clickable { editMode = !editMode },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✎", color = Color.White, fontSize = 16.sp, modifier = Modifier.align(Alignment.Center))
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                when {
                    bitmap != null -> Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = null, modifier = Modifier.size(120.dp).clip(CircleShape))
                    !user.photo.isNullOrBlank() -> AsyncImage(model = user.photo, contentDescription = null, modifier = Modifier.size(120.dp).clip(CircleShape))
                    else -> Box(modifier = Modifier.size(120.dp).background(AppBlue, CircleShape), contentAlignment = Alignment.Center) {
                        Text(displayName.take(1).uppercase(), color = Color.White, fontSize = 36.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(displayName, color = AppText, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            TextButton(onClick = { showDialog = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Изменить фото профиля", color = AppBlue, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            BarcodePanel(
                data = user.id.ifBlank { "1234567890" },
                barcodeHeight = 52.dp,
                onClick = { navController.navigate(navRoutes.loyalty) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            AuthField(label = "Имя", value = user.firstname.orEmpty(), onValueChange = { if (editMode) viewModel.user.value = user.copy(firstname = it) })
            Spacer(modifier = Modifier.height(16.dp))
            AuthField(label = "Фамилия", value = user.lastname.orEmpty(), onValueChange = { if (editMode) viewModel.user.value = user.copy(lastname = it) })
            Spacer(modifier = Modifier.height(16.dp))
            AuthField(label = "Адрес", value = user.address.orEmpty(), onValueChange = { if (editMode) viewModel.user.value = user.copy(address = it) })
            Spacer(modifier = Modifier.height(16.dp))
            AuthField(label = "Телефон", value = user.phone.orEmpty(), onValueChange = { if (editMode) viewModel.user.value = user.copy(phone = it) })
            Spacer(modifier = Modifier.height(22.dp))
            if (!errorMessage.isNullOrBlank()) {
                Text(errorMessage, color = AppDanger, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (editMode) {
                PrimaryButton(text = "Сохранить", onClick = { viewModel.save { editMode = false } })
                Spacer(modifier = Modifier.height(12.dp))
            }
            PrimaryButton(
                text = "Выйти из аккаунта",
                onClick = {
                    viewModel.signOut {
                        navController.navigate(navRoutes.signIn) {
                            popUpTo(navController.graph.id) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(120.dp))
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) { BottomNavBar(navRoutes.profile, navController) }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = { showDialog = false; cameraLauncher.launch(null) }) { Text("Камера", color = AppBlue) }
                    TextButton(onClick = { showDialog = false; galleryLauncher.launch("image/*") }) { Text("Галерея", color = AppBlue) }
                }
            },
            title = { Text("Выбор фото") },
            text = { Text("Откуда взять фото?") }
        )
    }
}
