package vic.dom.agendaapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "TContactos")
data class Contacto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,
    @ColumnInfo(name = "nombre")
    var nombre: String = "",
    @ColumnInfo(name = "correo")
    var correo: String = "",
    @ColumnInfo(name = "numero")
    var numero: String = "",
    @ColumnInfo(name = "imagen")
    var imagen: String = "",
    @ColumnInfo(name = "favorito")
    var favorito: Boolean = false,

    @Ignore
    var eventos: List<Evento> = emptyList()
)