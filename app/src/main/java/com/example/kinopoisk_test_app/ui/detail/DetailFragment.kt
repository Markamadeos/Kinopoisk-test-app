package com.example.kinopoisk_test_app.ui.detail

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.kinopoisk_test_app.R
import com.example.kinopoisk_test_app.databinding.FragmentDetailBinding
import com.example.kinopoisk_test_app.domian.models.Movie
import com.example.kinopoisk_test_app.presentation.models.DetailScreenState
import com.example.kinopoisk_test_app.presentation.viewModels.DetailsViewModel
import com.example.kinopoisk_test_app.util.MOVIE_ID
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.Continuation

class DetailFragment : Fragment() {
    private val viewModel by viewModel<DetailsViewModel>()
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.screenState.observe(viewLifecycleOwner) {
            updateScreen(it)
        }
        bind()
        if (findNavController().previousBackStackEntry?.destination?.id == R.id.favoriteFragment) {
            viewModel.getDataFromDb(requireArguments().getString(MOVIE_ID) ?: UNDEFINED_ID)
        } else {
            viewModel.getDataFromNetwork(requireArguments().getString(MOVIE_ID) ?: UNDEFINED_ID)
        }
    }

    private fun bind() {
        with(binding) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                btnBack.isVisible = false
            } else {
                btnBack.isVisible = true
                btnBack.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun updateScreen(state: DetailScreenState) {
        when (state) {
            is DetailScreenState.Loading -> showLoading()
            is DetailScreenState.Content -> showContent(state.movie)
            is DetailScreenState.InternetError -> this.showErrorState(state.message)
            is DetailScreenState.ServerError -> this.showErrorState(state.message)
        }
    }

    private fun showErrorState(message: Int) {
        with(binding) {
            bsMovieDescription.isVisible = false
            ivCover.isVisible = false
            tvInternetError.isVisible = true
            ivInternetError.isVisible = true
            pbLoading.isVisible = false
            tvInternetError.text = getString(message)
        }
    }

    private fun showContent(movie: Movie) {
        with(binding) {
            bsMovieDescription.isVisible = true
            ivCover.isVisible = true
            tvInternetError.isVisible = false
            ivInternetError.isVisible = false
            tvMovieTitle.text = movie.name
            tvDescription.text = movie.description
            tvCountryValue.text = movie.countries
            tvGenreValue.text = movie.genres
            pbLoading.isVisible = true
            loadPicture(movie.coverSmall)
        }
    }

    private fun loadPicture(cover: String) {
        Glide.with(requireContext())
            .load(cover)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    // nothing to do
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.pbLoading.isVisible = false
                    return false
                }
            })
            .fitCenter()
            .into(binding.ivCover)
    }

    private fun showLoading() {
        with(binding) {
            bsMovieDescription.isVisible = false
            ivCover.isVisible = false
            tvInternetError.isVisible = false
            ivInternetError.isVisible = false
            pbLoading.isVisible = true
        }
    }

    companion object {
        fun newInstance(id: String) = DetailFragment().apply {
            arguments = Bundle().apply {
                putString(MOVIE_ID, id)
            }
        }

        private const val UNDEFINED_ID = ""
    }
}