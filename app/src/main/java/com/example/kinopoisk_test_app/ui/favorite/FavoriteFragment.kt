package com.example.kinopoisk_test_app.ui.favorite

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kinopoisk_test_app.R
import com.example.kinopoisk_test_app.databinding.FragmentFavoriteBinding
import com.example.kinopoisk_test_app.domian.models.Movie
import com.example.kinopoisk_test_app.presentation.adapters.MoviesAdapter
import com.example.kinopoisk_test_app.presentation.models.FavoriteScreenState
import com.example.kinopoisk_test_app.presentation.viewModels.FavoriteViewModel
import com.example.kinopoisk_test_app.ui.detail.DetailFragment
import com.example.kinopoisk_test_app.util.MOVIE_ID
import com.example.kinopoisk_test_app.util.debounce
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FavoriteViewModel>()
    private var movieClickDebounce: ((Movie) -> Unit)? = null
    private val moviesAdapter = MoviesAdapter(
        { movie ->
            movieClickDebounce?.let { movieClickDebounce -> movieClickDebounce(movie) }
        },
        { showDialog(it) }
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.screenState.observe(viewLifecycleOwner) {
            updateScreen(it)
        }
        viewModel.getMovies()
        binding.rvFilms.adapter = moviesAdapter
        setClickDebounce()
    }

    private fun updateScreen(state: FavoriteScreenState) {
        when (state) {
            is FavoriteScreenState.Loading -> showLoading()
            is FavoriteScreenState.Content -> showContent(state.movies)
            is FavoriteScreenState.Empty -> showEmptyState(state.message)
        }
    }

    private fun showEmptyState(message: Int) {
        with(binding) {
            rvFilms.isVisible = false
            pbLoading.isVisible = false
            tvEmptyPlaceholder.isVisible = true
            tvEmptyPlaceholder.text = getString(message)
        }
    }

    private fun showContent(movies: List<Movie>) {
        with(binding) {
            rvFilms.isVisible = true
            pbLoading.isVisible = false
            tvEmptyPlaceholder.isVisible = false
            moviesAdapter.clearData()
            moviesAdapter.addMovies(movies)
        }
    }

    private fun showLoading() {
        with(binding) {
            rvFilms.isVisible = false
            pbLoading.isVisible = true
            tvEmptyPlaceholder.isVisible = false
        }
    }

    private fun setClickDebounce() {
        movieClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { movieItem ->
            setOnClickListener(movieItem)
        }
    }

    private fun setOnClickListener(movieItem: Movie) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            activity?.supportFragmentManager?.commit {
                activity?.findViewById<View>(R.id.fc_detail_container)?.let {
                    replace(it.id, DetailFragment.newInstance(movieItem.id))
                }
            }
        } else {
            findNavController().navigate(
                R.id.action_favoriteFragment_to_detailFragment,
                bundleOf(MOVIE_ID to movieItem.id)
            )
        }
    }

    private fun showDialog(movie: Movie): Boolean {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setMessage(getString(R.string.delete_from_fav_dialog))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.dialog_no)) { dialog, which ->
            }
            .setPositiveButton(getString(R.string.dialog_yes)) { dialog, which ->
                viewModel.deleteMovieFromFavorites(movie)
            }.show()
        return true
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 200L
    }
}