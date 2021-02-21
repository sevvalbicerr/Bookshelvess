package com.example.bookshelvess

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_insert_book.*
import kotlinx.android.synthetic.main.fragment_insert_book.imageView
import java.io.ByteArrayOutputStream
class Insert_book : Fragment() {
    var secilengrsel : Uri?=null
    var secilenbitmap : Bitmap?=null
    var kitapisim = ArrayList<String>()
    var kitapyazar = ArrayList<String>()
    lateinit var  bytedizi : ByteArray
    var secilenId :Int?=null
    lateinit var okumadurumu :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insert_book, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Radiobuttonla secilen seceneğin kaydı
        arguments?.let {
            var id=Insert_bookArgs.fromBundle(it).id
            val prefences = activity!!.getSharedPreferences("check", Context.MODE_PRIVATE)
            val name = prefences.getString("${id}","DEFAULT_VALUE")
            if(name=="okudum"){
                okudum.isChecked=true
            }
            else if(name=="okuyorum"){
                okuyorum.isChecked=true
            }
        }

       kaydet.setOnClickListener {
            kaydet(it)
           Radiobuttoncontrol()
        }
        imageView.setOnClickListener {
            gorselSec()
        }
        camera.setOnClickListener {
            cameraOpen()
        }
        paylas.setOnClickListener {
            kitabiPaylas()
        }
        updatebook.setOnClickListener {
            updateit(it)
           Radiobuttoncontrol()
        }
        sil.setOnClickListener {
            kaydisil()
        }

