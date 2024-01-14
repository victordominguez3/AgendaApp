package vic.dom.agendaapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import vic.dom.agendaapp.R
import vic.dom.agendaapp.adapters.ItemEventoAdapter
import vic.dom.agendaapp.adapters.ItemListContactoAdapter
import vic.dom.agendaapp.adapters.OnContactoClickListener
import vic.dom.agendaapp.database.AgendaApplication
import vic.dom.agendaapp.database.AgendaApplication.Companion.database
import vic.dom.agendaapp.databinding.FragmentEventosBinding
import vic.dom.agendaapp.databinding.FragmentFavoritosBinding
import vic.dom.agendaapp.models.Contacto

class FavoritosFragment : Fragment(), OnContactoClickListener {

    private lateinit var binding: FragmentFavoritosBinding

    private lateinit var mAdapter: ItemListContactoAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
    }

    private fun setRecyclerView() {

        val lista = database.agendaDao().getAllContactos().filter { it.favorito }.toMutableList()

        mAdapter = ItemListContactoAdapter(lista, this)
        mLayoutManager = LinearLayoutManager(requireContext())

        binding.recycler.apply {
            layoutManager = mLayoutManager
            adapter = mAdapter
        }

    }

    override fun onClick(id: Long) {

    }

    override fun onLongClick(contacto: Contacto) {

    }

}