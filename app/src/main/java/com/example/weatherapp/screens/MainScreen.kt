package com.example.weatherapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.R
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.ui.theme.Pink40
import com.example.weatherapp.ui.theme.Pink80
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun MainCard(currentDay: MutableState<WeatherModel>, onClickSync: () -> Unit, onClickSearch: () -> Unit ) {
    Column(
        modifier = Modifier
            .padding(top = 55.dp, start = 5.dp, end = 5.dp, bottom = 5.dp)
    ) {
        Card(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .fillMaxWidth()
                .alpha(0.8f),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentDay.value.time,
                        style = TextStyle(fontSize = 15.sp),
                        modifier = Modifier
                            .padding(8.dp)
                            .alpha(0.5f)
                    )
                    AsyncImage(
                        model = "https:${currentDay.value.icon}",
                        contentDescription = "img",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .alpha(1f)
                            .size(40.dp)
                    )
                }
                Text(
                    text = currentDay.value.city,
                    style = TextStyle(fontSize = 22.sp)
                )
                Text(
                    text = if(currentDay.value.currentTemp.isNotEmpty())
                        currentDay.value.currentTemp.toFloat().toInt().toString() +"°C"
                    else "${currentDay.value.maxTemp}°C/${currentDay.value.minTemp}°C",
                    style = TextStyle(fontSize = 60.sp)
                )
                Text(
                    text = currentDay.value.condition,
                    style = TextStyle(fontSize = 15.sp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        onClickSearch.invoke()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "icon1"
                        )
                    }
                    Text(
                        text = "${currentDay.value.maxTemp.toFloat().toInt().toString()}°C/${currentDay.value.minTemp.toFloat().toInt().toString()}°C",
                        style = TextStyle(fontSize = 14.sp)
                    )
                    IconButton(
                        onClick = {
                            onClickSync.invoke()
                    }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sync),
                            contentDescription = "icon2"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>, currentDay: MutableState<WeatherModel>) {
    val tabList = listOf("HOURS", "DAYS")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .alpha(0.8f)
    ) {

            Column(
            ) {
                TabRow(
                    selectedTabIndex = tabIndex,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                            color = Color.Black
                        )
                    },
                    backgroundColor = Color.White.copy(alpha = 0.65f),
                    contentColor = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    tabList.forEachIndexed { index, text ->
                        Tab(
                            selected = index == tabIndex,
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            text = {
                                Text(
                                    text = text,
                                    color = Color.Black,
                                    style = TextStyle(fontSize = 14.sp)
                                )
                            }
                        )
                    }
                }
                HorizontalPager(
                    count = tabList.size,
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)

                ) { index ->
                    val list = when(index){
                        0 -> getWeatherByHours(currentDay.value.hours)
                        1 -> daysList.value
                        else -> daysList.value
                    }
                    MainList(list, currentDay)
                }

        }
    }

}

private fun getWeatherByHours(hours: String): List<WeatherModel>{
    if (hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in 0 until hoursArray.length()){
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString() + "°C",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                ""
            )
        )
    }
    return list
}