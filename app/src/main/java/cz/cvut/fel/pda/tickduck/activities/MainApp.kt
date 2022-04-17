package cz.cvut.fel.pda.tickduck.activities

import android.app.Application
import cz.cvut.fel.pda.tickduck.db.MainDB

class MainApp : Application() {

    val database by lazy {
        MainDB.getDB(this)
    }
}