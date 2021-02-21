package com.example.bookshelvess

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_library.*

class Library : Fragment() {
    lateinit var libraryadapter: recycler_adapter
    var kitapisim = ArrayList<String>()
    var kitapyazar = ArrayList<String>()
    var kitapid=ArrayList<Int>()
    var imageList=ArrayList<ByteArray>()
    var rate =ArrayList<Float>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // libraryadapter= recycler_adapter(kitapisim,kitapyazar,kitapid,imageList,rate)
        libraryadapter= recycler_adapter(kitapisim,kitapyazar,kitapid,imageList,rate)
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerView.adapter=libraryadapter
        sqlveri()
        searchViewid.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
               /*if (newText==null || newText.isEmpty()){
                   libraryadapter
                }
                else{
                   libraryadapter.filter.filter(newText)
                   libraryadapter.notifyDataSetChanged()
                }*/
                /*
                burada searchview'e bir text yazıp sildiğimizde uygulama patlıyor. araştırdığımda çözüm için
                onQueryTextChange fonksiyonu içine yazılacak bir kod parçasıyla çözülebileceği söyleniyor.
                 */
                libraryadapter.filter.filter(newText)
                libraryadapter.notifyDataSetChanged()

                return false
            }
        })
    }
    fun sqlveri() {
        try {
            context?.let {
                val dTBase = it.openOrCreateDatabase("Kutuphane", Context.MODE_PRIVATE, null)
                val cursor = dTBase.rawQuery("SELECT * FROM kitap", null)
                val kitapismiIndex = cursor.getColumnIndex("kitapadi")
                val kitapyazarIndex = cursor.getColumnIndex("yazaradi")
                val kiapidIndex=cursor.getColumnIndex("id")
                val grselcardviewindex=cursor.getColumnIndex("gorsel")
                val ratecardindex=cursor.getColumnIndex("puan")
                kitapisim.clear()
                kitapyazar.clear()
                kitapid.clear()
                imageList.clear()
               rate.clear()
                while (cursor.moveToNext()) {

                    kitapisim.add(cursor.getString(kitapismiIndex))
                    kitapyazar.add(cursor.getString(kitapyazarIndex))
                    kitapid.add(cursor.getInt(kiapidIndex))
                    imageList.add(cursor.getBlob(grselcardviewindex))
                    rate.add(cursor.getFloat(ratecardindex))
                }
                libraryadapter.notifyDataSetChanged()
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}