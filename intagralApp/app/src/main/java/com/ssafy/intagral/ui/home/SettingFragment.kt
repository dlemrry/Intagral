package com.ssafy.intagral.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ssafy.intagral.MainMenuActivity
import com.ssafy.intagral.R
import com.ssafy.intagral.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.termsOfServiceButton.setOnClickListener(LogoutClickListener())
        binding.logoutButton.setOnClickListener(LogoutClickListener())
        return binding.root
    }

    inner class LogoutClickListener : View.OnClickListener{
        override fun onClick(p0: View?) {
            when(p0?.id){
                R.id.terms_of_service_button -> {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://k7a304.p.ssafy.io/intagral_policy.html")
                    )
                    startActivity(browserIntent)
                }
                R.id.logout_button -> {
                    val activity = requireActivity() as MainMenuActivity
                    activity.logout()
                }
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SettingFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}