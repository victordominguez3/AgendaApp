package vic.dom.agendaapp.adapters

import vic.dom.agendaapp.models.Contacto

interface OnContactoClickListener {

    fun onClick(id: Long)
    fun onLongClick(contacto: Contacto)

}