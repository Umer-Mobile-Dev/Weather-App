package com.appozee.technologies.supertalassignment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appozee.technologies.supertalassignment.databinding.BottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A BottomSheetDialogFragment that allows user input and triggers a callback when a button is clicked.
 */
class MyBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetLayoutBinding
    private var listener: BottomSheetListener? = null

    /**
     * Interface for listening to button clicks within the bottom sheet.
     */
    interface BottomSheetListener {
        /**
         * Called when the button within the bottom sheet is clicked.
         * @param text The text entered by the user in the edit text field.
         */
        fun onButtonClicked(text: String)
    }

    /**
     * Initializes the view of the BottomSheetDialogFragment.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetLayoutBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set up click listener for the button
        binding.button.setOnClickListener {
            val text = binding.editText.text.toString().trim()
            // Notify listener with the entered text and dismiss the bottom sheet
            listener?.onButtonClicked(text)
            dismiss()
        }

        return view
    }

    /**
     * Sets the listener for button clicks within the bottom sheet.
     * @param listener The listener to be set.
     */
    fun setBottomSheetListener(listener: BottomSheetListener) {
        this.listener = listener
    }
}