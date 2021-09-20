package com.drozdova.dancersbase.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.drozdova.dancersbase.R
import com.drozdova.dancersbase.database.Dancer
import com.drozdova.dancersbase.databinding.FragmentSecondBinding
import com.drozdova.dancersbase.viewmodel.DancerViewModel
import java.util.*

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private lateinit var dancerViewModel: DancerViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dancerViewModel = ViewModelProvider(requireActivity()).get(DancerViewModel::class.java)

        val editId = arguments?.getInt("editId")

        if (editId != null && editId != 0){
            binding.buttonInsert.text = getString(R.string.btn_update_dancer)
            binding.toolbar.title = getString(R.string.fragment_edit_title)
            dancerViewModel.allDancers.observe(viewLifecycleOwner) {
                val editedDancer = it.find { dancer -> dancer.id == editId }
                binding.textInputName.setText(editedDancer?.name)
                binding.textInputYearBirth.setText(editedDancer?.year.toString())
                binding.textInputClub.setText(editedDancer?.club)
                binding.textInputLeague.setText(editedDancer?.league)

                dancerViewModel.allDancers.removeObservers(viewLifecycleOwner)
            }
        }

        //TextInputs checks
        checkEmptyTexts()
        binding.textInputName.addTextChangedListener(myTextWatcher)
        binding.textInputYearBirth.addTextChangedListener(myTextWatcher)
        binding.textInputClub.addTextChangedListener(myTextWatcher)
        binding.textInputLeague.addTextChangedListener(myTextWatcher)

        binding.textInputName.setOnEditorActionListener { textView, i, _ ->
            if (textView.text.isEmpty() && i == EditorInfo.IME_ACTION_NEXT) {
                binding.textInputLayName.error = getString(R.string.empty_name_error)
            } else {
                binding.textInputLayName.error = null
                binding.textInputYearBirth.requestFocus()
            }
            return@setOnEditorActionListener true
        }
        binding.textInputYearBirth.setOnEditorActionListener { textView, i, _ ->
            if ((textView.text.isEmpty() || textView.text.toString().toIntOrNull() == null
                        || textView.text.toString().toInt() <= 0)
                && i == EditorInfo.IME_ACTION_NEXT){
                binding.textInputLayYearBirth.error = getString(R.string.negative_year_birth_error)
            } else {
                if(textView.text.toString().toInt() >= Calendar.getInstance().get(Calendar.YEAR) - 3) {
                    binding.textInputLayYearBirth.error = getString(R.string.negative_age_error)
                } else {
                    binding.textInputLayYearBirth.error = null
                    binding.textInputClub.requestFocus()
                }
            }
            return@setOnEditorActionListener true
        }
        binding.textInputClub.setOnEditorActionListener { textView, i, _ ->
            if (textView.text.isEmpty() && i == EditorInfo.IME_ACTION_DONE) {
                binding.textInputLayClub.error = getString(R.string.empty_club_error)
            } else {
                binding.textInputLayClub.error = null
                binding.textInputLeague.requestFocus()
            }
            return@setOnEditorActionListener true
        }

        binding.textInputLeague.setOnEditorActionListener { textView, i, _ ->
            if (textView.text.isEmpty() && i == EditorInfo.IME_ACTION_DONE) {
                binding.textInputLayLeague.error = getString(R.string.empty_league_error)
            } else {
                binding.textInputLayLeague.error = null
                binding.textInputLeague.clearFocus()
                hideKeyboard(view)
            }
            return@setOnEditorActionListener true
        }

        binding.buttonInsert.setOnClickListener {
            hideKeyboard(view)
            if (editId != null && editId != 0){
                val newDancer = Dancer(editId, binding.textInputName.text.toString(),
                    Integer.parseInt(binding.textInputYearBirth.text.toString()),
                    binding.textInputClub.text.toString(),
                    binding.textInputLeague.text.toString())

                dancerViewModel.update(newDancer)
                findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)

            } else {
                val newDancer = Dancer(0, binding.textInputName.text.toString(),
                    Integer.parseInt(binding.textInputYearBirth.text.toString()),
                    binding.textInputClub.text.toString(),
                    binding.textInputLeague.text.toString())

                dancerViewModel.allDancers.observe(viewLifecycleOwner) {
                    val editedDancer = it.find { a -> a.name == newDancer.name &&
                            a.year == newDancer.year && a.club == newDancer.club &&
                            a.league == newDancer.league}
                    if (editedDancer == null){
                        dancerViewModel.insert(newDancer)
                        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                    }else{
                        Toast.makeText(context,getString(R.string.duplicate_error), Toast.LENGTH_SHORT).show()
                    }

                    dancerViewModel.allDancers.removeObservers(viewLifecycleOwner)
                }
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    private val myTextWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {
            checkEmptyTexts()
        }
    }

    private fun hideKeyboard(view: View){
        val inputMethodManager = getSystemService(requireContext(), InputMethodManager::class.java) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun checkEmptyTexts(){
        val name = binding.textInputName.text
        val year = binding.textInputYearBirth.text.toString().toIntOrNull()
        val club = binding.textInputClub.text
        val league = binding.textInputLeague.text
        if (name != null && year != null && club != null && league != null){
            binding.buttonInsert.isEnabled = name.isNotEmpty() &&
                    (year > 0) &&
                    club.isNotEmpty() &&
                    league.isNotEmpty()
        }else binding.buttonInsert.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}