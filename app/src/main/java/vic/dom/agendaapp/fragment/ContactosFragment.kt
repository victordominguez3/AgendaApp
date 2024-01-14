package vic.dom.agendaapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import vic.dom.agendaapp.R
import vic.dom.agendaapp.adapters.ItemGridContactoAdapter
import vic.dom.agendaapp.adapters.ItemListContactoAdapter
import vic.dom.agendaapp.adapters.OnContactoClickListener
import vic.dom.agendaapp.database.AgendaApplication.Companion.database
import vic.dom.agendaapp.databinding.FragmentContactosBinding
import vic.dom.agendaapp.dialogs.ContactoDialog
import vic.dom.agendaapp.models.Contacto
import java.util.concurrent.LinkedBlockingQueue


class ContactosFragment : Fragment(), OnContactoClickListener {

    private lateinit var binding: FragmentContactosBinding

    private lateinit var mListAdapter: ItemListContactoAdapter
    private lateinit var mGridAdapter: ItemGridContactoAdapter

    private lateinit var mListLayoutManager: LinearLayoutManager
    private lateinit var mGridLayoutManager: StaggeredGridLayoutManager

    private var vistaActual = "Lista"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setGridRecyclerView()
        setListRecyclerView()

        binding.busqueda.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val queue = LinkedBlockingQueue<MutableList<Contacto>>()

                Thread {
                    val list = database.agendaDao().getAllContactos()
                    queue.add(list)
                }.start()

                mListAdapter.setContactos(queue.take()
                    .filter { it.nombre.lowercase().contains(s.toString().lowercase()) }.toMutableList())
                mGridAdapter.setContactos(queue.take()
                    .filter { it.nombre.lowercase().contains(s.toString().lowercase()) }.toMutableList())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.opciones.setOnClickListener {
            mostrarMenu(it)
        }

    }

    private fun setListRecyclerView() {

        val lista = database.agendaDao().getAllContactos().sortedBy { it.nombre }.toMutableList()

        mListAdapter = ItemListContactoAdapter(lista, this)
        mListLayoutManager = LinearLayoutManager(requireContext())

        binding.recycler.apply {
            layoutManager = mListLayoutManager
            adapter = mListAdapter
        }

    }

    private fun setGridRecyclerView() {

        val lista = database.agendaDao().getAllContactos().sortedBy { it.nombre }.toMutableList()

        mGridAdapter = ItemGridContactoAdapter(lista, this)
        mGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        binding.recycler.apply {
            layoutManager = mGridLayoutManager
            adapter = mGridAdapter
        }

    }

    private fun mostrarMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.popup_contactos, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.agregar_contacto -> {
                    val dialogo = ContactoDialog()
                    dialogo.showAlertDialog(
                        requireContext(),
                        object : ContactoDialog.ContactoDialogListener {
                            override fun onPositiveButtonClick(
                                text1: String,
                                text2: String,
                                text3: String,
                                image: String,
                                switch: Boolean
                            ) {
                                val contacto = Contacto(nombre = text1, numero = text2, correo = text3, imagen = image, favorito = switch)

                                database.agendaDao().addContacto(contacto)
                                mListAdapter.setContactos(database.agendaDao().getAllContactos())
                                mGridAdapter.setContactos(database.agendaDao().getAllContactos())
                            }

                            override fun onNegativeButtonClick() {

                            }

                        },
                        "",
                        "",
                        "",
                        "",
                        false
                    )

                    true
                }
                R.id.cambiar_vista -> {

                    vistaActual = if (vistaActual == "Lista") {
                        setGridRecyclerView()
                        "Grid"
                    } else {
                        setListRecyclerView()
                        "Lista"
                    }

                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onClick(id: Long) {}

    override fun onLongClick(contacto: Contacto) {

        val builder1 = AlertDialog.Builder(requireContext())
            .setItems(
                arrayOf("Editar", "Eliminar")
            ) { dialog, which ->
                if (which == 0) {
                    val dialogo = ContactoDialog()
                    dialogo.showAlertDialog(
                        requireContext(),
                        object : ContactoDialog.ContactoDialogListener {
                            override fun onPositiveButtonClick(
                                text1: String,
                                text2: String,
                                text3: String,
                                image: String,
                                switch: Boolean
                            ) {
                                contacto.nombre = text1
                                contacto.numero = text2
                                contacto.correo = text3
                                contacto.imagen = image
                                contacto.favorito = switch

                                database.agendaDao().updateContacto(contacto)
                                mListAdapter.setContactos(database.agendaDao().getAllContactos())
                                mGridAdapter.setContactos(database.agendaDao().getAllContactos())
                            }

                            override fun onNegativeButtonClick() {

                            }

                        },
                        contacto.nombre,
                        contacto.numero,
                        contacto.correo,
                        contacto.imagen,
                        contacto.favorito
                    )

                } else {
                    val builder2 = AlertDialog.Builder(requireContext())

                    builder2.setTitle("Alerta")
                        .setMessage("¿Estás seguro de que deseas borrar el contacto ${contacto.nombre}?")

                    builder2.setPositiveButton("Sí") { _, _ ->
                        database.agendaDao().deleteContacto(contacto)
                        mListAdapter.delete(contacto)
                        mGridAdapter.delete(contacto)
                    }

                    builder2.setNegativeButton("No") { _, _ ->

                    }

                    builder2.create().show()
                }
            }
        builder1.create().show()

    }


}