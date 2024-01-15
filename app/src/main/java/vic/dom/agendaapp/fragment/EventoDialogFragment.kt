package vic.dom.agendaapp.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import vic.dom.agendaapp.R
import vic.dom.agendaapp.database.AgendaApplication.Companion.database
import vic.dom.agendaapp.databinding.FragmentContactoDialogBinding
import vic.dom.agendaapp.databinding.FragmentEventoDialogBinding
import vic.dom.agendaapp.models.Evento

class EventoDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentEventoDialogBinding

    interface DialogListener {
        fun onDialogOpened()
        fun onDialogDismissed()
    }

    private var dialogListener: DialogListener? = null

    fun setDialogListener(listener: DialogListener) {
        dialogListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventoDialogBinding.inflate(inflater, container, false)

        val opciones = database.agendaDao().getAllContactos().sortedBy { it.nombre.lowercase() }.map { it.nombre }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerContactos.adapter = adapter

        if (tag == "editarEventoDialogFragment") {

            binding.tituloDialogoEvento.text = "Editar evento"

            binding.tituloEventoDialogo.setText(arguments?.getString("tituloEvento"))
            binding.descripcionDialogo.setText(arguments?.getString("descripcionEvento"))
            setFecha()
            binding.spinnerContactos.setSelection(database.agendaDao().getAllContactos().sortedBy { it.nombre }.indexOfFirst { it.id == arguments?.getLong("contactoEvento") })
            binding.spinnerColores.setSelection(when (arguments?.getString("colorEvento")) {
                "Azul" -> 0
                "Rojo" -> 1
                "Amarillo" -> 2
                "Verde" -> 3
                "Morado" -> 4
                "Naranja" -> 5
                else -> 6
            })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.botonCancelarDialogoEvento.setOnClickListener {
            this.dismiss()
        }

        binding.botonConfirmarDialogoEvento.setOnClickListener {

            val colorElegido = when (binding.spinnerColores.selectedItemPosition) {
            0 -> "Azul"
            1 -> "Rojo"
            2 -> "Amarillo"
            3 -> "Verde"
            4 -> "Morado"
            5 -> "Naranja"
            else -> "Rosa"
            }

            if (!binding.tituloEventoDialogo.text.isNullOrEmpty()) {

                if (tag == "editarEventoDialogFragment") {

                    val evento = database.agendaDao().getAllEventos().find { it.contactoId == arguments?.getLong("contactoEvento") }?.copy(
                        titulo = binding.tituloEventoDialogo.text.toString(),
                        fecha = "${binding.fechaDialogo.dayOfMonth}/${binding.fechaDialogo.month + 1}/${binding.fechaDialogo.year}",
                        descripcion = binding.descripcionDialogo.text.toString(),
                        contactoId = database.agendaDao().getAllContactos().find { it.nombre == binding.spinnerContactos.selectedItem.toString() }?.id!!,
                        color = colorElegido
                    )!!

                    database.agendaDao().updateEvento(evento)
                    Toast.makeText(requireContext(), "Evento editado con éxito", Toast.LENGTH_SHORT).show()
                } else {

                    val evento = Evento(
                        titulo = binding.tituloEventoDialogo.text.toString(),
                        fecha = "${binding.fechaDialogo.dayOfMonth}/${binding.fechaDialogo.month + 1}/${binding.fechaDialogo.year}",
                        descripcion = binding.descripcionDialogo.text.toString(),
                        contactoId = database.agendaDao().getAllContactos().find { it.nombre == binding.spinnerContactos.selectedItem.toString() }?.id!!,
                        color = colorElegido
                    )

                    database.agendaDao().addEvento(evento)
                    Toast.makeText(requireContext(), "Evento añadido con éxito", Toast.LENGTH_SHORT).show()
                }

                this.dismiss()

            } else Toast.makeText(requireContext(), "Los campos con un * son obligatorios", Toast.LENGTH_SHORT).show()

        }
    }

    private fun setFecha() {

        val fecha = arguments?.getString("fechaEvento")?.split("/")
        val dia = fecha?.get(0)?.toInt()!!
        val mes = fecha[1].toInt() - 1
        val anyo = fecha[2].toInt()
        binding.fechaDialogo.init(anyo, mes, dia, null)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogListener?.onDialogDismissed()
    }

}