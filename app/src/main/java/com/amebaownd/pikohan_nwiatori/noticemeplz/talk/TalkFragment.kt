package com.amebaownd.pikohan_nwiatori.noticemeplz.talk

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amebaownd.pikohan_nwiatori.noticemeplz.MainActivity
import com.amebaownd.pikohan_nwiatori.noticemeplz.R
import com.amebaownd.pikohan_nwiatori.noticemeplz.databinding.FragmentTalkBinding
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.EventObserver
import com.amebaownd.pikohan_nwiatori.noticemeplz.util.getViewModelFactory

import java.util.Calendar

class TalkFragment : Fragment() {

    private val talkViewModel: TalkViewModel by viewModels { getViewModelFactory() }
    private val args: TalkFragmentArgs by navArgs()

    private lateinit var fragmentTalkBinding: FragmentTalkBinding
    private lateinit var talkAdapter: TalkAdapter

    companion object {
        const val TIMEPICKER_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentTalkBinding = FragmentTalkBinding.inflate(layoutInflater, container, false).apply {
            viewModel = talkViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        setHasOptionsMenu(true)
        return fragmentTalkBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        resetMainViewModel()
        setupRecyclerView()
        setupTimePicker()
        talkViewModel.start(args.userId)
        setTimer()
        setRepeat()
        onMessageChanged()
        onTimerChanged()
        onStartTimeChanged()
        onIntervalChanged()
        onMessageAdded()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TIMEPICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val time = data?.getStringExtra("selectedTime")
            talkViewModel.time.value = time
        }
    }


    private fun setupRecyclerView() {
        val viewModel = fragmentTalkBinding.viewModel
        if (viewModel != null) {
            talkAdapter = TalkAdapter(viewModel, this.context)
            fragmentTalkBinding.talkRecyclerView.adapter = talkAdapter
        }
        fragmentTalkBinding.talkRecyclerView.scrollToPosition(talkAdapter.itemCount - 1)
    }

    private fun onMessageChanged() {
        talkViewModel.message.observe(this, Observer {
            talkViewModel.onMessageChanged()
        })
    }

    private fun resetMainViewModel() {
        val mainActivity = this.activity as MainActivity
        mainActivity.viewModel.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_talk, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.talk_edit -> {
                navigateToAddEditFragment()
                return true
            }
            R.id.talk_delete -> {
                showDialog()
                return true
            }
        }
        return false
    }

    private fun setTimer() {
        talkViewModel.setTimerSlackEvent.observe(this, EventObserver {addressOrUserName :String->
            if (addressOrUserName.isNotEmpty()) {
                var serviceCode = 0
                talkViewModel.selectedServices.forEach {
                    serviceCode = serviceCode or it
                }
                (this.activity as MainActivity).setTimer(
                    addressOrUserName, talkViewModel.message.value!!,
                    talkViewModel.time.value!!,
                    serviceCode
                )
            }
        })
        talkViewModel.setTimerMailEvent.observe(this, EventObserver {addressOrUserName :String->
            if (addressOrUserName.isNotEmpty()) {
                var serviceCode = 0
                talkViewModel.selectedServices.forEach {
                    serviceCode = serviceCode or it
                }
                (this.activity as MainActivity).setTimer(
                    addressOrUserName, talkViewModel.message.value!!,
                    talkViewModel.time.value!!,
                    serviceCode
                )
            }
        })
    }

    private fun setRepeat() {
        talkViewModel.setRepeatSlackEvent.observe(this, EventObserver {addressOrUserName :String->
            if (addressOrUserName.isNotEmpty()) {
                var serviceCode = 0
                talkViewModel.selectedServices.forEach {
                    serviceCode = serviceCode or it
                }
                (this.activity as MainActivity).setRepeatTimer(
                    addressOrUserName, talkViewModel.message.value!!,
                    System.currentTimeMillis() + talkViewModel.startTime.value!!.toLong() * 1000,
                    talkViewModel.interval.value!!.toLong() * 1000,
                    serviceCode
                )
            }
        })

        talkViewModel.setRepeatMailEvent.observe(this, EventObserver { addressOrUserName :String->
            if (addressOrUserName.isNotEmpty()) {
                var serviceCode = 0
                talkViewModel.selectedServices.forEach {
                    serviceCode = serviceCode or it
                }
                (this.activity as MainActivity).setRepeatTimer(
                    addressOrUserName, talkViewModel.message.value!!,
                    System.currentTimeMillis() + talkViewModel.startTime.value!!.toLong() * 1000,
                    talkViewModel.interval.value!!.toLong() * 1000,
                    serviceCode
                )
            }
        })
    }

    private fun onMessageAdded() {
        talkViewModel.messages.observe(this, Observer {
            if (it.size > 0) {
                fragmentTalkBinding.talkRecyclerView.smoothScrollToPosition(it.size - 1)
            }
        })
    }

    private fun showDialog() {

        val alertDialog = AlertDialog.Builder(this.context)
            .setTitle("Caution!")
            .setMessage("Are you sure you want to delete?")
            .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    deleteUser()
                }
            })
            .setNegativeButton("Cancel", null)
        alertDialog.show()

    }

    private fun deleteUser() {
        talkViewModel.delete(args.userId)
        navigateToUserListFragment()
    }

    private fun setupTimePicker() {
        fragmentTalkBinding.talkTimeEditText.setOnFocusChangeListener { view, isFocused ->
            if (isFocused) {
                val newFragment = TimePick()
                newFragment.setTargetFragment(this, TIMEPICKER_REQUEST_CODE)
                newFragment.show(requireFragmentManager(), "timePicker")
            }
        }
    }

    private fun onTimerChanged() {
        talkViewModel.time.observe(this, Observer {
            talkViewModel.onTimeChanged()
        })
    }

    private fun onStartTimeChanged() {
        talkViewModel.startTime.observe(this, Observer {
            talkViewModel.onStartTimeChanged()
        })
    }

    private fun onIntervalChanged() {
        talkViewModel.interval.observe(this, Observer {
            talkViewModel.onIntervalChanged()
        })
    }

    private fun navigateToAddEditFragment() {
        val action = TalkFragmentDirections
            .actionTalkFragmentToAddEditUserFragment(args.userId, args.title, -1, null)
        findNavController().navigate(action)
    }

    private fun navigateToUserListFragment() {
        val action = TalkFragmentDirections
            .actionTalkFragmentToUserListFragment()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        (this.activity as MainActivity).stopAlarmService()
    }
}