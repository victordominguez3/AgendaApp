package vic.dom.agendaapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TEventos")
data class Evento(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,
    @ColumnInfo(name = "contactoId")
    var contactoId: Long = 0L,
    @ColumnInfo(name = "titulo")
    var titulo: String = "",
    @ColumnInfo(name = "fecha")
    var fecha: String = "",
    @ColumnInfo(name = "color")
    var color: String = "",
    @ColumnInfo(name = "descripcion")
    var descripcion: String = ""
)