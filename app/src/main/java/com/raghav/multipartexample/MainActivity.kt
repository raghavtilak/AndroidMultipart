package com.raghav.multipartexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.raghav.multipartexample.databinding.ActivityMainBinding
import com.raghav.multipartexample.network.APIClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)


        binding.addCountryBtn.setOnClickListener{
            startActivity(Intent(this,AddCountry::class.java))
        }

        val call=APIClient.CountryAPI.getCountries();
        call.enqueue(object : Callback<List<Country>?> {
            override fun onResponse(
                call: Call<List<Country>?>,
                response: Response<List<Country>?>
            ) {
                if(response.isSuccessful){

                    val countries=response.body()
                    val adapter= countries?.let { CountryAdapter(this@MainActivity, it) }
                    binding.recyclerview.adapter=adapter
                    binding.recyclerview.layoutManager=LinearLayoutManager(this@MainActivity);

                    println(response.body())
                }else{
                    Log.println(Log.DEBUG,"TAG","ERROR");
                }
            }

            override fun onFailure(call: Call<List<Country>?>, t: Throwable) {
                println(t.printStackTrace())
                t.message?.let { Log.println(Log.DEBUG,"TAG-ERR", it) };
            }
        })
    }
}