package com.dplamongan.devkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.dplamongan.devkotlin.databinding.Item1Binding
import com.dplamongan.devkotlin.databinding.Item2Binding
import com.dplamongan.devkotlin.databinding.ItemBinding
import com.simpleadapter.SimpleAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item.view.*

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        httpreq()
    }

    private fun niceToast(message: String,tag: String = "Info",length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, "$tag : $message", length).show()
    }

    private fun httpreq(){
        AndroidNetworking.get("https://jsonplaceholder.typicode.com/posts")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    parse_data(response)
                }
                override fun onError(error: ANError) {
                    niceToast(error.errorDetail,"Error")
                }
            })
    }

    private fun parse_data(response: JSONArray){
        val list = ArrayList<User>()

        for (i in 0 until response.length()) {
            try {
                val obj = response.getJSONObject(i)
                list.add(User(obj.getString("id"),obj.getString("title")))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        //Create adapter
        val adapter2 = SimpleAdapter.with<User, ItemBinding>(R.layout.item) { adapterPosition, model, binding ->
            binding.textId.text = model.name+" "+model.last_name;
        }

        //clickable views
        adapter2.setClickableViews({ view, model, adapterPosition ->
            Toast.makeText(this@MainActivity, "${model.name} clicked", Toast.LENGTH_SHORT).show()
        }, R.id.text, R.id.text1, R.id.text2)

        //for filtering
        adapter2.performFilter(text = "", filterLogic = { text, model ->
            model.name.contains(text) or model.last_name.contains(text)
        })

        adapter2.addAll(list)
        adapter2.notifyDataSetChanged()
        recyclerView.adapter = adapter2

        findViewById<EditText>(R.id.etSearch).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) { }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter2.performFilter(p0.toString()) { text, model ->
                    model.name.startsWith(text) or model.last_name.startsWith(text)
                }
            }
        })
    }

}