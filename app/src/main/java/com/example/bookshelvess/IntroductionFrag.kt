package com.example.bookshelvess

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.Adapter
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class IntroductionFrag : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction_frag)
        val intent=intent

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflater
        val inflater=menuInflater
        inflater.inflate(R.menu.menu,menu)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.kitapEkle){
           val action=LibraryDirections.actionLibraryToİnsertBook(0)
            Navigation.findNavController(this,R.id.fragment).navigate(action)

        }
          return super.onOptionsItemSelected(item)
    }
}