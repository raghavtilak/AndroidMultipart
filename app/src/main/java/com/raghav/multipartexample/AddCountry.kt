package com.raghav.multipartexample

import android.app.Activity
import android.app.Instrumentation
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSerializer
import com.raghav.multipartexample.databinding.ActivityAddCountryBinding
import com.raghav.multipartexample.network.APIClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.StringBuilder
import kotlinx.coroutines.coroutineScope as coroutineScope1

class AddCountry : AppCompatActivity() {

    lateinit var binding: ActivityAddCountryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val images : MutableList<File> = mutableListOf()

        val getContent= registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode== Activity.RESULT_OK){
                val data:Intent?=it.data

                if(data?.clipData!=null){
                    val count=data.clipData?.itemCount ?: 0
                    for(i in 0 until count){
                        val imageUri : Uri?=data.clipData?.getItemAt(i)?.uri
                        val file=getImageFromUri(imageUri)
                        file?.let {
                            images.add(file)
                        }
                    }
                }else if(data?.data!=null){
                    val uri: Uri?=data.data
                    val file=getImageFromUri(uri)

                    file?.let {
                        images.add(file)
                    }
                }
            }

        }

        var json=""
        binding.submitBtn.setOnClickListener{
            val name=binding.name.text.toString()
            val capital=binding.capital.text.toString()
            val country = Country(0,name,capital,"")
            json=Util.getJson(country)
            println(json)
//            json=Gson().toJson(country)
            //getting wrong output in json, the order is changed
            //it is not according to the order of the ctor or the vals declared
            //i assume it is in alphabetical order
            //So we have to implement custom serializer for GSON
            //https://futurestud.io/tutorials/gson-advanced-custom-serialization-part-1
//            println(json)

            val image= mutableListOf<MultipartBody.Part>()
            images.forEach {
                image.add(MultipartBody.Part.
                            createFormData(
                                "images",
                                it.name,
                                RequestBody.create(MediaType.parse("image/*"),it)
                            )
                        )
            }
            val reqBody=RequestBody.create(MediaType.parse("text/plain"),json)
            val call = APIClient.CountryAPI.addCountry(image,reqBody)
            call.enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) {
                    response.body()?.let { it1 -> Log.d("TAG", it1) }
                    Log.d("TAG", "response is null"+response.errorBody())
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                    Log.d("TAG", "Error"+t.stackTraceToString())
                }
            })
        }

//        binding.submitBtn.setOnClickListener{
//
//    //sending single image
////            val obj=MultipartBody.Part.
////                            createFormData(
////                                "image",
////                                images[0].name,
////                                RequestBody.create(MediaType.parse("image/*"),images[0])
////                            )
////            val call=APIClient.CountryAPI.addImage(obj,"haha");
////
////            call.enqueue(object : Callback<String?> {
////                override fun onResponse(call: Call<String?>, response: Response<String?>) {
////                    response.body()?.let { it1 -> Log.d("TAG", it1) }
////                    Log.d("TAG", "response is null"+response.errorBody())
////                }
////
////                override fun onFailure(call: Call<String?>, t: Throwable) {
////                    Log.d("TAG", "Error"+t.stackTraceToString())
////                }
////            })
//
////sending multiple images
//            val response= mutableListOf<MultipartBody.Part>()
//            images.forEach {
//                val obj=MultipartBody.Part.
//                            createFormData(
//                                "images",
//                                it.name,
//                                RequestBody.create(MediaType.parse("image/*"),it)
//                            )
//                response.add(obj)
//            }
//
//            val call=APIClient.CountryAPI.addImages(response);
////
//            call.enqueue(object : Callback<String?> {
//                override fun onResponse(call: Call<String?>, response: Response<String?>) {
//                    response.body()?.let { it1 -> Log.d("TAG", it1) }
//                    Log.d("TAG", "response is null"+response.errorBody())
//                }
//
//                override fun onFailure(call: Call<String?>, t: Throwable) {
//                    Log.d("TAG", "Error"+t.stackTraceToString())
//                }
//            })
//
//        }
        binding.imagesBtn.setOnClickListener{
            val intent=Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.type="image/*"
            getContent.launch(intent)
        }

    }

    private fun getImageFromUri(imageUri: Uri?) : File? {
        imageUri?.let { uri ->
            val mimeType = getMimeType(this, uri)
            mimeType?.let {
                val file = createTmpFileFromUri(this, imageUri,"temp_image", ".$it")
                file?.let { Log.d("image Url = ", file.absolutePath) }
                return file
            }
        }
        return null
    }

    private fun getMimeType(context: Context, uri: Uri): String? {
        //Check uri format to avoid null
        val extension: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }

    private fun createTmpFileFromUri(context: Context, uri: Uri, fileName: String, mimeType: String): File? {
        return try {
            val istream = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile(fileName, mimeType,cacheDir)
            val ostream= file.outputStream()
            istream?.copyTo(ostream)

            istream?.close()
            ostream.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}