package com.example.lifeLogger

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.ImageButton


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var dataCheckButton = view?.findViewById<(ImageButton)>(R.id.dataCheckButton)
    var connectButton = view?.findViewById<(ImageButton)>(R.id.connectButton)
    var samsungButton = view?.findViewById<(ImageButton)>(R.id.samsungButton)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view:View = inflater.inflate(R.layout.fragment_home, container, false)
        val HealthConnect = "com.google.android.apps.healthdata"
        val SamsungHealth = "com.sec.android.app.shealth"


        dataCheckButton = view?.findViewById<(ImageButton)>(R.id.dataCheckButton)
        connectButton = view?.findViewById<(ImageButton)>(R.id.connectButton)
        samsungButton = view?.findViewById<(ImageButton)>(R.id.samsungButton)

        dataCheckButton?.setOnClickListener{view->
            println("클릭")

            val dataSituationIntent = Intent(activity, DataSituationActivity::class.java)
            startActivity(dataSituationIntent)
        }

        connectButton?.setOnClickListener{view->
            println("클릭")

            val intent = activity?.packageManager?.getLaunchIntentForPackage(HealthConnect)


            if (intent == null) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = (Uri.parse("market://details?id=$HealthConnect"))
                startActivity(intent)
            }else{
                startActivity(intent)
            }

        }

        samsungButton?.setOnClickListener{view->
            println("클릭")
            val intent = activity?.packageManager?.getLaunchIntentForPackage(SamsungHealth)
            if (intent == null) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = (Uri.parse("market://details?id=$SamsungHealth"))
                startActivity(intent)
            }else{
                startActivity(intent)
            }
        }

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}