package vic.dom.agendaapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import vic.dom.agendaapp.R
import vic.dom.agendaapp.adapters.ItemEventoAdapter
import vic.dom.agendaapp.adapters.OnEventoClickListener
import vic.dom.agendaapp.database.AgendaApplication.Companion.database
import vic.dom.agendaapp.databinding.FragmentEventosBinding
import vic.dom.agendaapp.models.Evento
import java.text.SimpleDateFormat
import java.util.Locale

class EventosFragment : Fragment(), OnEventoClickListener, EventoDialogFragment.DialogListener {

    private lateinit var binding: FragmentEventosBinding

    private lateinit var mAdapter: ItemEventoAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventosBinding.inflate(inflater, container, false)

        val opciones = listOf("Todos") + database.agendaDao().getAllContactos().sortedBy { it.nombre.lowercase() }.map { it.nombre }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEventos.adapter = adapter

        binding.spinnerEventos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 0) {
                    mAdapter.setEventos(database.agendaDao().getAllEventos())
                } else {
                    val contacto = database.agendaDao().getAllContactos().sortedBy { it.nombre.lowercase() }[p2 - 1]
                    mAdapter.setEventos(database.agendaDao().getAllEventos().filter { it.contactoId == contacto.id }.toMutableList())
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

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

        val lista = database.agendaDao().getAllEventos()

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

                    val dialogFragment = EventoDialogFragment()
                    dialogFragment.setDialogListener(this)
                    dialogFragment.show(parentFragmentManager, "nuevoEventoDialogFragment")

                    true
                }
                R.id.ordenar_fecha -> {

                    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    mAdapter.setEventos(database.agendaDao().getAllEventos().sortedBy { formatoFecha.parse(it.fecha) }.toMutableList())

                    true
                }
                R.id.ordenar_titulo -> {

                    mAdapter.setEventos(database.agendaDao().getAllEventos().sortedBy { it.titulo.lowercase() }.toMutableList())

                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onClick(id: Long) {}

    override fun onLongClick(evento: Evento) {

        val builder = AlertDialog.Builder(requireContext())
            .setItems(
                arrayOf("Editar", "Eliminar")
            ) { dialog, which ->
                if (which == 0) {

                    val dialogFragment = EventoDialogFragment()
                    dialogFragment.setDialogListener(this)
                    val bundle = Bundle()
                    bundle.putLong("idEvento", evento.id)
                    bundle.putString("tituloEvento", evento.titulo)
                    bundle.putString("fechaEvento", evento.fecha)
                    bundle.putString("descripcionEvento", evento.descripcion)
                    bundle.putString("colorEvento", evento.color)
                    bundle.putLong("contactoEvento", evento.contactoId)
                    dialogFragment.arguments = bundle
                    dialogFragment.show(parentFragmentManager, "editarEventoDialogFragment")

                } else {

                    val builder = AlertDialog.Builder(requireContext())

                    builder.setTitle("Alerta")
                        .setMessage("¿Estás seguro de que deseas borrar el evento ${evento.titulo}?")

                    builder.setPositiveButton("Sí") { _, _ ->
                        database.agendaDao().deleteEvento(evento)
                        mAdapter.delete(evento)
                    }

                    builder.setNegativeButton("No") { _, _ ->

                    }

                    builder.create().show()
                }
            }
        builder.create().show()

    }

    override fun onDialogOpened() {

    }

    override fun onDialogDismissed() {
        setRecyclerView()
    }

}