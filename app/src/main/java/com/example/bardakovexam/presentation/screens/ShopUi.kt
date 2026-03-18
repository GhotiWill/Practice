package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bardakovexam.presentation.navigation.navRoutes

val AppBlue = Color(0xFF4CB1E8)
val AppBlueDark = Color(0xFF2E7DA5)
val AppBackground = Color(0xFFF7F8FC)
val AppCard = Color.White
val AppMuted = Color(0xFF9CA3AF)
val AppText = Color(0xFF2F2F37)
val AppField = Color(0xFFF4F5F8)
val AppDanger = Color(0xFFFF6D63)

@Composable
fun ScreenContainer(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .background(AppBackground)
            .padding(horizontal = 28.dp, vertical = 22.dp),
        content = content
    )
}

@Composable
fun BackCircleButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .shadow(2.dp, CircleShape)
            .background(Color.White, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("‹", color = AppText, fontSize = 28.sp)
    }
}

@Composable
fun HeaderTitle(title: String, subtitle: String? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(text = title, color = AppText, fontSize = 32.sp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Center)
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = AppMuted,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = AppBlue,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null
) {
    if (label.isNotBlank()) {
        Text(text = label, color = AppText, fontSize = 18.sp)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(10.dp))
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = {
            val placeholder = when {
                label.contains("Email") -> "xyz@gmail.com"
                label.contains("Телефон") -> "+7 811-732-5298"
                label.contains("Адрес") -> "Nigeria"
                else -> "xxxxxxxx"
            }
            Text(placeholder, color = AppMuted)
        },
        visualTransformation = visualTransformation,
        trailingIcon = trailing,
        shape = RoundedCornerShape(18.dp),
        textStyle = TextStyle(color = AppText, fontSize = 16.sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AppField,
            unfocusedContainerColor = AppField,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = AppBlue,
            focusedTextColor = AppText,
            unfocusedTextColor = AppText
        )
    )
}

@Composable
fun BottomNavBar(currentRoute: String, navController: NavController) {
    val items = listOf(
        "⌂" to navRoutes.home,
        "♡" to navRoutes.favorite,
        "👜" to navRoutes.catalog,
        "🔔" to navRoutes.sideMenu,
        "◌" to navRoutes.profile,
    )
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, (icon, route) ->
                val selected = currentRoute == route
                if (index == 2) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(AppBlue, CircleShape)
                            .clickable { navController.navigate(route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(icon, color = Color.White, fontSize = 24.sp)
                    }
                } else {
                    Text(
                        text = icon,
                        color = if (selected) AppBlue else AppMuted,
                        fontSize = 24.sp,
                        modifier = Modifier.clickable { navController.navigate(route) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChips(selected: String, items: List<String>, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(items) { item ->
            val isSelected = item == selected
            Surface(
                color = if (isSelected) AppBlue else Color.White,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.clickable { onSelect(item) }
            ) {
                Text(
                    text = item,
                    color = if (isSelected) Color.White else AppText,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 14.dp)
                )
            }
        }
    }
}

@Composable
fun BarcodePanel(data: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppField, RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        BarcodeView(data)
        Text(
            text = "Открыть",
            color = AppText,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(horizontal = 6.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun OtpCell(value: String, isError: Boolean = false) {
    Box(
        modifier = Modifier
            .size(width = 58.dp, height = 120.dp)
            .background(AppField, RoundedCornerShape(18.dp))
            .border(1.dp, if (isError) AppDanger else Color.Transparent, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = value.ifBlank { "o" }, color = AppText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}
