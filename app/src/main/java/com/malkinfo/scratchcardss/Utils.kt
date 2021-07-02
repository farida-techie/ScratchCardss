package com.malkinfo.scratchcardss

import android.content.Context
import java.util.*

object Utils {
    var random = Random()

    fun dipToPx(context:Context,dipValue:Float):Float{
        val density = context.resources.displayMetrics.density
        return dipValue * density
    }
    /**Generate random number (Prize)*/
    private fun generateCodePart(min:Int,max:Int):String
    {
        val minNumber = 10
        val maxNumber = 200
        return (random.nextInt(maxNumber - minNumber + 1)+minNumber).toString()
    }
    fun generateNewCode():String{
        val firstCodePart = generateCodePart(1000,9999)
        return "You Won \nRs. $firstCodePart"

    }
}