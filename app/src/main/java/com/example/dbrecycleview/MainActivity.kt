package com.example.dbrecycleview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.EditText

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlite.DBHelper
import com.example.sqlite.RecyclerAdapter


class MainActivity : AppCompatActivity() {
    private val list = mutableListOf<String>()
    private lateinit var adapter: RecyclerAdapter
    private val dbHelper = DBHelper(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = RecyclerAdapter(list) {
            if(it != -1) {
                val name = list[it]
                list.removeAt(it)
                adapter.notifyItemRemoved(it)
                val listTod = dbHelper.getAll()
                for (todo in listTod) {
                    if (todo.title == name) {
                        dbHelper.remove(todo.id)
                    }

                }
            }
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val listTod = dbHelper.getAll()
        for (todo in listTod) {
            list.add(todo.title)
            adapter.notifyItemInserted(list.lastIndex)
        }


        val number = findViewById<EditText>(R.id.editTextNumber)

        val buttonAdd = findViewById<Button>(R.id.button)

        buttonAdd.setOnClickListener {
            dbHelper.add(number.text.toString())
            val listTod = dbHelper.getAll()
            val s = StringBuilder()
            for (todo in listTod) {
                s.append("${todo.id} ${todo.title}\n")
            }
            list.add(number.text.toString())
            adapter.notifyItemInserted(list.lastIndex)
            number.setText("")
        }

    }
}




