package com.guanmu.onehours.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.guanmu.onehours.R
import com.guanmu.onehours.view.LoadingView

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentSuccess.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class FragmentSuccess : BaseFragment() {

    private var mListener: OnFragmentInteractionListener? = null


    public override fun createView(inflater: LayoutInflater, container: ViewGroup?,
                                   savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_success, container, false)
        val continueButton = view.findViewById<View>(R.id.continue_button) as LoadingView
        continueButton.setNextable(true)
        continueButton.setOnClickListener { finishLogin() }
        return view
    }

    fun finishLogin() {
        if (mListener != null) {
            mListener!!.onFragmentFinish()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentFinish()
    }
}// Required empty public constructor
