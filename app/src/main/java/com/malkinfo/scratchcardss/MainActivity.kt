package com.malkinfo.scratchcardss

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.malkinfo.scratchcardss.Utils.generateNewCode

class MainActivity : AppCompatActivity() {

    private var mScratchCard:ScratchCard? = null
    private lateinit var codeTxt:TextView
    var number:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mScratchCard = findViewById(R.id.scratchCard)
        codeTxt= findViewById(R.id.codeTxt)
        number = codeTxt.text.toString()
        codeTxt.text = number
        codeTxt.text = generateNewCode()
        findViewById<Button>(R.id.btnScratchAgain).setOnClickListener {
            finish()
            startActivity(intent)
        }

    }
}