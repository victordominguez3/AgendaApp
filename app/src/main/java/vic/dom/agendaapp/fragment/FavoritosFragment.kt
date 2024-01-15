package vic.dom.agendaapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import vic.dom.agendaapp.R
import vic.dom.agendaapp.adapters.ItemGridContactoAdapter
import vic.dom.agendaapp.adapters.ItemListContactoAdapter
import vic.dom.agendaapp.adapters.OnContactoClickListener
import vic.dom.agendaapp.database.AgendaApplication.Companion.database
import vic.dom.agendaapp.databinding.FragmentFavoritosBinding
import vic.dom.agendaapp.models.Contacto
import java.util.concurrent.LinkedBlockingQueue

class FavoritosFragment : Fragment(), OnContactoClickListener, ContactoDialogFragment.DialogListener {

    private lateinit var binding: FragmentFavoritosBinding

    private lateinit var mListAdapter: ItemListContactoAdapter
    private lateinit var mGridAdapter: ItemGridContactoAdapter

    private lateinit var mListLayoutManager: LinearLayoutManager
    private lateinit var mGridLayoutManager: StaggeredGridLayoutManager

    private var vistaActual = "Lista"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false)
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

                if (vistaActual == "Lista") {
                    mListAdapter.setContactos(queue.take()
                        .filter { it.nombre.lowercase().contains(s.toString().lowercase()) }
                        .sortedBy { it.nombre.lowercase() }
                        .filter { it.favorito }.toMutableList())
                } else {
                    mGridAdapter.setContactos(queue.take()
                        .filter { it.nombre.lowercase().contains(s.toString().lowercase()) }
                        .sortedBy { it.nombre.lowercase() }
                        .filter { it.favorito }.toMutableList())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        binding.opciones.setOnClickListener {
            mostrarMenu(it)
        }
    }

    private fun setListRecyclerView() {

        val lista = database.agendaDao().getAllContactos()
            .filter { it.favorito }
            .sortedBy { it.nombre.lowercase() }.toMutableList()

        mListAdapter = ItemListContactoAdapter(lista, this)
        mListLayoutManager = LinearLayoutManager(requireContext())

        binding.recycler.apply {
            layoutManager = mListLayoutManager
            adapter = mListAdapter
        }

    }

    private fun setGridRecyclerView() {

        val lista = database.agendaDao().getAllContactos()
            .filter { it.favorito }
            .sortedBy { it.nombre.lowercase() }.toMutableList()

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
        inflater.inflate(R.menu.popup_favoritos, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.agregar_favorito -> {

                    val dialogFragment = ContactoDialogFragment()
                    dialogFragment.setDialogListener(this)
                    dialogFragment.show(parentFragmentManager, "nuevoContactoFavoritoDialogFragment")

                    true
                }
                R.id.cambiar_vista_fav -> {

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

    override fun onClick(id: Long) {

    }

    override fun onLongClick(contacto: Contacto) {
        val builder = AlertDialog.Builder(requireContext())
            .setItems(
                arrayOf("Editar", "Eliminar")
            ) { dialog, which ->
                if (which == 0) {

                    val dialogFragment = ContactoDialogFragment()
                    dialogFragment.setDialogListener(this)
                    val bundle = Bundle()
                    bundle.putString("nombreContacto", contacto.nombre)
                    bundle.putString("numeroContacto", contacto.numero)
                    bundle.putString("correoContacto", contacto.correo)
                    bundle.putString("imagenContacto", contacto.imagen)
                    bundle.putBoolean("favoritoContacto", contacto.favorito)
                    dialogFragment.arguments = bundle
                    dialogFragment.show(parentFragmentManager, "editarContactoDialogFragment")

                } else {

                    if (database.agendaDao().getAllEventos().find { it.contactoId == contacto.id } == null) {

                        val builder = AlertDialog.Builder(requireContext())

                        builder.setTitle("Alerta")
                            .setMessage("¿Estás seguro de que deseas borrar el contacto ${contacto.nombre}?")

                        builder.setPositiveButton("Sí") { _, _ ->
                            database.agendaDao().deleteContacto(contacto)
                            mListAdapter.delete(contacto)
                            mGridAdapter.delete(contacto)
                        }

                        builder.setNegativeButton("No") { _, _ ->

                        }

                        builder.create().show()

                    } else {

                        val builder = AlertDialog.Builder(requireContext())

                        builder.setTitle("Alerta")
                            .setMessage("El contacto ${contacto.nombre} tiene eventos vinculados. ¿Desea borrar el contacto y todos sus eventos?")

                        builder.setPositiveButton("Sí") { _, _ ->
                            database.agendaDao().getAllEventos().filter { it.contactoId == contacto.id }.forEach { evento -> database.agendaDao().deleteEvento(evento) }
                            database.agendaDao().deleteContacto(contacto)
                            mListAdapter.delete(contacto)
                            mGridAdapter.delete(contacto)
                        }

                        builder.setNegativeButton("No") { _, _ ->

                        }

                        builder.create().show()
                    }

                }
            }
        builder.create().show()
    }

    override fun onDialogOpened() {

    }

    override fun onDialogDismissed() {
        setListRecyclerView()
    }

}