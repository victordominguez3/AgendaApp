package vic.dom.agendaapp.database

import android.app.Application
import androidx.room.Room

class AgendaApplication: Application() {

    companion object {
        lateinit var database: AgendaDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(this,
            AgendaDatabase::class.java,
            "db_agenda")
            .allowMainThreadQueries().enableMultiInstanceInvalidation().build()
    }

}