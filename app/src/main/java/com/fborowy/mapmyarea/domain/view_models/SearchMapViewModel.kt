package com.fborowy.mapmyarea.domain.view_models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchMapViewModel: ViewModel() {

    private var _searchText = MutableStateFlow(String())
    val searchText: StateFlow<String> = _searchText
    private var _searchResult = MutableStateFlow(String())
    val searchResult: StateFlow<String> = _searchResult




}