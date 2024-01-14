package vic.dom.agendaapp.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import vic.dom.agendaapp.R
import vic.dom.agendaapp.database.AgendaApplication.Companion.database

class EventoDialog {

    interface EventoDialogListener {
        fun onPositiveButtonClick(text1: String, text2: String, text3: String, fecha: String, color: String)
        fun onNegativeButtonClick()
    }

    companion object {

        fun showAlertDialog(context: Context, listener: EventoDialogListener, contacto: String, titulo: String, descripcion: String, fecha: String, color: String) {

            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.evento_dialog, null)

            val title: TextView = dialogView.findViewById(R.id.titulo_dialogo_evento)
            val spinnerContacto: Spinner = dialogView.findViewById(R.id.spinner_contactos)
            val editTextTitulo: EditText = dialogView.findViewById(R.id.titulo_evento_dialogo)
            val editTextDescripcion: EditText = dialogView.findViewById(R.id.descripcion_dialogo)
            val datePicker: DatePicker = dialogView.findViewById(R.id.fecha_dialogo)
            val spinnerColor: Spinner = dialogView.findViewById(R.id.spinner_colores)
            val buttonAceptar: Button = dialogView.findViewById(R.id.botonConfirmar_dialogo_evento)
            val buttonCancelar: Button = dialogView.findViewById(R.id.botonCancelar_dialogo_evento)


            if (contacto != "") {
                title.text = "Editar evento"

                val posContacto = database.agendaDao().getAllContactos().indexOfFirst { it.nombre == contacto }
                spinnerContacto.setSelection(posContacto)

                editTextTitulo.setText(titulo)
                editTextDescripcion.setText(descripcion)

                val dia = fecha.subSequence(0, 1).toString().toInt()
                val mes = fecha.subSequence(3, 4).toString().toInt()
                val anyo = fecha.subSequence(6, 9).toString().toInt()

                datePicker.init(anyo, mes, dia, null)

                val posColor = when(color) {
                    "Azul" -> 0
                    "Rojo" -> 1
                    "Amarillo" -> 2
                    "Verde" -> 3
                    "Morado" -> 4
                    "Naranja" -> 5
                    else -> 6
                }
                spinnerColor.setSelection(posColor)
            }

            builder.setView(dialogView)
            val dialog = builder.create()


            buttonCancelar.setOnClickListener {
                listener.onNegativeButtonClick()
                dialog.cancel()
            }

            buttonAceptar.setOnClickListener {

                val colorElegido = when (spinnerColor.selectedItem) {
                    0 -> "#BEE3DF"
                    1 -> "#FFA6A6"
                    2 -> "#FAFFA6"
                    3 -> "#A6FFAB"
                    4 -> "#C8A6FF"
                    5 -> "#FFD1A6"
                    else -> "#FFA6FA"
                }

                listener.onPositiveButtonClick(spinnerContacto.selectedItem.toString(), editTextTitulo.text.toString(), editTextDescripcion.text.toString(), "${datePicker.dayOfMonth}/${datePicker.month}/${datePicker.year}", colorElegido)
                dialog.cancel()
            }

            dialog.show()
        }
    }

}