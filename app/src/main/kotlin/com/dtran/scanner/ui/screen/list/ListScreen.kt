@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.dtran.scanner.ui.screen.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dtran.scanner.R
import com.dtran.scanner.data.Status
import com.dtran.scanner.ui.widget.ProgressIndicator
import com.dtran.scanner.ui.widget.TopBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState,
    goBack: () -> Unit,
    goToWeb: (String) -> Unit
) {
    val itemList = viewModel.itemList.collectAsStateWithLifecycle()
    val revealedItemList = viewModel.revealedItemList.collectAsStateWithLifecycle()
    val progressIndicatorState = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = remember { mutableStateOf(false) }
    val showEmptyIcon = viewModel.shouldShowEmptyIcon.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshList().collectLatest {
            when (it) {
                is Status.Loading -> {
                    progressIndicatorState.value = true
                }

                is Status.Error -> {
                    progressIndicatorState.value = false
                    snackbarHostState.showSnackbar(it.error.toString())
                }

                is Status.Success -> {
                    progressIndicatorState.value = false
                    it.data?.let { items ->
                        viewModel.updateList(items)
                    }
                }
            }
        }
    }


    Scaffold(
        topBar = {
            TopBar(
                isHome = false,
                title = stringResource(id = R.string.title_list),
                onBackArrowPressed = goBack,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing.value,
            onRefresh = {
                coroutineScope.launch {
                    viewModel.refreshList().collectLatest {
                        when (it) {
                            is Status.Loading -> {
                                isRefreshing.value = true
                                progressIndicatorState.value = true
                            }

                            is Status.Error -> {
                                isRefreshing.value = false
                                progressIndicatorState.value = false
                                snackbarHostState.showSnackbar(it.error.toString())
                            }

                            is Status.Success -> {
                                isRefreshing.value = false
                                progressIndicatorState.value = false
                                it.data?.let { items ->
                                    viewModel.updateList(items)
                                }
                            }
                        }
                    }
                }
            },
            state = pullToRefreshState,
            modifier = modifier.padding(padding)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = modifier
                    .padding(vertical = 10.dp)
                    .fillMaxSize()
            ) {
                items(items = itemList.value, key = { item -> item.id }) { item ->
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        ActionsRow(onDelete = {
                            coroutineScope.launch {
                                viewModel.removeItem(item).collectLatest { res ->
                                    when (res) {
                                        is Status.Loading -> {
                                            progressIndicatorState.value = true
                                        }

                                        is Status.Error -> {
                                            progressIndicatorState.value = false
                                            snackbarHostState.showSnackbar(res.error ?: "")
                                        }

                                        is Status.Success -> {
                                            progressIndicatorState.value = false
                                            viewModel.updateListAfterRemove(item)
                                        }
                                    }
                                }
                            }
                        }, onEdit = {}, onFavorite = {}, modifier = modifier
                        )
                        //for advanced cases use DraggableCardComplex
                        DraggableCard(
                            item = item,
                            isRevealed = revealedItemList.value.contains(item),
                            onExpand = { viewModel.onItemExpanded(item) },
                            onCollapse = { viewModel.onItemCollapsed(item) },
                            onItemClicked = {
                                if (!revealedItemList.value.contains(item)) {
                                    viewModel.resetRevealedList()
                                    goToWeb(item.url)
                                } else {
                                    viewModel.onItemCollapsed(item)
                                }
                            },
                        )
                    }
//                    val dismissState = rememberSwipeToDismissState(confirmValueChange = {
//                        if (it == SwipeToDismissValue.EndToStart) {
//                            coroutineScope.launch {
//                                viewModel.removeItem(item).collectLatest { res ->
//                                    when (res) {
//                                        is Status.Loading -> {
//                                            viewModel.updateListAfterRemove(item)
//                                            progressIndicatorState.value = true
//                                        }
//
//                                        is Status.Error -> {
//                                            progressIndicatorState.value = false
//                                            snackbarHostState.showSnackbar(res.error ?: "")
//                                        }
//
//                                        is Status.Success -> {
//                                            progressIndicatorState.value = false
//                                        }
//                                    }
//                                }
//                            }
//                            true
//                        } else false
//                    })
//
//                    SwipeToDismissBox(
//                        state = dismissState,
//                        enableDismissFromEndToStart = true,
//                        enableDismissFromStartToEnd = false,
//                        backgroundContent = {
//                            val backgroundColor = animateColorAsState(
//                                targetValue = when (dismissState.targetValue) {
//                                    SwipeToDismissValue.EndToStart -> MaterialTheme.colorScheme.secondary
//                                    else -> Color.Transparent
//                                }, label = ""
//                            )
//                            Box(
//                                modifier = modifier
//                                    .fillMaxSize()
//                                    .padding(end = 10.dp)
//                                    .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
//                                    .background(color = backgroundColor.value), contentAlignment = Alignment.CenterEnd
//                            ) {
//                                Icon(
//                                    Icons.Default.Delete,
//                                    contentDescription = null,
//                                    tint = Color.White,
//                                    modifier = modifier
//                                        .padding(end = 10.dp)
//                                        .size(50.dp)
//                                )
//                            }
//                        },
//                        content = { ChildItem(item, navigator, modifier) },
//                        modifier = modifier.animateItemPlacement()
//                    )
//                }
                }
            }
        }

        when (showEmptyIcon.value) {
            false -> Unit
            true ->
//                    LottieAnimation(composition.value, iterations = LottieConstants.IterateForever)
                Icon(
                    painterResource(id = R.drawable.ic_empty_box),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
        }
    }

    ProgressIndicator(progressIndicatorState.value, modifier = modifier.fillMaxSize())
}