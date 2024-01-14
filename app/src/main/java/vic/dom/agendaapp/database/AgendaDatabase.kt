package vic.dom.agendaapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import vic.dom.agendaapp.models.Contacto
import vic.dom.agendaapp.models.Evento

@Database(entities = [Contacto::class, Evento::class], version = 1)
abstract class AgendaDatabase: RoomDatabase() {
    abstract fun agendaDao(): AgendaDao
}