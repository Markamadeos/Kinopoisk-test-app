package com.example.kinopoisk_test_app.presentation.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kinopoisk_test_app.R
import com.example.kinopoisk_test_app.domian.api.FavoriteInteractor
import com.example.kinopoisk_test_app.domian.api.SearchInteractor
import com.example.kinopoisk_test_app.domian.models.Movie
import com.example.kinopoisk_test_app.domian.models.SearchResultData
import com.example.kinopoisk_test_app.presentation.models.PopularScreenState
import com.example.kinopoisk_test_app.presentation.models.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PopularViewModel(
    private val searchInteractor: SearchInteractor,
    private val favoriteInteractor: FavoriteInteractor
) : ViewModel() {
    private val _screenState: MutableLiveData<PopularScreenState> = MutableLiveData()
    private val _favoriteNotificationSate = SingleLiveEvent<Int>()
    val screenState: LiveData<PopularScreenState> = _screenState
    val favoriteNotificationState: LiveData<Int> = _favoriteNotificationSate
    private var searchJob: Job? = null
    private var currentQuery = EMPTY_QUERY
    private var popularMovies: List<Movie>? = null

    private fun getPopularMovies() {
        _screenState.postValue(PopularScreenState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            searchInteractor.getPopularMovies().collect { result ->
                processingResult(result)
            }
        }
    }

    fun getMovies() {
        if (currentQuery.isEmpty()) {
            getPopularMovies()
        } else {
            searchMovies(currentQuery)
        }
    }

    private fun searchMovies(query: String) {
        _screenState.postValue(PopularScreenState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            searchInteractor.searchMovies(query).collect { result ->
                processingResult(result)
            }
        }
    }

    fun debounceSearch(query: String) {
        if (query != currentQuery) {
            currentQuery = query
            searchJob?.cancel()
            if (currentQuery.length > ONE_LETTER) {
                searchJob = viewModelScope.launch {
                    delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
                    searchMovies(currentQuery)
                }
            }
        }
    }

    private fun processingResult(result: SearchResultData<List<Movie>>) {
        when (result) {
            is SearchResultData.NoInternet -> {
                _screenState.postValue(PopularScreenState.InternetError(result.message))
            }

            is SearchResultData.ServerError -> {
                _screenState.postValue(PopularScreenState.ServerError(result.message))
            }

            is SearchResultData.Empty -> {
                _screenState.postValue(PopularScreenState.Empty(result.message))
            }

            is SearchResultData.Data -> {
                if (result.value != null) {
                    popularMovies = result.value
                    _screenState.postValue(PopularScreenState.Content(popularMovies!!))
                }
            }
        }
    }

    fun saveMovieToDb(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            if (favoriteInteractor.isMovieInFavorites(movie.id)) {
                _favoriteNotificationSate.postValue(R.string.already_in_favorites)
            } else {
                var fullMovieInfo: Movie? = null
                try {
                    fullMovieInfo =
                        (searchInteractor.getMovieById(movie.id)
                            .first() as SearchResultData.Data).value!!
                } catch (_: Throwable) {
                }
                if (fullMovieInfo == null) {
                    _favoriteNotificationSate.postValue(R.string.add_to_favorite_error)
                } else {
                    favoriteInteractor.saveMovieToDb(fullMovieInfo)
                    _favoriteNotificationSate.postValue(R.string.added_to_favorites)
                    popularMovies = popularMovies?.let { ArrayList(it) }
                    val indexForUpdate = popularMovies!!.indexOfFirst { it.id == movie.id }
                    (popularMovies as ArrayList<Movie>)[indexForUpdate] =
                        popularMovies!![indexForUpdate].copy(isFavorite = true)
                    processingResult(SearchResultData.Data(popularMovies))
                }
            }
        }
    }

    companion object {
        private const val EMPTY_QUERY = ""
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private const val ONE_LETTER = 1
    }
}