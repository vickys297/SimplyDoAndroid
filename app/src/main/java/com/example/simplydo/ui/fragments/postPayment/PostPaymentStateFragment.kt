package com.example.simplydo.ui.fragments.postPayment

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.simplydo.R
import com.example.simplydo.databinding.PostPaymentStateFragmentBinding

class PostPaymentStateFragment : Fragment(R.layout.post_payment_state_fragment) {

    private lateinit var viewModel: PostPaymentStateViewModel
    private lateinit var binding: PostPaymentStateFragmentBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PostPaymentStateFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this)[PostPaymentStateViewModel::class.java]

        binding.lottieAnimation.addAnimatorListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                findNavController().navigateUp()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

        })
    }

}