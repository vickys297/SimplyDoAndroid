package com.example.simplydo.ui.fragments.accounts.plans

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.PaidPlansAdapter
import com.example.simplydo.databinding.PlansFragmentBinding
import com.example.simplydo.model.PaidPlanModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppPreference
import com.example.simplydo.utlis.PaidPlain
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlansFragment : Fragment(R.layout.plans_fragment) {


    private lateinit var binding: PlansFragmentBinding
    private lateinit var viewModel: PlansViewModel

    private var currentSelectedPlanId: Int = 0
    private lateinit var paidPlanAdapter: PaidPlansAdapter

    private val paidPlanCallback = object : PaidPlain.Callback {
        override fun onSelectPlan(id: Int) {
            currentSelectedPlanId = id
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PlansFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this)[PlansViewModel::class.java]

        val accentText = "<font color='#6200EE'>Plan</font>"

        val workspaceTextTitle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(String.format("Choose your %s", accentText), Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(String.format("Choose your %s", accentText))
        }

        binding.textViewHeader.text = workspaceTextTitle
        paidPlanAdapter = PaidPlansAdapter(currentSelectedPlanId, callback = paidPlanCallback)
        binding.recyclerViewPlans.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = paidPlanAdapter
        }

        binding.buttonContinueToPayment.setOnClickListener {
            findNavController().navigate(R.id.action_plansFragment_to_postPaymentStateFragment)
        }

        loadPaidPlan()
    }

    private fun loadPaidPlan() {
        val plans = Gson().fromJson<ArrayList<PaidPlanModel>>(
            AppPreference.getPreferences(
                AppConstant.Preferences.PAID_PLANS,
                requireContext()
            ),
            object : TypeToken<ArrayList<PaidPlanModel>>() {}.type
        )


        paidPlanAdapter.update(plans)
    }

}