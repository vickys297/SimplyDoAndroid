package com.example.simplydo.screens.todoList.addNewTodo.contactListView

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.example.simplydo.R
import com.example.simplydo.databinding.FragmentContactListDialogBinding

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

class ContactFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private var _binding: FragmentContactListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Define global mutable variables
    // Define a ListView object
    lateinit var contactsList: ListView

    // Define variables for the contact the user selects
    // The contact's _ID value
    var contactId: Long = 0

    // The contact's LOOKUP_KEY
    var contactKey: String? = null

    // A content URI for the selected contact
    var contactUri: Uri? = null

    // An adapter that binds the result Cursor to the ListView
    private var cursorAdapter: SimpleCursorAdapter? = null
    private val TO_IDS: IntArray = intArrayOf(android.R.id.text1)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentContactListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity?.also {
            contactsList = it.findViewById(R.id.list) as ListView

            cursorAdapter = SimpleCursorAdapter(
                it,
                R.layout.fragment_contact_list_dialog_item, null, FROM_COLUMNS, TO_IDS,
                0
            )

            contactsList.adapter = cursorAdapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val contactPermission =
            requireActivity().applicationContext.checkSelfPermission(android.Manifest.permission.READ_CONTACTS)

        if (contactPermission != PackageManager.PERMISSION_GRANTED)
            requireActivity().requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 8700)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            8700 -> {
                if (grantResults.equals(PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        TODO("Not yet implemented")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        TODO("Not yet implemented")
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        TODO("Not yet implemented")
    }
}

@SuppressLint("InlinedApi")
private val FROM_COLUMNS: Array<String> = arrayOf(
    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
    } else {
        ContactsContract.Contacts.DISPLAY_NAME
    }
)