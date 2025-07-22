package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.data.WeatherModel
import com.example.weatherapp.screens.DialogSearch
import com.example.weatherapp.screens.MainCard
import com.example.weatherapp.screens.TabLayout
import org.json.JSONObject

const val API_KEY = "3a06af123cac4dd09a275317252706"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            WeatherAppTheme {
                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }
                val dialogState = remember{
                    mutableStateOf(false)
                }
                val currentDay = remember {
                    mutableStateOf(
                        WeatherModel(
                            "",
                            "",
                            "0",
                            "",
                            "",
                            "0",
                            "0",
                            ""
                        )
                    )
                }
                getData("Seoul", this, daysList, currentDay)
                if (dialogState.value){
                    DialogSearch(dialogState, onSubmit = {
                        getData(it, this, daysList, currentDay)
                        Log.d("MyLog", "Response: $it")
                    })
                }
                Image(
                    painter = painterResource(id = R.drawable.main1),
                    contentDescription = "image",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.8f),
                    contentScale = ContentScale.Crop
                )
                Column {
                    MainCard(currentDay, onClickSync = {
                        getData("Seoul", this@MainActivity, daysList, currentDay)
                    }, onClickSearch = {
                        dialogState.value = true
                    })
                    TabLayout(daysList, currentDay)
                }
            }
        }
    }
}

private fun getData(city: String, context: Context, daysList: MutableState<List<WeatherModel>>, currentDay:MutableState<WeatherModel>) {
    val url = "https://api.weatherapi.com/v1/forecast.json" +
            "?key=$API_KEY" +
            "&q=$city" +
            "&days=" +
            "14" +
            "&aqi=no&alerts=no"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.GET,
        url,
        { response ->
            val list = getWeatherByDays(response)
            daysList.value = list
            currentDay.value = list[0]
            Log.d("MyLog", "Response: $response")
        },
        { error ->
            Log.d("MyLog", "VolleyError: $error")

        }
    )
    queue.add(stringRequest)
}

private fun getWeatherByDays(response: String): List<WeatherModel> {
    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                item.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                item.getJSONArray("hour").toString()
            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}