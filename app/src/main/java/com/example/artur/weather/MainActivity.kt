package com.example.artur.weather

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.view.Gravity

class MainActivity : AppCompatActivity() {

    lateinit var temperature: String
    lateinit var description: String
    lateinit var pressure: String
    lateinit var windSpeed: String
    var exc: Boolean = false
    var city: String = "Gliwice"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()

        searchCityBt.setOnClickListener()
        {
            city = cityEditText.text.toString()
            loadData()
        }
    }

    fun loadData()
    {
        exc = false
        val openWeatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=$city,pl&APPID=a0d4e2155cda382ce68a0b1433ab18b8"
        val client = OkHttpClient()
        val request = Request.Builder().url(openWeatherUrl).build()

        client.newCall(request).enqueue(object: Callback {

            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {

                val body: String? = response.body()?.string()
                val jsonBody: JSONObject = JSONObject(body)

                try {
                    val weatherArray: JSONArray = jsonBody.getJSONArray("weather")
                } catch(e: Exception) {
                    runOnUiThread() {
                        toast("Incorrect city name!")
                    }
                    exc = true
                }

                if(exc == false) {
                    val weatherArray: JSONArray = jsonBody.getJSONArray("weather")
                    val weatherObject: JSONObject = weatherArray.getJSONObject(0)
                    description = weatherObject.getString("description")

                    val mainObject: JSONObject = jsonBody.getJSONObject("main")
                    val temperatureString = mainObject.getString("temp")
                    temperature = (temperatureString.toDouble() - 273.15).toInt().toString()
                    pressure = mainObject.getString("pressure")

                    val windObject: JSONObject = jsonBody.getJSONObject("wind")
                    windSpeed = windObject.getString("speed")

                    city = jsonBody.getString("name")

                    runOnUiThread {
                        degreesValue.text = temperature
                        descriptionTxt.text = description
                        pressureValue.text = pressure
                        windSpeedTxt.text = windSpeed
                        cityName.text = city
                        timeTxt.text = getCurrentTime()
                        dateTxt.text = getCurrentDate()
                    }
                }
            }
        })
    }

    fun Context.toast(message: CharSequence) {
        val toast: Toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
        toast.show()
    }

    fun getCurrentTime(): String
    {
        val time = SimpleDateFormat("hh:mm")
        val currentTime = time.format(Date())

        return currentTime
    }

    fun getCurrentDate(): String
    {
        val dayOfWeek = SimpleDateFormat("EEEE")
        val currentDayOfWeek = dayOfWeek.format(Date())

        val dayOfMonth = SimpleDateFormat("dd")
        val currentDayOfMonth = dayOfMonth.format(Date())

        val month = SimpleDateFormat("MMMM")
        val currentMonth = month.format(Date())

        val currentDate = "$currentDayOfWeek $currentDayOfMonth $currentMonth"

        return currentDate
    }
}
