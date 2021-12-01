package com.example.simplydo.ui.fragments.attachmentsFragments.document

import android.app.Activity
import android.content.ClipData
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.FilesAdapter
import com.example.simplydo.databinding.DocumentListFragmentBinding
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppFunctions

internal val TAG = FileListFragment::class.java.canonicalName

class FileListFragment : Fragment(R.layout.document_list_fragment) {

    companion object {
        fun newInstance() = FileListFragment()
    }

    private lateinit var _binding: DocumentListFragmentBinding

    private lateinit var filesAdapter: FilesAdapter
    private var arrayListFile: ArrayList<FileModel> = ArrayList()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    private lateinit var listViewModel: FilesListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DocumentListFragmentBinding.bind(view)
        listViewModel = ViewModelProvider(this).get(FilesListViewModel::class.java)

        filesAdapter = FilesAdapter()
        binding.recyclerViewDocumentList.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2)
            adapter = filesAdapter
        }


        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes

                    result.data?.let {
                        it.clipData?.also { clipData ->
                            // Perform operations on the document using its URI.
                            Log.i(TAG, "onActivityResult: uri--> $clipData.")
                            getItemFromClipData(clipData)
                        }


                        it.data?.also { uri ->
                            // Perform operations on the document using its URI.
                            Log.i(TAG, "onActivityResult: uri--> $uri")
                            getSelectItemData(uri)
                        }
                    }

                }
            }

        binding.buttonSelectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"

                // Optionally, specify a URI for the file that should appear in the
                // system file picker when it loads.
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOCUMENTS)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                putExtra(Intent.ACTION_CLOSE_SYSTEM_DIALOGS, true)
            }
            resultLauncher.launch(intent)
        }

        binding.buttonAddFile.setOnClickListener {
            if (arrayListFile.isNotEmpty()) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    AppConstant.Key.NAVIGATION_FILES_DATA_KEY,
                    arrayListFile
                )
                findNavController().popBackStack()
            } else {
                AppFunctions.showSnackBar(binding.root, "No files selected")
            }
        }

        binding.imageButtonClose.setOnClickListener {
            findNavController().navigateUp()
        }

    }


    private fun getItemFromClipData(clipData: ClipData) {

        for (i in 0 until clipData.itemCount) {
//            clipData.getItemAt(i)
            Log.i(TAG, "getitemFromClipData: ${clipData.getItemAt(i).uri}")
            getSelectItemData(clipData.getItemAt(i).uri)
        }

    }

    @WorkerThread
    private fun getSelectItemData(uri: Uri) {

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.VOLUME_NAME,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
        )
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)

        cursor?.run {

            cursor.moveToFirst()

            val columnId = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val fileSize = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
            val createdDate =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
            val fileType = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            Log.i(TAG, "getSelectItemData: ${cursor.count}")

            val id = cursor.getLong(columnId)
            val name = cursor.getString(nameColumn)
            val size = cursor.getInt(fileSize)
            val createdAt = cursor.getString(createdDate)
            val fileDataType = cursor.getInt(fileType)
            val mimeType =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))


            val contentUri = ContentUris.withAppendedId(
                MediaStore.Files.getContentUri(MediaStore.Files.FileColumns.VOLUME_NAME),
                id
            )

            Log.i(TAG, "getDocumentList: $id/$name")

            val newData = FileModel(
                id = id,
                name = name,
                size = size,
                createdAt = createdAt,
                fileDataType = fileDataType,
                mimeType = mimeType,
                contentUri = contentUri.toString()
            )

            Log.i(TAG, "getSelectItemData: DocumentModel -> $newData")
            arrayListFile.add(newData)

            cursor.close()
        }

        updateRecyclerView()

    }

    private fun updateRecyclerView() {
        if (arrayListFile.isEmpty()) {
            binding.recyclerViewDocumentList.visibility = View.GONE
            binding.linearLayoutNoFIleSelected.visibility = View.VISIBLE
        } else {
            filesAdapter.updateDataSet(arrayListFile)
            binding.recyclerViewDocumentList.visibility = View.VISIBLE
            binding.linearLayoutNoFIleSelected.visibility = View.GONE
        }

    }

}