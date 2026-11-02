package com.antigravity.multigrid.model

data class AppPreset(
    val name: String,
    val url: String,
    val iconText: String,
    val colorHex: String
) {
    companion object {
        fun getDefaultPresets(): List<AppPreset> {
            return listOf(
                AppPreset("YouTube", "https://m.youtube.com", "YT", "#FF0000"),
                AppPreset("TikTok", "https://www.tiktok.com", "TT", "#000000"),
                AppPreset("Facebook", "https://m.facebook.com", "FB", "#1877F2"),
                AppPreset("Messenger", "https://www.messenger.com", "MS", "#0084FF"),
                AppPreset("Instagram", "https://www.instagram.com", "IG", "#E1306C"),
                AppPreset("X (Twitter)", "https://mobile.twitter.com", "X", "#1DA1F2"),
                AppPreset("ChatGPT", "https://chatgpt.com", "AI", "#10A37F"),
                AppPreset("TradingView", "https://www.tradingview.com/chart/", "TV", "#2962FF"),
                AppPreset("Google", "https://www.google.com", "GG", "#4285F4"),
                AppPreset("Twitch", "https://m.twitch.tv", "TW", "#9146FF"),
                AppPreset("Shopee", "https://shopee.vn", "SP", "#EE4D2D"),
                AppPreset("Zing MP3", "https://zingmp3.vn", "MP3", "#8A3FFC")
            )
        }
    }
}
