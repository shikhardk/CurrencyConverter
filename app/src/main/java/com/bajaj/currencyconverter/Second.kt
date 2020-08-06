package com.bajaj.currencyconverter

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.loader.content.AsyncTaskLoader
import kotlinx.android.synthetic.main.activity_second.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Second : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }

    fun finalConvert(view: View) {
        val amount = usdC.text.toString()

        if (amount.isNotEmpty()){
            //val amt = amount.toInt()
            if (isNetAvailable()){
                val test = Toast.makeText(this, "Getting conversion done...", Toast.LENGTH_LONG)
                test.show()
                //Toast test = Toa
                ConvertTask().execute()

                val test2 = Toast.makeText(this, "Conversion done!", Toast.LENGTH_LONG)
                test.cancel()
                test2.show()
                //Toast.makeText(this, "abc", 1500).show()
            }
            else{
                Toast.makeText(this, "Please connect to a network!", Toast.LENGTH_LONG).show()
            }
        }
        else{
            Toast.makeText(this, "Please enter valid amount!", Toast.LENGTH_LONG).show()
            usdC.setText("")
            resultR.setText("")
        }
    }

    fun isNetAvailable(): Boolean{
        val cManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cManager.activeNetwork
        if (network != null){
            return true
        }
        return false
    }

    inner class ConvertTask: AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg args: String?): String {
            var result = ""
            val urls = "https://api.exchangeratesapi.io/latest?base=USD&symbols=INR"
            val url = URL(urls)
            val connect = url.openConnection() as HttpURLConnection
            connect.connectTimeout = 15000
            connect.readTimeout = 15000
            //return result

            val reader = BufferedReader(InputStreamReader(connect.inputStream))
            var line = reader.readLine()
            while (line != null){
                result += line
                line = reader.readLine()
            }
            //Log.d("ConvertTag", "$result")
            return result
        }

        override fun onPostExecute(result: String?) {
            resultR.setText("")
            if (result!!.isNotEmpty()){
                val resultJson = JSONObject(result)
                val rateJson = resultJson.getJSONObject("rates")
                val inr = rateJson.getString("INR")
                val uamount = usdC.text.toString()
                val uamt = uamount.toDouble()
                val finalc = uamt * inr.toDouble()
                val finalc2:Double = String.format("%.2f", finalc).toDouble()
                resultR.setText("$$uamt = $finalc2 INR")
                usdC.setText("")
            }
            else{
                Toast.makeText(baseContext, "No converter found!", Toast.LENGTH_LONG).show()
            }
            super.onPostExecute(result)
        }
    }
}