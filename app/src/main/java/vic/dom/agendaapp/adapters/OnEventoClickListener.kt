package vic.dom.agendaapp.adapters

import vic.dom.agendaapp.models.Evento

interface OnEventoClickListener {

    fun onClick(id: Long)
    fun onLongClick(evento: Evento)

}