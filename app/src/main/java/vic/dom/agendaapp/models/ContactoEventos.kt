package vic.dom.agendaapp.models

import androidx.room.Embedded
import androidx.room.Relation

data class ContactoEventos(

    @Embedded
    val contacto: Contacto,

    @Relation(parentColumn = "id", entityColumn = "contactoId")
    val eventos: List<Evento>

)