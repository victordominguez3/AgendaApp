package vic.dom.agendaapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import vic.dom.agendaapp.R
import vic.dom.agendaapp.adapters.ItemEventoAdapter
import vic.dom.agendaapp.adapters.OnEventoClickListener
import vic.dom.agendaapp.database.AgendaApplication
import vic.dom.agendaapp.database.AgendaApplication.Companion.database
import vic.dom.agendaapp.databinding.FragmentEventosBinding
import vic.dom.agendaapp.dialogs.ContactoDialog
import vic.dom.agendaapp.dialogs.EventoDialog
import vic.dom.agendaapp.models.Contacto
import vic.dom.agendaapp.models.Evento
import java.text.SimpleDateFormat
import java.util.Locale

class EventosFragment : Fragment(), OnEventoClickListener {

    private lateinit var binding: FragmentEventosBinding

    private lateinit var mAdapter: ItemEventoAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()

        binding.opciones.setOnClickListener {
            mostrarMenu(it)
        }
    }

    private fun setRecyclerView() {

        val lista = AgendaApplication.database.agendaDao().getAllEventos()

        mAdapter = ItemEventoAdapter(lista, this)
        mLayoutManager = LinearLayoutManager(requireContext())

        binding.recycler.apply {
            layoutManager = mLayoutManager
            adapter = mAdapter
        }

    }

    private fun mostrarMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_eventos, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.agregar_evento -> {

                    EventoDialog.showAlertDialog(
                        requireContext(),
                        object : EventoDialog.EventoDialogListener {
                            override fun onPositiveButtonClick(
                                text1: String,
                                text2: String,
                                text3: String,
                                fecha: String,
                                color: String
                            ) {
                                val evento = Evento(contactoId = database.agendaDao().getAllContactos().find { it.nombre == text1 }?.id!!, titulo = text2, descripcion = text3, fecha = fecha, color = color)

                                database.agendaDao().addEvento(evento)
                                mAdapter.setEventos(database.agendaDao().getAllEventos())
                            }

                            override fun onNegativeButtonClick() {

                            }
                        },
                        "",
                        "",
                        "",
                        "",
                        ""
                    )

                    true
                }
                R.id.ordenar_fecha -> {

                    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    mAdapter.setEventos(database.agendaDao().getAllEventos().sortedBy { formatoFecha.parse(it.fecha) }.toMutableList())

                    true
                }
                R.id.ordenar_titulo -> {

                    mAdapter.setEventos(database.agendaDao().getAllEventos().sortedBy { it.titulo }.toMutableList())

                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onClick(id: Long) {}

    override fun onLongClick(evento: Evento) {

        val builder1 = AlertDialog.Builder(requireContext())
            .setItems(
                arrayOf("Editar", "Eliminar")
            ) { dialog, which ->
                if (which == 0) {

                    EventoDialog.showAlertDialog(
                        requireContext(),
                        object : EventoDialog.EventoDialogListener {
                            override fun onPositiveButtonClick(
                                text1: String,
                                text2: String,
                                text3: String,
                                fecha: String,
                                color: String
                            ) {
                                evento.contactoId = database.agendaDao().getAllContactos().find { it.nombre == text1 }?.id!!
                                evento.titulo = text2
                                evento.descripcion = text3
                                evento.fecha = fecha
                                evento.color = color

                                database.agendaDao().updateEvento(evento)
                                mAdapter.setEventos(database.agendaDao().getAllEventos())
                            }

                            override fun onNegativeButtonClick() {

                            }
                        },
                        database.agendaDao().getAllContactos().find { it.id == evento.contactoId }?.nombre!!,
                        evento.titulo,
                        evento.descripcion,
                        evento.fecha,
                        evento.color
                    )

                } else {
                    val builder2 = AlertDialog.Builder(requireContext())

                    builder2.setTitle("Alerta")
                        .setMessage("¿Estás seguro de que deseas borrar el evento ${evento.titulo}?")

                    builder2.setPositiveButton("Sí") { _, _ ->
                        database.agendaDao().deleteEvento(evento)
                        mAdapter.delete(evento)
                    }

                    builder2.setNegativeButton("No") { _, _ ->

                    }

                    builder2.create().show()
                }
            }
        builder1.create().show()

    }

}