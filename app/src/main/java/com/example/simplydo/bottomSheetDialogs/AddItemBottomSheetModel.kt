package com.example.simplydo.bottomSheetDialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.simplydo.R
import com.example.simplydo.utli.AppInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddItemBottomSheetModel(val addItemInterface: AppInterface.AddContent) :
    BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item_bottom_sheet_model, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val content = view.findViewById<EditText>(R.id.edit_text_content)
        val addButton = view.findViewById<Button>(R.id.button_add)



        addButton.setOnClickListener {
            if (content.text.isNotEmpty()) {
                addItemInterface.onAdd(content = content.text.toString())
            } else {
                content.error = "Field Required"
            }
        }


    }

    companion object {
        fun newInstance(addItemInterface: AppInterface.AddContent) =
            AddItemBottomSheetModel(addItemInterface)
    }
}