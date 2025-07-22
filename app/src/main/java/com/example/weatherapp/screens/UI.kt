package com.example.weatherapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import coil.compose.AsyncImage
import com.example.weatherapp.data.WeatherModel
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.alpha

@Composable
fun ListItem(item: WeatherModel, currentDay: MutableState<WeatherModel>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
            .alpha(0.8f)
            .clickable {
                if(item.hours.isEmpty()) return@clickable
                currentDay.value = item
            },
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, top = 5.dp, bottom = 5.dp)
            ) {
                Text(item.time)
                Text(item.condition)
            }
            Text(text = item.currentTemp.ifEmpty { "${item.maxTemp}°C/${item.minTemp}°C" }, style = TextStyle(fontSize = 25.sp))
            AsyncImage(
                model = "https:${item.icon}",
                contentDescription = "img2",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .alpha(1f)
                    .size(40.dp)
            )
        }
    }
}

@Composable
fun MainList(list: List<WeatherModel>, currentDay: MutableState<WeatherModel>){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        itemsIndexed(
            list
        ){ _ , item ->
            ListItem(item, currentDay)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSearch(
    dialogState: MutableState<Boolean>,
    onSubmit: (String) -> Unit
) {
    val lightLavender = Color(0xFFF5F0FF)
    val softLavender = Color(0xFFE6D7FF)
    val mediumLavender = Color(0xFFD0B8FF)
    val textColor = Color.Black

    val dialogText = remember { mutableStateOf("") }
    val alpha = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(dialogState.value) {
        if (dialogState.value) {
            alpha.animateTo(1f, animationSpec = tween(300))
        } else {
            alpha.animateTo(0f, animationSpec = tween(200))
        }
    }

    if (dialogState.value) {
        Dialog(
            onDismissRequest = { dialogState.value = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .alpha(alpha.value),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                color = lightLavender
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Поиск города",
                        style = MaterialTheme.typography.headlineSmall,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = dialogText.value,
                        onValueChange = { dialogText.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Название города", color = textColor.copy(alpha = 0.7f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = mediumLavender,
                            unfocusedBorderColor = softLavender,
                            cursorColor = mediumLavender,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedLabelColor = mediumLavender,
                            unfocusedLabelColor = softLavender
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                onSubmit(dialogText.value)
                                dialogState.value = false
                            }
                        ),
                        singleLine = true,
                        trailingIcon = {
                            if (dialogText.value.isNotEmpty()) {
                                IconButton(
                                    onClick = { dialogText.value = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Очистить",
                                        tint = textColor.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { dialogState.value = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = textColor
                            )
                        ) {
                            Text("ОТМЕНА")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                onSubmit(dialogText.value)
                                dialogState.value = false
                            },
                            enabled = dialogText.value.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = mediumLavender,
                                contentColor = textColor
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("ПОИСК")
                        }
                    }
                }
            }
        }
    }
}