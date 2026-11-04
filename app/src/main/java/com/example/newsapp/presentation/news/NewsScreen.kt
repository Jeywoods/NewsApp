
package com.example.newsapp.presentation.news

import androidx.compose.foundation.clickable
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun NewsScreen(
    categoryName: String,
    viewModel: NewsViewModel,
    onNewsClick: (News) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState, uiState.news.size, uiState.hasMorePages) {
        snapshotFlow {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItems = listState.layoutInfo.totalItemsCount

            lastVisibleItem != null && lastVisibleItem >= totalItems - 3
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && uiState.hasMorePages) {
                viewModel.loadNews()
            }
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
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
                        modifier = Modifier.align(Alignment.Center)
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

                        TextButton(onClick = viewModel::loadNews) {
                            Text("Повторить")
                        }
                    }
                }

                uiState.news.isEmpty() -> {
                    Text(
                        text = "В этой категории пока нет новостей",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(
                            items = uiState.news,
                            key = { it.id }
                        ) { news ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNewsClick(news) }
                                    .padding(
                                        horizontal = 20.dp,
                                        vertical = 16.dp
                                    ),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = news.title,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = formatNewsDate(news.date),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = news.shortDescription,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            HorizontalDivider()
                        }

                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        if (uiState.error != null && uiState.news.isNotEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = uiState.error ?: "Ошибка загрузки",
                                        color = MaterialTheme.colorScheme.error
                                    )

                                    TextButton(onClick = viewModel::loadNews) {
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

private fun formatNewsDate(value: String): String {
    return try {
        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.getDefault()
        ).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val outputFormat = SimpleDateFormat(
            "dd.MM.yyyy HH:mm",
            Locale.getDefault()
        ).apply {
            timeZone = TimeZone.getDefault()
        }

        val date = inputFormat.parse(value)
        if (date != null) outputFormat.format(date) else value
    } catch (exception: Exception) {
        value
    }
}
