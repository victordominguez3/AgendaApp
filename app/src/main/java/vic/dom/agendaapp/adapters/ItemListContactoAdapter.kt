package vic.dom.agendaapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vic.dom.agendaapp.R
import vic.dom.agendaapp.database.AgendaApplication.Companion.database
import vic.dom.agendaapp.databinding.ItemContactoLayoutBinding
import vic.dom.agendaapp.models.Contacto

class ItemListContactoAdapter(private var listItem: MutableList<Contacto>, private var listener: OnContactoClickListener) : RecyclerView.Adapter<ItemListContactoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contacto_layout, parent, false)
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

    fun setContactos(contactos: MutableList<Contacto>) {
        listItem = contactos
        notifyDataSetChanged()
    }

    fun delete(contacto: Contacto) {
        val index = listItem.indexOf(contacto)
        if (index != -1){
            listItem.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder (view: View): RecyclerView.ViewHolder(view) {

        private val binding = ItemContactoLayoutBinding.bind(view)

        fun bind(item: Contacto) {

            binding.numero.text = "| ${item.numero}"
            binding.nombre.text = item.nombre

            if (item.favorito) {
                binding.fav.setBackgroundResource(R.drawable.favorito_true)
            } else {
                binding.fav.setBackgroundResource(R.drawable.favorito_false)
            }

            binding.fav.setOnClickListener {
                if (binding.fav.background.constantState == ContextCompat.getDrawable(itemView.context, R.drawable.favorito_false)?.constantState) {
                    binding.fav.setBackgroundResource(R.drawable.favorito_true)
                    item.favorito = true
                    database.agendaDao().updateContacto(item)
                } else {
                    binding.fav.setBackgroundResource(R.drawable.favorito_false)
                    item.favorito = false
                    database.agendaDao().updateContacto(item)
                }
            }

        }

        fun setListener(contacto: Contacto) {

            binding.root.setOnClickListener {  }
            binding.root.setOnLongClickListener {
                listener.onLongClick(contacto)
                true
            }
        }

    }

}