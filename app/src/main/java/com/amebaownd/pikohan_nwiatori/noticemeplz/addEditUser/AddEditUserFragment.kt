package com.amebaownd.pikohan_nwiatori.noticemeplz.addEditUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.amebaownd.pikohan_nwiatori.noticemeplz.MainActivity
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.model.UsingService
import com.amebaownd.pikohan_nwiatori.noticemeplz.data.repository.AddEditRepository
import com.amebaownd.pikohan_nwiatori.noticemeplz.databinding.FragmentAddEditBinding
import com.amebaownd.pikohan_nwiatori.noticemeplz.userList.UserListFragmentDirections
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.EventObserver
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.getViewModelFactory
import kotlinx.android.synthetic.main.fragment_add_edit.*

class AddEditUserFragment :Fragment(){

    private val addEditUserViewModel :AddEditUserViewModel by viewModels {getViewModelFactory()}
    private val args :AddEditUserFragmentArgs by navArgs()

    private lateinit var addEditUserAdapter:AddEditUserAdapter
    private lateinit var fragmentAddEditBinding:FragmentAddEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAddEditBinding = FragmentAddEditBinding.inflate(inflater,container,false).apply {
            viewModel = addEditUserViewModel
        }
        fragmentAddEditBinding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(true)

        return fragmentAddEditBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        setupRecyclerView()
        val activityViewModel= (this.activity as MainActivity).viewModel
        val userId= args.userId ?: activityViewModel.user?.id
        val name = activityViewModel.name
        val usingServices = activityViewModel.usingServices
        addEditUserViewModel.start(userId,name,usingServices,args.selectedServiceCode,args.address)
        onAddService()
        onNameChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun setupFab(){
        addEditUserViewModel.submitEvent.observe(this,EventObserver{
            if(it)
                navigateToUserListFragment()
        })
    }

    private fun onNameChanged(){
        addEditUserViewModel.name.observe(this, Observer {
            addEditUserViewModel.isAbleToSave()
        })
    }

    private fun onAddService(){
        addEditUserViewModel.addServiceEvent.observe(this,EventObserver{
            if(it){
                val mainActivity = this.activity as MainActivity
                mainActivity.viewModel.user= addEditUserViewModel.userAndUsingService.value?.user
                mainActivity.viewModel.usingServices= addEditUserViewModel.usingServices.value
                mainActivity.viewModel.name = addEditUserViewModel.name.value
                navigateToChooseServiceFragment()
            }
        })
    }

    private fun setupRecyclerView(){
        val viewModel = fragmentAddEditBinding.viewModel
        if(viewModel != null) {
            addEditUserAdapter = AddEditUserAdapter(viewModel)
            add_edit_services_recyclerView.adapter = addEditUserAdapter
            add_edit_services_recyclerView.addItemDecoration(
                DividerItemDecoration(
                    this.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun navigateToUserListFragment(){
        val action =  AddEditUserFragmentDirections
            .actionAddEditUserFragmentToUserListFragment()
        findNavController().navigate(action)
    }

    private fun navigateToChooseServiceFragment(){
        val action = AddEditUserFragmentDirections
            .actionAddEditUserFragmentToChooseServiceFragment()
        findNavController().navigate(action)
    }
}