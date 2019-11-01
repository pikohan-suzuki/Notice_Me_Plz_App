package com.amebaownd.pikohan_nwiatori.noticemeplz.chooseService

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.amebaownd.pikohan_nwiatori.noticemeplz.MainActivity
import com.amebaownd.pikohan_nwiatori.noticemeplz.databinding.FragmentChooseServiceBinding
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.EventObserver
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.getViewModelFactory

class ChooseServiceFragment :Fragment(){

    private val chooseServiceViewModel :ChooseServiceViewModel by viewModels { getViewModelFactory() }
    lateinit var fragmentChooseServiceBinding: FragmentChooseServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChooseServiceBinding = FragmentChooseServiceBinding.inflate(layoutInflater,container,false).apply {
            viewModel = chooseServiceViewModel
        }
        fragmentChooseServiceBinding.lifecycleOwner = this.viewLifecycleOwner
        return fragmentChooseServiceBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onFabClicked()
        onAddressChanged()
    }

    private fun onFabClicked(){
        chooseServiceViewModel.submitEvent.observe(this,EventObserver{
            if(it)
                navigateToAddEditUserFragment()
        })
    }

    private fun onAddressChanged(){
        chooseServiceViewModel.address.observe(this, Observer {
            chooseServiceViewModel.onAddressChanged()
        })
    }

    private fun navigateToAddEditUserFragment(){
        val action = ChooseServiceFragmentDirections
            .actionChooseServiceFragmentToAddEditUserFragment(
                userId = null,
                title= (this.activity as MainActivity).viewModel.user?.name ?: "ユーザーを追加",
                selectedServiceCode = chooseServiceViewModel.selectedService.value ?: -1,
                address = chooseServiceViewModel.address.value
            )
        findNavController().navigate(action)
    }
}