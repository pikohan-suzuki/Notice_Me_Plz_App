package com.amebaownd.pikohan_nwiatori.noticemeplz.userList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.amebaownd.pikohan_nwiatori.noticemeplz.MainActivity
import com.amebaownd.pikohan_nwiatori.noticemeplz.databinding.FragmentListBinding
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.EventObserver
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.getViewModelFactory
import kotlinx.android.synthetic.main.fragment_list.*

class UserListFragment : Fragment(){

    private val userListViewModel :UserListViewModel by viewModels{getViewModelFactory()}

    private lateinit var viewDataBinding:FragmentListBinding
    private lateinit var userListAdapter:UserListAdapter
    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       viewDataBinding = FragmentListBinding.inflate(inflater,container,false).apply {
            viewModel = userListViewModel
       }
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        setupRecyclerView()
        userListViewModel.start()
        onSearchTextChanged()
        startTalk()
        resetMainViewModel()
    }

    private fun setupRecyclerView(){
        val viewModel = viewDataBinding.viewModel
        if(viewModel!=null){
            userListAdapter = UserListAdapter(viewModel,view?.context)
            viewDataBinding.userListRecyclerView.adapter=userListAdapter
            viewDataBinding.userListRecyclerView.addItemDecoration(
                DividerItemDecoration(
                    this.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun setupFab(){
        userList_addUser_fab.setOnClickListener {
            navigateToAddEditFragment()
        }
    }
    private fun resetMainViewModel(){
        val mainActivity = this.activity as MainActivity
        mainActivity.viewModel.clear()
    }

    private fun onSearchTextChanged(){
        viewDataBinding.listSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun startTalk(){
        userListViewModel.startTalkEvent.observe(this,EventObserver{
            navigateToTalkFragment(it.id,it.name)
        })
    }
    private fun navigateToTalkFragment(userId:String,name:String){
        val action = UserListFragmentDirections
            .actionUserListFragmentToTalkFragment(userId,name)
        findNavController().navigate(action)
    }

    private fun navigateToAddEditFragment(){
        val action = UserListFragmentDirections
            .actionUserListFragmentToAddEditUserFragment(
                null,
                "ユーザーを追加",
                address = null
            )
        findNavController().navigate(action)
    }
}