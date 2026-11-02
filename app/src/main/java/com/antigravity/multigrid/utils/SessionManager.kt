package com.antigravity.multigrid.utils

import android.content.Context
import android.content.SharedPreferences
import com.antigravity.multigrid.model.GridSlotData

class SessionManager(context: Context) {
    companion object {
        private const val PREF_NAME = "MultiGridWorkspaceSession"
        private const val KEY_LAYOUT_MODE = "saved_layout_mode"
        private const val KEY_SLOT_URL = "slot_url_"
        private const val KEY_SLOT_TITLE = "slot_title_"
        private const val KEY_SLOT_MUTED = "slot_muted_"

        const val LAYOUT_1 = 1
        const val LAYOUT_2_V = 2
        const val LAYOUT_2_H = 3
        const val LAYOUT_3 = 4
        const val LAYOUT_4 = 5

        val DEFAULT_URLS = listOf(
            "https://m.youtube.com",
            "https://m.facebook.com",
            "https://www.tiktok.com",
            "https://www.google.com"
        )
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Lưu chế độ chia màn hình hiện tại
     */
    fun saveLayoutMode(mode: Int) {
        prefs.edit().putInt(KEY_LAYOUT_MODE, mode).apply()
    }

    /**
     * Lấy chế độ chia màn hình lần cuối sử dụng (mặc định là 4 ngăn nếu mới cài)
     */
    fun getSavedLayoutMode(): Int {
        return prefs.getInt(KEY_LAYOUT_MODE, LAYOUT_4)
    }

    /**
     * Lưu trạng thái của một ngăn cụ thể
     */
    fun saveSlotData(slotIndex: Int, url: String, title: String, isMuted: Boolean = false) {
        prefs.edit().apply {
            putString("$KEY_SLOT_URL$slotIndex", url)
            putString("$KEY_SLOT_TITLE$slotIndex", title)
            putBoolean("$KEY_SLOT_MUTED$slotIndex", isMuted)
            apply()
        }
    }

    /**
     * Tải dữ liệu của một ngăn đã lưu lần trước
     */
    fun getSlotData(slotIndex: Int): GridSlotData {
        val defaultUrl = if (slotIndex in DEFAULT_URLS.indices) DEFAULT_URLS[slotIndex] else "https://www.google.com"
        val url = prefs.getString("$KEY_SLOT_URL$slotIndex", defaultUrl) ?: defaultUrl
        val title = prefs.getString("$KEY_SLOT_TITLE$slotIndex", "Ngăn ${slotIndex + 1}") ?: "Ngăn ${slotIndex + 1}"
        val isMuted = prefs.getBoolean("$KEY_SLOT_MUTED$slotIndex", false)

        return GridSlotData(
            slotIndex = slotIndex,
            title = title,
            url = url,
            isMuted = isMuted
        )
    }

    /**
     * Lưu toàn bộ danh sách các ngăn
     */
    fun saveAllSlots(slots: List<GridSlotData>) {
        val editor = prefs.edit()
        slots.forEach { slot ->
            editor.putString("$KEY_SLOT_URL${slot.slotIndex}", slot.url)
            editor.putString("$KEY_SLOT_TITLE${slot.slotIndex}", slot.title)
            editor.putBoolean("$KEY_SLOT_MUTED${slot.slotIndex}", slot.isMuted)
        }
        editor.apply()
    }
}
