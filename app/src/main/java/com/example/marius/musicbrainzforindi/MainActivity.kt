package com.example.marius.musicbrainzforindi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showFragment(PlacesFragment())
    }

    private fun showFragment(fragment: Fragment) {
        val tag = fragment.javaClass.name
        val transaction = supportFragmentManager.beginTransaction()
        transaction.addToBackStack(tag)
        transaction.replace(R.id.mainFragmentContainer, fragment, tag).commit()
    }
}
