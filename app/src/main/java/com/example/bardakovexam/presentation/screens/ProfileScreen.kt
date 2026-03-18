package com.example.bardakovexam.presentation.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
    val profile = viewModel.profile.value
    val fullName = listOf(profile.firstname, profile.lastname).filter { !it.isNullOrBlank() }.joinToString(" ").ifBlank { "Emmanuel Oyiboke" }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.profile.value = profile.copy(photo = uri.toString())
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp ->
        bitmap = bmp
    }

    Box(modifier = Modifier.fillMaxSize().background(AppBackground)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 18.dp).verticalScroll(rememberScrollState())) {
            androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("≡", color = AppText, fontSize = 28.sp, modifier = Modifier.clickable { navController.navigate(navRoutes.sideMenu) })
                Text("Профиль", color = AppText, fontSize = 22.sp, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Box(modifier = Modifier.size(36.dp).background(AppBlue, CircleShape).clickable { editMode = !editMode }, contentAlignment = Alignment.Center) {
                    Text("✎", color = Color.White, fontSize = 16.sp, modifier = Modifier.align(Alignment.Center))
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                when {
                    bitmap != null -> Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = null, modifier = Modifier.size(120.dp).clip(CircleShape))
                    !profile.photo.isNullOrBlank() -> AsyncImage(model = profile.photo, contentDescription = null, modifier = Modifier.size(120.dp).clip(CircleShape))
                    else -> Box(modifier = Modifier.size(120.dp).background(AppBlue, CircleShape), contentAlignment = Alignment.Center) { Text(fullName.take(1), color = Color.White, fontSize = 36.sp) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(fullName, color = AppText, fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            TextButton(onClick = { showDialog = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text(if (editMode) "Изменить фото профиля" else "Изменить фото профиля", color = AppBlue, fontSize = 16.sp) }
            Spacer(modifier = Modifier.height(16.dp))
            BarcodePanel(data = profile.user_id.ifBlank { "1234567890" }, onClick = { navController.navigate(navRoutes.loyalty) })
            Spacer(modifier = Modifier.height(24.dp))
            AuthField(label = "Имя", value = profile.firstname.orEmpty(), onValueChange = { if (editMode) viewModel.profile.value = profile.copy(firstname = it) })
            Spacer(modifier = Modifier.height(16.dp))
            AuthField(label = "Фамилия", value = profile.lastname.orEmpty(), onValueChange = { if (editMode) viewModel.profile.value = profile.copy(lastname = it) })
            Spacer(modifier = Modifier.height(16.dp))
            AuthField(label = "Адрес", value = profile.address.orEmpty(), onValueChange = { if (editMode) viewModel.profile.value = profile.copy(address = it) })
            Spacer(modifier = Modifier.height(16.dp))
            AuthField(label = "Телефон", value = profile.phone.orEmpty(), onValueChange = { if (editMode) viewModel.profile.value = profile.copy(phone = it) })
            Spacer(modifier = Modifier.height(22.dp))
            if (editMode) PrimaryButton(text = "Сохранить", onClick = { viewModel.save(); editMode = false })
            Spacer(modifier = Modifier.height(120.dp))
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) { BottomNavBar(navRoutes.profile, navController) }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Column {
                    TextButton(onClick = { showDialog = false; cameraLauncher.launch(null) }) { Text("Камера", color = AppBlue) }
                    TextButton(onClick = { showDialog = false; galleryLauncher.launch("image/*") }) { Text("Галерея", color = AppBlue) }
                }
            },
            title = { Text("Выбор фото") },
            text = { Text("Откуда взять фото?") }
        )
    }
}
