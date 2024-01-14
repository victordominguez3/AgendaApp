package vic.dom.agendaapp.dialogs

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import vic.dom.agendaapp.R

class ContactoDialog: Fragment() {

    interface ContactoDialogListener {
        fun onPositiveButtonClick(text1: String, text2: String, text3: String, image: String, switch: Boolean)
        fun onNegativeButtonClick()
    }

        fun showAlertDialog(context: Context, listener: ContactoDialogListener, nombre: String, numero: String, correo: String, imagen: String, fav: Boolean) {

            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.contacto_dialog, null)

            val title: TextView = dialogView.findViewById(R.id.titulo_dialogo_contacto)
            val editTextNombre: EditText = dialogView.findViewById(R.id.nombre_dialogo)
            val editTextNumero: EditText = dialogView.findViewById(R.id.numero_dialogo)
            val editTextCorreo: EditText = dialogView.findViewById(R.id.correo_dialogo)
            val image: ImageView = dialogView.findViewById(R.id.imagen_dialogo)
            val switch: SwitchMaterial = dialogView.findViewById(R.id.switch_dialogo)
            val buttonAceptar: Button = dialogView.findViewById(R.id.botonConfirmar_dialogo_contacto)
            val buttonCancelar: Button = dialogView.findViewById(R.id.botonCancelar_dialogo_contacto)

            if (nombre != "") {
                title.text = "Editar contacto"
                editTextNombre.setText(nombre)
                editTextNumero.setText(numero)
                editTextCorreo.setText(correo)
                image.setImageURI(Uri.parse(imagen))
                switch.isActivated = fav
            }

            builder.setView(dialogView)
            val dialog = builder.create()

            buttonCancelar.setOnClickListener {
                listener.onNegativeButtonClick()
                dialog.cancel()
            }

            buttonAceptar.setOnClickListener {
                listener.onPositiveButtonClick(editTextNombre.text.toString(), editTextNumero.text.toString(), editTextCorreo.text.toString(), image.toString(), switch.isActivated)
                dialog.cancel()
            }

            dialog.show()

        }



}