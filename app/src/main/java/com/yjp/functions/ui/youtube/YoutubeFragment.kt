package com.yjp.functions.ui.youtube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yjp.functions.R
import com.yjp.functions.databinding.FragmentYoutubeBinding
import com.yjp.functions.ui.theme.FunctionsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class YoutubeFragment : Fragment() {

    private val viewModel: YoutubeViewModel by viewModels()

    private var _binding: FragmentYoutubeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_youtube, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                FunctionsTheme {
                    YoutubeScreen(viewModel = viewModel)
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
