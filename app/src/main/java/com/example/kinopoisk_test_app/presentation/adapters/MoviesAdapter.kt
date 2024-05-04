package com.example.kinopoisk_test_app.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.kinopoisk_test_app.databinding.MovieListItemBinding
import com.example.kinopoisk_test_app.domian.models.Movie
import com.example.kinopoisk_test_app.presentation.viewholders.MoviesViewHolder

class MoviesAdapter(
    private val onClick: (Movie) -> Unit,
    val onLongClick: (Movie) -> Boolean
) : ListAdapter<Movie, MoviesViewHolder>(AsyncDifferConfig.Builder(MovieDiffUtil()).build()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val binding =
            MovieListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoviesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener { onClick.invoke(item) }
        holder.itemView.setOnLongClickListener { onLongClick.invoke(item) }
    }

    override fun onBindViewHolder(
        holder: MoviesViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val item = getItem(position)
            for (payload in payloads) {
                if (payload == "favorite") {
                    holder.updateFavoriteState(item.isFavorite)
                }
            }
        }
    }

    class MovieDiffUtil : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Movie, newItem: Movie): Any? {
            if (oldItem.isFavorite != newItem.isFavorite) {
                return "favorite"
            }

            return null
        }
    }
}
