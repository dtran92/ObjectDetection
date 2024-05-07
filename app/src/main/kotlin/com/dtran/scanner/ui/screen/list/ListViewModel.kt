package com.dtran.scanner.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dtran.scanner.data.Status
import com.dtran.scanner.data.network.model.Item
import com.dtran.scanner.data.network.repository.FirebaseRepository
import com.dtran.scanner.ui.model.ItemUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class ListViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    private val _itemList = MutableStateFlow(emptyList<ItemUiModel>())
    val itemList = _itemList.asStateFlow()

    private val _revealedItemList = MutableStateFlow(emptyList<ItemUiModel>())
    val revealedItemList = _revealedItemList.asStateFlow()

    private val _shouldShowEmptyIcon = MutableStateFlow(false)
    val shouldShowEmptyIcon = _shouldShowEmptyIcon.asStateFlow()

    val apiResultFlow = firebaseRepository.getList()
        .map {
            when (it) {
                is Status.Loading -> Status.Loading()
                is Status.Error -> Status.Error(error = it.error)
                is Status.Success -> Status.Success(data = it.data?.map { item ->
                    ItemUiModel(
                        id = item.id,
                        text = item.text,
                        url = item.url
                    )
                })
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000L),
            initialValue = Status.Loading()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun refreshList() = apiResultFlow.flatMapLatest {
        firebaseRepository.getList().map {
            when (it) {
                is Status.Loading -> Status.Loading()
                is Status.Error -> Status.Error(error = it.error)
                is Status.Success -> Status.Success(data = it.data?.map { item ->
                    ItemUiModel(
                        id = item.id,
                        text = item.text,
                        url = item.url
                    )
                })
            }
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000L),
            initialValue = Status.Loading()
        )

    fun updateList(newList: List<ItemUiModel>) {
        _shouldShowEmptyIcon.value = false
        _itemList.value = newList
        _shouldShowEmptyIcon.value = newList.isEmpty()
    }

    fun removeItem(item: ItemUiModel): Flow<Status<Nothing>> {
        return firebaseRepository.removeItem(item.let { elem ->
            Item(
                id = elem.id,
                url = elem.url,
                text = elem.text
            )
        })
    }

    fun updateListAfterRemove(item: ItemUiModel) {
        _itemList.update {
            it.toMutableList().apply { this.remove(item) }
        }
        _revealedItemList.update {
            it.toMutableList().apply { this.remove(item) }
        }
    }

    fun onItemExpanded(item: ItemUiModel) {
        if (_revealedItemList.value.contains(item)) return
        _revealedItemList.update {
            it.toMutableList().apply { this.add(item) }
        }
    }

    fun onItemCollapsed(item: ItemUiModel) {
        if (!_revealedItemList.value.contains(item)) return
        _revealedItemList.update {
            it.toMutableList().apply { this.remove(item) }
        }
    }

    fun resetRevealedList() {
        _revealedItemList.update {
            emptyList()
        }
    }
}