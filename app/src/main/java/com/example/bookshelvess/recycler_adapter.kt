package com.example.bookshelvess

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.carview.view.*
import java.util.*
import kotlin.collections.ArrayList


class recycler_adapter(val kitapisimlistesi:ArrayList<String>,
                       val kitapyazarlistesi:ArrayList<String>,
                       val kitapidListesi:ArrayList<Int>,
                       val imageList:ArrayList<ByteArray>,
                       val  rateList:ArrayList<Float>
)
    : RecyclerView.Adapter<recycler_adapter.KitaplarVH>() ,
    Filterable {
    var bookFilterList = ArrayList<String>()
    var authorFilterList = ArrayList<String>()
    init {
        bookFilterList = kitapisimlistesi
        authorFilterList=kitapyazarlistesi
    }
    class KitaplarVH(itemview: View) : RecyclerView.ViewHolder(itemview) {

    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    bookFilterList = kitapisimlistesi
                } else {
                    val resultList = ArrayList<String>()
                    val resultauthorList = ArrayList<String>()
                    var i=0
                    for (row in bookFilterList) {
                        if (row.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT)) ) {
                            resultList.add(row)
                            resultauthorList.add(authorFilterList[i])
                        }
                        i++
                    }
                    bookFilterList = resultList
                    authorFilterList=resultauthorList
                }
                val filterResults = FilterResults()
                filterResults.values = bookFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                bookFilterList = results?.values as ArrayList<String>
                notifyDataSetChanged()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitaplarVH {
        val inf = LayoutInflater.from(parent.context)
        val mview = inf.inflate(R.layout.carview, parent, false)
        return KitaplarVH(mview)
        //inflater
    }

    override fun onBindViewHolder(holder: KitaplarVH, position: Int) {

       holder.itemView.yazarcardview.text = authorFilterList.get(position)
        holder.itemView.textcardview.text = bookFilterList.get(position)
        //Resimleri anasayfaya çekiyoruz
        val imagebtmp= BitmapFactory.decodeByteArray(imageList.get(position),0,imageList[position].size)
        holder.itemView.img.setImageBitmap(imagebtmp)
        //anasayfadaki paylas butonu
        holder.itemView.paylascard.setOnClickListener {
            val paylasIntent=Intent(Intent.ACTION_SEND)
            paylasIntent.putExtra(Intent.EXTRA_TEXT,kitapisimlistesi.get(position))
            paylasIntent.type="text/plain"
            startActivity(it.context,paylasIntent,null)
        }
        //anasayfada kayıtlı kitaba basıldığında ne yapmasını istiyoruz?
        holder.itemView.setOnClickListener {
            val action = LibraryDirections.actionLibraryToİnsertBook(kitapidListesi[position])
            Navigation.findNavController(it).navigate(action)
        }
       // println(rateList.get(position))
        holder.itemView.ratingbarcard.rating=rateList.get(position)

    }
    override fun getItemCount(): Int {
        return bookFilterList.size
    }
}
