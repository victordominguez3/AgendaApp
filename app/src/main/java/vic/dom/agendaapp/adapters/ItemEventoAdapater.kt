package vic.dom.agendaapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vic.dom.agendaapp.R
import vic.dom.agendaapp.database.AgendaApplication.Companion.database
import vic.dom.agendaapp.databinding.ItemContactoLayoutBinding
import vic.dom.agendaapp.databinding.ItemEventoLayoutBinding
import vic.dom.agendaapp.models.Contacto
import vic.dom.agendaapp.models.Evento

class ItemEventoAdapter(private var listItem: MutableList<Evento>, private var listener: OnEventoClickListener) : RecyclerView.Adapter<ItemEventoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evento_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItem[position]
        holder.bind(item)
        holder.setListener(item)
    }

    fun setEventos(eventos: MutableList<Evento>) {
        listItem = eventos
        notifyDataSetChanged()
    }

    fun delete(evento: Evento) {
        val index = listItem.indexOf(evento)
        if (index != -1){
            listItem.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder (view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemEventoLayoutBinding.bind(view)

        fun bind(item: Evento) {

            binding.titulo.text = item.titulo
            binding.fecha.text = item.fecha
            binding.descripcion.text = item.descripcion
            binding.contacto.text = database.agendaDao().getAllContactos().find { it.id == item.contactoId }?.nombre
            binding.tarjeta.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, when(item.color) {
                "Azul" -> R.color.Azul
                "Rojo" -> R.color.Rojo
                "Amarillo" -> R.color.Amarillo
                "Verde" -> R.color.Verde
                "Morado" -> R.color.Morado
                "Naranja" -> R.color.Naranja
                else -> R.color.Rosa
            }))

        }

        fun setListener(evento: Evento) {

            binding.root.setOnClickListener {  }
            binding.root.setOnLongClickListener {
                listener.onLongClick(evento)
                true
            }
        }

    }

}