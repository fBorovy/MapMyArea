package com.fborowy.mapmyarea.domain.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fborowy.mapmyarea.R
import com.fborowy.mapmyarea.data.Repository
import com.fborowy.mapmyarea.data.classes.MapData
import com.fborowy.mapmyarea.data.classes.UserData
import com.fborowy.mapmyarea.domain.states.AddingMapState
import com.fborowy.mapmyarea.domain.states.SignInResult
import com.fborowy.mapmyarea.domain.states.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
    private val repository: Repository = Repository()
): ViewModel() {

    private val _signInState = MutableStateFlow(SignInState())
    val signInState = _signInState.asStateFlow()
    private var _userData = MutableStateFlow(UserData(userId = null, username = null, savedMaps = emptyList()))
    val userData: StateFlow<UserData> = _userData
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
    private var displayedMap = MapData(null, null, "", null, null, null)
    private val maxMapNameLength = 50
    private val _addingMapState = MutableStateFlow(AddingMapState())
    val addingMapState: StateFlow<AddingMapState> = _addingMapState
    private val _mapDeletionIssue = MutableStateFlow(false)
    val mapDeletionIssue: StateFlow<Boolean> = _mapDeletionIssue
    private var _collectingUserInfoError = MutableStateFlow("")
    val collectingUserInfoError: StateFlow<String?> = _collectingUserInfoError

    fun onSignIn(result: SignInResult) {
        _signInState.update { it.copy(
            signInSuccessful = result.data != null,
            signInError = result.errorMessage
        ) }
    }
    fun resetSignInState() {
        _signInState.update { SignInState() }
    }

    fun getSignedUserInfo() {
        try {
            viewModelScope.launch {
                _userData.update {
                    repository.getSignedUserInfo()?:UserData()
                }
            }
        } catch (e: Exception) {
            _collectingUserInfoError.value = e.message?: ""
        }
    }

    fun switchDisplayedMap(map: MapData) {
        displayedMap = map
    }
    fun getDisplayedMapInfo(): MapData {
        return displayedMap
    }

    fun updateSearchMapText(text: String) {
        if (text.length < maxMapNameLength)
            _searchText.update { text }
        else _searchText.update { text.take(maxMapNameLength) }
    }

    fun addMapToUserSavedMapsFromSearch() {
        if (
            _searchText.value.length < MIN_MAP_NAME_LENGTH ||
            FORBIDDEN_MAP_NAME_CHARACTERS.any { _searchText.value.contains(it) } ||
            _searchText.value.startsWith(".") || _searchText.value.endsWith(".") ||
            _searchText.value.contains("..")
        ) {
            _addingMapState.update {
                AddingMapState(false, R.string.map_not_found)
            }
            return
        }
        for (map in _userData.value.savedMaps!!) {
            if (map.mapName == _searchText.value) {
                _addingMapState.update {
                    AddingMapState(false, R.string.you_have_added_this_map_already)
                }
                return
            }
        }
        viewModelScope.launch {
            val addingMapState = repository.saveMapForUser(searchText.value)
            if (addingMapState.addingSuccessful) {
                    _userData.update { it.copy(
                        savedMaps = it.savedMaps!!.plus(addingMapState.addedMap!!)
                    )}
                _searchText.value = ""
            } else {
                _addingMapState.update {
                    AddingMapState(false, addingMapState.errorCode)
                }
            }
        }
    }

    fun resetAddingMapState() {
        _addingMapState.update {
            AddingMapState(false, null)
        }
    }
    fun clearSearchText() {
        _searchText.update { "" }
    }

    fun deleteMap(mapName: String) {
        try {
            repository.deleteOwnMap(mapName)
            for (map in _userData.value.savedMaps!!) {
                if (map.owner == _userData.value.userId) {
                    _userData.update { it.copy(
                        savedMaps = it.savedMaps!!.minus(map)
                    ) }
                }
            }
        } catch (e: Exception) {
            _mapDeletionIssue.update { true }
        }
    }

    fun removeMapFromUserSaved(mapName: String) {
        viewModelScope.launch {
            try {
                repository.removeMapFromUserMaps("/maps/${mapName}")
                for (map in _userData.value.savedMaps!!) {
                    if (map.mapName == mapName) {
                        _userData.update { it.copy(
                            savedMaps = it.savedMaps!!.minus(map)
                        ) }
                    }
                }
            } catch (e: Exception) {
                _mapDeletionIssue.update { true }
            }
        }
    }

    fun resetMapDeletionStatus() {
        _mapDeletionIssue.update { false }
    }

    fun checkIfLogged(): Boolean {
        return repository.checkIfLogged()
    }

    fun addUserDataToFirestoreIfItsNotThere() {
        repository.addUserDataToFirestoreIfItsNotThere()
    }

    fun addMapToUserSavedMaps(newMap: MapData) {
        newMap.owner = _userData.value.userId
        for (map in _userData.value.savedMaps!!) {
            if (map.mapName == newMap.mapName) {
                _userData.update { it.copy(
                    savedMaps = it.savedMaps!!.minus(map)
                )}
            }
        }
        _userData.update { it.copy(
            savedMaps = it.savedMaps!!.plus(newMap)
        ) }
    }
}