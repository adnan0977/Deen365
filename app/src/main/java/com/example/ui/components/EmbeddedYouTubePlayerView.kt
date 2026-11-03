package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun EmbeddedYouTubePlayerView(
    videoId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Optimized HTML with IFrame API and error 150/152 detection for direct native fallback
    val embedHtml = remember(videoId) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * { box-sizing: border-box; }
                body, html { 
                    margin: 0; 
                    padding: 0; 
                    width: 100%; 
                    height: 100%; 
                    background-color: #000000; 
                    overflow: hidden; 
                    display: flex; 
                    align-items: center; 
                    justify-content: center; 
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                }
                .iframe-container { 
                    width: 100%; 
                    height: 100%; 
                    position: relative; 
                }
                iframe, #player { 
                    width: 100%; 
                    height: 100%; 
                    border: 0; 
                }
                #error-overlay {
                    display: none;
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                    background: linear-gradient(135deg, #0A1E14 0%, #050B08 100%);
                    color: #FFFFFF;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    text-align: center;
                    padding: 20px;
                    z-index: 100;
                }
                .error-title {
                    font-size: 15px;
                    font-weight: 700;
                    color: #D4AF37;
                    margin-bottom: 6px;
                }
                .error-desc {
                    font-size: 12px;
                    color: #C2D5CC;
                    margin-bottom: 14px;
                    line-height: 1.4;
                }
                .yt-btn {
                    background: #D4AF37;
                    color: #000000;
                    padding: 10px 18px;
                    border-radius: 8px;
                    font-size: 13px;
                    font-weight: 700;
                    text-decoration: none;
                    display: inline-block;
                }
            </style>
        </head>
        <body>
            <div class="iframe-container">
                <div id="player"></div>
                <div id="error-overlay">
                    <div class="error-title">Restricted by Video Creator (Error 152)</div>
                    <div class="error-desc">This Islamic lecture has third-party embedding disabled by its publisher.<br>Tap below to watch smoothly in the YouTube app.</div>
                    <a class="yt-btn" href="https://www.youtube.com/watch?v=$videoId">▶ Watch on YouTube</a>
                </div>
            </div>

            <script>
                var tag = document.createElement('script');
                tag.src = "https://www.youtube.com/iframe_api";
                var firstScriptTag = document.getElementsByTagName('script')[0];
                firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                var player;
                function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                        height: '100%',
                        width: '100%',
                        videoId: '$videoId',
                        playerVars: {
                            'playsinline': 1,
                            'autoplay': 1,
                            'rel': 0,
                            'modestbranding': 1,
                            'enablejsapi': 1,
                            'origin': 'https://www.youtube.com'
                        },
                        events: {
                            'onError': function(event) {
                                // Error codes: 2 (invalid), 5 (HTML5), 100 (not found), 101/150/152 (restricted embed)
                                document.getElementById('player').style.display = 'none';
                                document.getElementById('error-overlay').style.display = 'flex';
                            }
                        }
                    });
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Black)
                .testTag("embedded_youtube_player_$videoId")
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setLayerType(View.LAYER_TYPE_HARDWARE, null)

                        // Enable cookies and third-party cookies for YouTube auth & policy verification
                        try {
                            val cookieManager = CookieManager.getInstance()
                            cookieManager.setAcceptCookie(true)
                            cookieManager.setAcceptThirdPartyCookies(this, true)
                        } catch (_: Exception) {}

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            mediaPlaybackRequiresUserGesture = false
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                        }
                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                if (url != null && (url.contains("youtube.com/watch") || url.startsWith("vnd.youtube"))) {
                                    launchYouTubeApp(ctx, videoId)
                                    return true
                                }
                                return false
                            }
                        }
                        tag = videoId
                        loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
                    }
                },
                update = { webView ->
                    if (webView.tag != videoId) {
                        webView.tag = videoId
                        webView.loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
                    }
                }
            )

            // Top-right button for direct YouTube app launch
            FilledTonalIconButton(
                onClick = {
                    launchYouTubeApp(context, videoId)
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.65f),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .testTag("open_in_youtube_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Open in YouTube App",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Quick Fallback Action Bar below video player
        Surface(
            shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.SmartDisplay, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "IFrame Player with Native Fallback",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = { launchYouTubeApp(context, videoId) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Watch in YouTube App", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun launchYouTubeApp(context: Context, videoId: String) {
    val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
    try {
        context.startActivity(appIntent)
    } catch (e: Exception) {
        try {
            context.startActivity(webIntent)
        } catch (_: Exception) {}
    }
}
