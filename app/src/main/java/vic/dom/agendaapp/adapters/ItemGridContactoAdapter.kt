package vic.dom.agendaapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import vic.dom.agendaapp.R
import vic.dom.agendaapp.database.AgendaApplication.Companion.database
import vic.dom.agendaapp.databinding.ItemTarjetaContactoLayoutBinding
import vic.dom.agendaapp.models.Contacto

class ItemGridContactoAdapter(private var listItem: MutableList<Contacto>, private var listener: OnContactoClickListener) : RecyclerView.Adapter<ItemGridContactoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tarjeta_contacto_layout, parent, false)
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

        val binding = ItemTarjetaContactoLayoutBinding.bind(view)

        fun bind(item: Contacto) {

            binding.numero.text = item.numero
            binding.nombre.text = item.nombre
            binding.correo.text = item.correo

            if (item.imagen.isNotEmpty()) {
                try {
                    Glide.with(binding.imagen)
                        .load(Uri.parse(item.imagen))
                        .transform(CenterCrop(), RoundedCorners(10))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.imagen)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Glide.with(binding.imagen)
                        .load(R.drawable.fotonula)
                        .transform(CenterCrop(), RoundedCorners(10))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.imagen)
                }
            } else {
                Glide.with(binding.imagen)
                    .load(R.drawable.fotonula)
                    .transform(CenterCrop(), RoundedCorners(10))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.imagen)
            }

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