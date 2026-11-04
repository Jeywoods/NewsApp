
package com.example.newsapp.presentation.news

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.newsapp.domain.model.News
import com.example.newsapp.presentation.common.AppTopBar

@Composable
fun NewsScreen(
    categoryName: String,
    viewModel: NewsViewModel,
    onNewsClick: (News) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()


    LaunchedEffect(
        listState,
        uiState.news.size,
        uiState.hasMorePages,
        uiState.isLoading,
        uiState.isLoadingMore,
        uiState.error
    ) {
        snapshotFlow {
            val lastVisibleItem =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index

            val totalItems = listState.layoutInfo.totalItemsCount

            val shouldLoadMore = lastVisibleItem != null &&
                    lastVisibleItem >= totalItems - 3

            shouldLoadMore &&
                    uiState.hasMorePages &&
                    !uiState.isLoading &&
                    !uiState.isLoadingMore &&
                    uiState.error == null
        }.collect { shouldLoadMore ->
            if (shouldLoadMore) {
                viewModel.loadNews()
            }
        }
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            AppTopBar(
                title = categoryName,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading && uiState.news.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                uiState.error != null && uiState.news.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uiState.error ?: "Произошла ошибка",
                            color = MaterialTheme.colorScheme.error
                        )

                        TextButton(
                            onClick = viewModel::loadNews
                        ) {
                            Text("Повторить")
                        }
                    }
                }

                uiState.news.isEmpty() -> {
                    Text(
                        text = "В этой категории пока нет новостей",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 24.dp
                        )
                    ) {
                        items(
                            items = uiState.news,
                            key = { it.id }
                        ) { news ->
                            NewsItem(
                                news = news,
                                onClick = { onNewsClick(news) }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        if (uiState.error != null && uiState.news.isNotEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = uiState.error
                                            ?: "Ошибка загрузки",
                                        color = MaterialTheme.colorScheme.error
                                    )

                                    TextButton(
                                        onClick = viewModel::loadNews
                                    ) {
                                        Text("Повторить")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}