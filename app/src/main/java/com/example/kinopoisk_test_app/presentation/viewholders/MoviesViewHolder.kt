package com.example.kinopoisk_test_app.presentation.viewholders

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.kinopoisk_test_app.R
import com.example.kinopoisk_test_app.databinding.MovieListItemBinding
import com.example.kinopoisk_test_app.domian.models.Movie

class MoviesViewHolder(private val binding: MovieListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie) {
        with(binding) {
            Glide.with(itemView)
                .load(movie.coverSmall)
                .transform(FitCenter(), RoundedCorners(CORNER_RADIUS))
                .placeholder(R.drawable.ic_cover_placeholder)
                .into(ivCover)

            tvMovieTitle.text = movie.name
            tvMovieGenreYear.text = "${movie.genres} (${movie.year})"
            ivFavoriteStar.isVisible = movie.isFavorite
        }
    }

    fun updateFavoriteState(isFavorite: Boolean) {
        binding.ivFavoriteStar.isVisible = isFavorite
    }

    companion object {
        private const val CORNER_RADIUS = 5
    }
}