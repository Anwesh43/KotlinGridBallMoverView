package ui.anwesome.com.kotlingridballmoverview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.gridballmoverview.GridBallMoverView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GridBallMoverView.create(this)
    }
}
