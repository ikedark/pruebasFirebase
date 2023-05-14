package mx.edu.potros.crudmascotas

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.gms.common.internal.ServiceSpecificExtraArgs.CastExtraArgs
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File



class reportePerdido : AppCompatActivity() {
    private val mascotaRef = FirebaseDatabase.getInstance().getReference("mascotaPerdida")
    val storage = Firebase.database
    private var imagen = null
    private val File = 1
    lateinit var img_btn_upload: ImageView
    lateinit var et_Nombre : EditText
    lateinit var et_Desc : EditText
    lateinit var et_ubicacion : EditText
    lateinit var et_cel : EditText
    lateinit var fechaR : EditText
    lateinit var s_sexoM: Spinner
    lateinit var btnReportar: Button
    var sexoPet : String = ""

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte_perdido)

        et_Nombre  = findViewById(R.id.nombreP)
        et_Desc  = findViewById(R.id.descP)
        et_ubicacion = findViewById(R.id.ubicacionP)
        et_cel  = findViewById(R.id.celP)
        s_sexoM  = findViewById(R.id.spinnerSexo)
        btnReportar = findViewById(R.id.btnReportar)
        fechaR  = findViewById(R.id.fechaP)
        fechaR.setOnClickListener { showDatePickerDialog() }


        img_btn_upload = findViewById(R.id.btn_subirFoto)
        img_btn_upload.setOnClickListener {
            fileUpload()
        }

        val lista = resources.getStringArray(R.array.sexoOpciones)
        val adaptador = ArrayAdapter(this, R.layout.spinner_sexo, lista)
        s_sexoM.adapter = adaptador

        s_sexoM.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sexoPet = lista[position].toString()
                //Toast.makeText(this@reportePerdido, lista[position], Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        btnReportar.setOnClickListener {
            savePetData()
            val intent: Intent = Intent(this, Mascotas_EP::class.java)
        }
    }

    private fun savePetData(){
        val nombreMascota = et_Nombre.text.toString()
        val descripcionMascota = et_Desc.text.toString()
        val lugarExtravio = et_ubicacion.text.toString()
        val fechaExtravio = fechaR.text.toString()
        val telefonoDuenio = et_cel.text.toString()

        if (nombreMascota.isNullOrEmpty() || descripcionMascota.isNullOrEmpty() || lugarExtravio.isNullOrEmpty() || telefonoDuenio.length > 10
            || telefonoDuenio.isNullOrEmpty() || fechaExtravio.isNullOrEmpty()){
            Toast.makeText(this, "Por favor llene todos los campos", Toast.LENGTH_LONG).show()
        }

        if (sexoPet != "Macho" || sexoPet != "Hembra"){
            Toast.makeText(this, "Por favor seleccione una opcion valida", Toast.LENGTH_LONG).show()
        }

        val mascotaId = mascotaRef.push().key!!

        val mascota : Mascota = Mascota(mascotaId, nombreMascota, descripcionMascota, fechaExtravio, telefonoDuenio, lugarExtravio, null, sexoPet)

        mascotaRef.child(mascotaId).setValue(mascota)
            .addOnCompleteListener {
                Toast.makeText(this, "El reporte de su mascota perdida se genero correctamente", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { err ->
                Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_SHORT).show()
            }
        //generarReporteMascota(mascota)
    }

    fun fileUpload(){
        val intent= Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, File)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == File) {
            if (resultCode == RESULT_OK) {
                val FileUri = data!!.data
                val Folder: StorageReference =
                    FirebaseStorage.getInstance().getReference().child("mascotaPerdida")
                val file_name: StorageReference = Folder.child("file" + FileUri!!.lastPathSegment)
                file_name.putFile(FileUri).addOnSuccessListener { taskSnapshot ->
                    file_name.getDownloadUrl().addOnSuccessListener { uri ->
                        val hashMap =
                            HashMap<String, String>()
                        hashMap["link"] = java.lang.String.valueOf(uri)
                        mascotaRef.setValue(hashMap)
                        Log.d("Mensaje", "Se subió correctamente")
                    }
                }
            }
        }
    }

    private fun generarReporteMascota(mascota: Mascota){
        val mascotaId = mascotaRef.push().key!!
        mascotaRef.child(mascotaId).setValue(mascota)
        Toast.makeText(this, "El reporte de su mascota perdida se genero correctamente", Toast.LENGTH_SHORT).show()
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    fun onDateSelected(day:Int, month:Int, year:Int){
        fechaR.setText("$day/$month/$year")
    }
}