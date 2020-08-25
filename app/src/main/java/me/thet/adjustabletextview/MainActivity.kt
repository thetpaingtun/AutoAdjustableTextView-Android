package me.thet.adjustabletextview

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var mText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        txt1.setOnClickListener(this)
        txt2.setOnClickListener(this)
        txt3.setOnClickListener(this)
        txt4.setOnClickListener(this)
        txt5.setOnClickListener(this)
        txt6.setOnClickListener(this)
        txt7.setOnClickListener(this)
        txt8.setOnClickListener(this)
        txt9.setOnClickListener(this)
        txt10.setOnClickListener(this)
        txt11.setOnClickListener(this)
        txt12.setOnClickListener(this)
        txt13.setOnClickListener(this)
        txt14.setOnClickListener(this)
        txt15.setOnClickListener(this)
        txt16.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        if (v is TextView) {
            val t = v.text
            if (!t.isEmpty() && t != "DEL") {
                mText += t
            } else if (t == "DEL" && mText.length > 0) {
                mText = mText.removeRange(IntRange(mText.lastIndex, mText.lastIndex))
            }
        }

        adjTxt.setText(mText)
    }

}