package vic.dom.agendaapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import vic.dom.agendaapp.models.Contacto
import vic.dom.agendaapp.models.ContactoEventos
import vic.dom.agendaapp.models.Evento

@Dao
interface AgendaDao {

    @Query("SELECT * FROM TContactos")
    fun getAllContactos(): MutableList<Contacto>

    @Query("SELECT * FROM TEventos")
    fun getAllEventos(): MutableList<Evento>

    @Insert
    fun addContacto(contacto: Contacto)

    @Insert
    fun addEvento(evento: Evento)

    @Update
    fun updateContacto(contacto: Contacto)

    @Update
    fun updateEvento(evento: Evento)

    @Delete
    fun deleteContacto(contacto: Contacto)

    @Delete
    fun deleteEvento(evento: Evento)

    @Transaction
    @Query("SELECT * FROM TContactos")
    fun getContactosEventos(): List<ContactoEventos>
}