        //bundle kullanmak için intent yapısını kullanmam gerekiyor ve fragmentte intent yapısını kullanamadım
        //yani buradaki alina1 değişkeni her zaman null geliyor.
        //alinan1=null olduğu zamanda else yani kayıtlı bilgileri getiren blok çalışıyor.
        //zaten kitap eklemek iççin item kullandığım için uygulamanın çalışmasında bir problem olmuyor
        //ama istediğim olayı da doğru gerçekleştirmiş olmuyorum
        val bundle2=Bundle(arguments)
        //val alinan1=bundle2.getString("kitapekle")
        var alinan=arguments!!.getString("key")
        /*aynı şekilde kaydet butonunu da kayıtlı kitap verisini aldığım zaman visibility kullanarak gizlemek istiyorum
        ama aşağıdaki if else blokları doğru çalışmadığı için bunu gerçekleyemiyorum

        --navigation argümanlarıyla bunu yapabiliyoruz , bundle i intent gibi çeşitli yöntemler gösteriliyor ama fragment yapısında intent
        kullanamadığım için ikisini de gerçekleyemiyorum. */
        arguments?.let {
            if (alinan=="kitapekle"){
                kitapadi.setText("")
                yazar.setText("")
                tur.setText("")
                inceleme.setText("")
                kitapbasim.setText("")
               //kaydet.visibility=View.VISIBLE
                val gorselsetle= BitmapFactory.decodeResource(context?.resources,R.drawable.gorselsec)
                imageView.setImageBitmap(gorselsetle)
            }else {
                secilenId=Insert_bookArgs.fromBundle(it).id
                context?.let {
                   // kaydet.visibility= View.GONE
                    try {
                        val dTBase = it.openOrCreateDatabase("Kutuphane", Context.MODE_PRIVATE, null)
                        val cursor=dTBase.rawQuery("SELECT * FROM kitap WHERE id= ?", arrayOf(secilenId.toString()))
                        val kitapismiIndex=cursor.getColumnIndex("kitapadi")
                        val yazaradiIndex=cursor.getColumnIndex("yazaradi")
                        val basimIndex=cursor.getColumnIndex("basim")
                        val turuIndex=cursor.getColumnIndex("turu")
                        val incelemeIndex=cursor.getColumnIndex("inceleme")
                        val gorsel=cursor.getColumnIndex("gorsel")
                        val puan=cursor.getColumnIndex("puan")
                        while (cursor.moveToNext()){
                            kitapadi.setText(cursor.getString(kitapismiIndex))
                            yazar.setText(cursor.getString(yazaradiIndex))
                            tur.setText(cursor.getString(turuIndex))
                            kitapbasim.setText(cursor.getString(basimIndex))
                            inceleme.setText(cursor.getString(incelemeIndex))
                            val bytedizi=cursor.getBlob(gorsel)
                            val imagebtmp= BitmapFactory.decodeByteArray(bytedizi,0,bytedizi.size)
                            imageView.setImageBitmap(imagebtmp)
                            ratinginsert.rating=cursor.getFloat(puan)
                        }
                        cursor.close()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                //kaydet.visibility=View.INVISIBLE
            }
        }
    }
    fun Radiobuttoncontrol(){
        val selectedId=radio_group.checkedRadioButtonId
        arguments?.let {
            val id=Insert_bookArgs.fromBundle(it).id
            if (selectedId==R.id.okudum){
                okumadurumu="okudum"

            }
            else if (selectedId==R.id.okuyorum){
                okumadurumu="okuyorum"
            }
            val sharedPreference = activity!!.getSharedPreferences("check", Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.putString("${id}", okumadurumu)
            editor.apply()
        }
    }
    fun kaydet(view: View){
        val kitapadial=kitapadi.text.toString()
        val yazaradial=yazar.text.toString()
        val tural=tur.text.toString()
        val incelemeal=inceleme.text.toString()
        val basimal=kitapbasim.text.toString()
        val puanal=ratinginsert.rating
       // checkbox()
       // radio()
       // ratingsave=ratinginsert.rating.toString()
     //   onRadioButtonClicked(view)
        if (secilengrsel!=null){
            val kucukbitmap=grselKucul(300,secilenbitmap!!)
            val byteoutput=ByteArrayOutputStream()
            kucukbitmap.compress(Bitmap.CompressFormat.PNG,50,byteoutput)
             bytedizi= byteoutput.toByteArray()
        try {
            context?.let {
                val dTBase = it.openOrCreateDatabase("Kutuphane", Context.MODE_PRIVATE, null)
                dTBase.execSQL("CREATE TABLE IF NOT EXISTS kitap(id INTEGER PRIMARY KEY,kitapadi VARCHAR,yazaradi VARCHAR,basim VARCHAR,turu VARCHAR,inceleme VARCHAR,gorsel BLOB,puan FLOAT)")
              /* sqLite table tasarımı
               val sqlString = "INSERT INTO kitap(kitapadi,yazaradi ,basim,turu,inceleme,gorsel,puan) VALUES (?,?,?,?,?,?,?)"
                val statement = dTBase.compileStatement(sqlString)
                statement.bindString(1, kitapadial)
                statement.bindString(2,yazaradial)
                statement.bindString(3, basimal)
                statement.bindString(4,tural)
                statement.bindString(5,incelemeal)
                statement.bindBlob(6, bytedizi)
                statement.bindString(7,puanal)*/
                // statement.execute()
                val values = ContentValues()
                values.put("kitapadi",kitapadial)
                values.put("yazaradi",yazaradial)
                values.put("basim", basimal)
                values.put("turu", tural)
                values.put("inceleme",incelemeal)
                values.put("puan",puanal)

                if (secilenbitmap!=null){

                    val kucukbitmap=grselKucul(300,secilenbitmap!!)
                    val byteoutput=ByteArrayOutputStream()
                    kucukbitmap.compress(Bitmap.CompressFormat.PNG,50,byteoutput)
                    bytedizi= byteoutput.toByteArray()
                    values.put("gorsel",bytedizi)
                }

                dTBase.insert("kitap",null, values)
                dTBase.close()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        val action = Insert_bookDirections.actionİnsertBookToLibrary()
        Navigation.findNavController(view!!).navigate(action)
    }}
    fun kitabiPaylas(){
        val paylasIntent=Intent(Intent.ACTION_SEND)
        paylasIntent.putExtra(Intent.EXTRA_TEXT,kitapisim)
        paylasIntent.type="text/plain"
        //  ContextCompat.startActivity(paylasIntent)
        startActivity(paylasIntent)
    }
    fun updateit(view: View){
        val kitapadial=kitapadi.text.toString()
        val yazaradial=yazar.text.toString()
        val tural=tur.text.toString()
        val incelemeal=inceleme.text.toString()
        val basimal=kitapbasim.text.toString()
        val puanal=ratinginsert.rating
        arguments?.let {
            val secilenId=Insert_bookArgs.fromBundle(it).id

            context?.let {
                try {
                    val dTBase = it.openOrCreateDatabase("Kutuphane", Context.MODE_PRIVATE, null)
                    val values = ContentValues()
                    values.put("kitapadi",kitapadial)
                    values.put("yazaradi",yazaradial)
                    values.put("basim", basimal)
                    values.put("turu", tural)
                    values.put("inceleme",incelemeal)
                    if (secilenbitmap!=null){

                        val kucukbitmap=grselKucul(300,secilenbitmap!!)
                        val byteoutput=ByteArrayOutputStream()
                        kucukbitmap.compress(Bitmap.CompressFormat.PNG,50,byteoutput)
                        bytedizi= byteoutput.toByteArray()
                        values.put("gorsel",bytedizi)
                    }
                    values.put("puan",puanal)
                    dTBase.update("kitap",values, "id = ?", arrayOf(secilenId.toString()))
                    dTBase.close()
                }catch (e:Exception){
                    e.printStackTrace()
                }
                Toast.makeText(context,"Kayıt güncellendi",Toast.LENGTH_LONG).show()
                val actionn=Insert_bookDirections.actionİnsertBookToLibrary()
                Navigation.findNavController(view).navigate(actionn)
            }
        }
    }
    fun checkbox(){
        //Eğer checkbox kullanılmak istenirse
       if (okuyorum.isChecked && !okudum.isChecked){
            okumadurumu="okuyorum"
        }
        else if (okudum.isChecked && !okuyorum.isChecked){
           okumadurumu="okudum"
        }
        else if(okudum.isChecked && okuyorum.isChecked){


       }


        println("son durum ${okumadurumu}")
        val sharedPreference = activity!!.getSharedPreferences("check", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("numStars", okumadurumu)
        editor.apply()


    }
    fun kaydisil(){
        val alert= AlertDialog.Builder(context)
        alert.setMessage("Kayıt silinsin mi ?")
        alert.setPositiveButton("Evet", DialogInterface.OnClickListener { dialog, which ->
            Toast.makeText(context,"Evet",Toast.LENGTH_LONG).show()
            arguments?.let {
                val secilenId=Insert_bookArgs.fromBundle(it).id
                context?.let {
                    try {
                        val dTBase = it.openOrCreateDatabase("Kutuphane", Context.MODE_PRIVATE, null)
                        dTBase.execSQL("DELETE FROM kitap WHERE id= ?", arrayOf(secilenId.toString()))
                        dTBase.close()

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
            val actionn=Insert_bookDirections.actionİnsertBookToLibrary()
            Navigation.findNavController(view!!).navigate(actionn)
        })
        alert.setNegativeButton("Hayır", DialogInterface.OnClickListener { dialog, which ->
            Toast.makeText(context,"Kaydı silmekten vazgeçiniz.",Toast.LENGTH_LONG).show()
        })
        alert.show()
    }
    fun cameraOpen(){
        context?.let {
            if (ContextCompat.checkSelfPermission(it,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                //Camera izni verilmediyse
                requestPermissions(arrayOf(Manifest.permission.CAMERA),3)
            }
            else{
                val cameraintent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val values=ContentValues()
                values.put(MediaStore.Video.Media.TITLE,"new")
                secilengrsel= it.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
                startActivityForResult(cameraintent,4)
            }

        }

    }
    fun gorselSec(){
        context?.let {
            if(ContextCompat.checkSelfPermission(it.applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                //izin verilmedi
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

            }
            else{ Toast.makeText(this.context,"Permission already granted",LENGTH_LONG).show()
                val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==1){
            if (grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //izin verildiyse görüntüyü al
                Toast.makeText(this.context,"Permission already granted",LENGTH_LONG).show()
                val intent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }
        }
        else if (requestCode==3){
            if (grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                val intent=Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,4)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            secilengrsel = data.data
            try {
                context?.let {
                    if (secilengrsel != null) {
                        if (Build.VERSION.SDK_INT > 28) {
                            val source = ImageDecoder.createSource(it.contentResolver, secilengrsel!!)
                            secilenbitmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenbitmap)
                        }
                        else {
                            secilenbitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, secilengrsel)
                            imageView.setImageBitmap(secilenbitmap)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }  else  if (requestCode == 4 && resultCode == Activity.RESULT_OK && data!=null) {
            secilenbitmap = data.extras!!.get("data") as Bitmap
           imageView.setImageBitmap(secilenbitmap)
        }
    }
    fun grselKucul(maximum:Int,islenecekBitmap:Bitmap): Bitmap{
        var width=islenecekBitmap.width
        var height=islenecekBitmap.height
        val bitmaporani=width.toDouble()/height.toDouble()
        if (bitmaporani>1){
            width=maximum
            val kisaltilmis=width/bitmaporani
            height=kisaltilmis.toInt()
        }
        else{
            height=maximum
            val kisaltilmis=height * bitmaporani
            width=kisaltilmis.toInt()
        }
        return Bitmap.createScaledBitmap(islenecekBitmap, width,height, false)
    }


}
