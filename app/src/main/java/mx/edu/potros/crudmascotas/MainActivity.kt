package mx.edu.potros.crudmascotas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnIrReporte : Button = findViewById(R.id.button)

        btnIrReporte.setOnClickListener {
            val intent: Intent = Intent(this, reportePerdido::class.java)
            startActivity(intent)
        }
    }
}