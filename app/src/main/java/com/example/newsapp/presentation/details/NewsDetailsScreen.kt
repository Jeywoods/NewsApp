
package com.example.newsapp.presentation.details

import android.content.Intent
import android.graphics.Color as AndroidColor
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.newsapp.presentation.common.AppTopBar
import com.example.newsapp.presentation.common.formatNewsDate
import java.util.Locale

@Composable
fun NewsDetailsScreen(
    viewModel: NewsDetailsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val fullDescription = uiState.fullDescription
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = uiState.title,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 16.dp,
                    bottom = 32.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = formatNewsDate(uiState.date),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = uiState.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = uiState.shortDescription,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outline
            )

            when {
                fullDescription != null -> {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = false

                                webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(
                                        view: WebView,
                                        request: WebResourceRequest
                                    ): Boolean {
                                        openExternalLink(
                                            view,
                                            request.url
                                        )
                                        return true
                                    }
                                }

                                setBackgroundColor(AndroidColor.TRANSPARENT)
                            }
                        },
                        update = { webView ->
                            val html = """
                                <!DOCTYPE html>
                                <html>
                                <head>
                                    <meta name="viewport"
                                        content="width=device-width, initial-scale=1.0">
                                    <style>
                                        body {
                                            font-family: sans-serif;
                                            font-size: 16px;
                                            line-height: 1.6;
                                            color: #${"%06X".format(
                                Locale.ROOT,
                                0xFFFFFF and textColor
                            )};
                                            margin: 0;
                                            padding: 0;
                                        }

                                        p {
                                            margin-top: 0;
                                            margin-bottom: 16px;
                                        }

                                        img {
                                            max-width: 100%;
                                            height: auto;
                                        }

                                        a {
                                            color: #34465E;
                                        }
                                    </style>
                                </head>
                                <body>
                                    $fullDescription
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
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = uiState.error
                                ?: "Не удалось загрузить полный текст",
                            color = MaterialTheme.colorScheme.error
                        )

                        TextButton(
                            onClick = viewModel::loadDetails
                        ) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }
}

private fun openExternalLink(
    view: WebView,
    uri: Uri
) {
    val intent = Intent(Intent.ACTION_VIEW, uri)

    try {
        view.context.startActivity(intent)
    } catch (_: Exception) {
    }
}
