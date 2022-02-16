package com.openclassrooms.realestatemanager.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.FragmentEditBinding
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var property: Property
    private var latestTmpUri: Uri? = null
    private val args: EditFragmentArgs by navArgs()

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) latestTmpUri?.let(this::insertPhoto)
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let(this::insertPhoto)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditBinding.inflate(inflater, container, false)

        if (args.propertyId != null) {
            viewModel.getProperty(args.propertyId!!).observe(viewLifecycleOwner) {
                property = it
                bind()
            }
        } else {
            property = Property(
                id = Calendar.getInstance().timeInMillis.toString(),
                type = "",
                price = 0,
                address = "",
                sellStatus = false,
                onSaleDate = Calendar.getInstance()
            )
        }

        binding.fabSave.setOnClickListener { viewModel.insert(property) }

        binding.fabTakePicture.setOnClickListener { takeImage() }
        binding.fabAddPicture.setOnClickListener { selectImageFromGallery() }

        binding.texteditType.doAfterTextChanged { property.type = it.toString() }
        binding.texteditRooms.doAfterTextChanged { property.numberOfRooms = it.toString().toInt() }
        binding.texteditDescription.doAfterTextChanged { property.description = it.toString() }
        binding.texteditAddress.doAfterTextChanged { property.address = it.toString() }
        binding.texteditPrice.doAfterTextChanged { property.price = it.toString().toInt() }
        binding.texteditArea.doAfterTextChanged { property.area = it.toString().toInt() }
        binding.texteditRooms.doAfterTextChanged { property.numberOfRooms = it.toString().toInt() }

        binding.texteditOnSaleDate.setOnClickListener { buttonSelectDate() }

        return binding.root
    }

    private fun bind() {
        binding.texteditType.setText(property.type)
        binding.texteditDescription.setText(property.description)
        binding.texteditAddress.setText(property.address)
        binding.texteditRooms.setText(property.numberOfRooms.toString())
        binding.texteditArea.setText(property.area.toString())
        binding.texteditPrice.setText(property.price.toString())
        binding.texteditOnSaleDate.setText(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(property.onSaleDate.time)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun buttonSelectDate() {
        val cal = Calendar.getInstance()
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setSelection(cal.timeInMillis)
        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener {
            cal.timeInMillis = it
            property.onSaleDate = cal
            binding.texteditOnSaleDate.setText(
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(cal.time)
            )
        }
        activity?.let { datePicker.show(it.supportFragmentManager, "") }
    }

    private fun takeImage() {
        requireActivity().lifecycleScope.launchWhenStarted {
            getFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getFileUri(): Uri {
        val file = File(requireActivity().filesDir, "${UUID.randomUUID()}.jpg")

        return FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.provider",
            file
        )
    }

    private fun insertPhoto(uri: Uri) {
        val newPhoto = Photo(
            id = Calendar.getInstance().timeInMillis.toString(),
            title = "",
            uri = uri,
            propertyId = property.id
        )
        viewModel.insertPhoto(newPhoto)
        binding.previewImage.setImageURI(uri)
    }
}