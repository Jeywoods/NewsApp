
package com.example.newsapp.presentation.details

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp

@Composable
fun NewsDetailsScreen(
    viewModel: NewsDetailsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                TextButton(onClick = onBackClick) {
                    Text("← Назад")
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = uiState.title,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = uiState.date,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = uiState.shortDescription,
                style = MaterialTheme.typography.bodyLarge
            )

            when {
                uiState.fullDescription != null -> {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = false

                                webViewClient = object : WebViewClient() {

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView,
                                        request: WebResourceRequest
                                    ): Boolean {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            request.url
                                        )

                                        try {
                                            view.context.startActivity(intent)
                                        } catch (exception: Exception) {
                                            // Подходящее приложение для ссылки не найдено.
                                        }

                                        return true
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView,
                                        url: String
                                    ): Boolean {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(url)
                                        )

                                        try {
                                            view.context.startActivity(intent)
                                        } catch (exception: Exception) {
                                            // Подходящее приложение для ссылки не найдено.
                                        }

                                        return true
                                    }
                                }

                                setBackgroundColor(Color.TRANSPARENT)
                            }
                        },
                        update = { webView ->
                            val html = """
                                <!DOCTYPE html>
                                <html>
                                <head>
                                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                    <style>
                                        body {
                                            font-family: sans-serif;
                                            font-size: 16px;
                                            line-height: 1.5;
                                            color: #333333;
                                            margin: 0;
                                            padding: 0;
                                        }
                                        img {
                                            max-width: 100%;
                                            height: auto;
                                        }
                                    </style>
                                </head>
                                <body>
                                    ${uiState.fullDescription}
                                </body>
                                </html>
                            """.trimIndent()

                            webView.loadDataWithBaseURL(
                                null,
                                html,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    )
                }

                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error ?: "Не удалось загрузить полный текст",
                            color = MaterialTheme.colorScheme.error
                        )

                        TextButton(onClick = viewModel::loadDetails) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }
}
