package com.antigravity.multigrid

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.antigravity.multigrid.databinding.ActivityMainBinding
import com.antigravity.multigrid.utils.SessionManager
import com.antigravity.multigrid.view.GridSlotView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    // Holds up to 4 persistent slot views
    private val slotViews = mutableListOf<GridSlotView>()
    private var currentLayoutMode: Int = SessionManager.LAYOUT_4
    private var maximizedSlot: GridSlotView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Khôi phục chế độ layout lần cuối sử dụng
        currentLayoutMode = sessionManager.getSavedLayoutMode()

        // Khởi tạo trước 4 slots với dữ liệu đã lưu từ lần trước
        for (i in 0 until 4) {
            val slotData = sessionManager.getSlotData(i)
            val slotView = GridSlotView(
                context = this,
                slotData = slotData,
                onStateChanged = { updatedSlot ->
                    sessionManager.saveSlotData(
                        updatedSlot.slotIndex,
                        updatedSlot.url,
                        updatedSlot.title,
                        updatedSlot.isMuted
                    )
                    notifySaved()
                },
                onToggleMaximize = { clickedSlot ->
                    toggleMaximizeSlot(clickedSlot)
                }
            )
            slotViews.add(slotView)
        }

        setupButtons()
        applyLayout(currentLayoutMode)

        // Xử lý nút Back của hệ thống
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (maximizedSlot != null) {
                    toggleMaximizeSlot(maximizedSlot!!)
                } else {
                    // Kiểm tra xem có webview nào đang back được không
                    val activeViews = getActiveSlotViews()
                    val handled = activeViews.any { it.webView.canGoBack() }
                    if (handled) {
                        activeViews.firstOrNull { it.webView.canGoBack() }?.webView?.goBack()
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })
    }

    private fun setupButtons() {
        binding.btnLayout1.setOnClickListener { switchLayout(SessionManager.LAYOUT_1) }
        binding.btnLayout2V.setOnClickListener { switchLayout(SessionManager.LAYOUT_2_V) }
        binding.btnLayout2H.setOnClickListener { switchLayout(SessionManager.LAYOUT_2_H) }
        binding.btnLayout3.setOnClickListener { switchLayout(SessionManager.LAYOUT_3) }
        binding.btnLayout4.setOnClickListener { switchLayout(SessionManager.LAYOUT_4) }

        binding.btnReloadAll.setOnClickListener {
            getActiveSlotViews().forEach { it.reload() }
        }
    }

    private fun switchLayout(newMode: Int) {
        if (maximizedSlot != null) {
            maximizedSlot = null
        }
        currentLayoutMode = newMode
        sessionManager.saveLayoutMode(newMode)
        applyLayout(newMode)
        notifySaved()
    }

    /**
     * Dựng cấu trúc giao diện dựa theo chế độ layout mà người dùng chọn
     */
    private fun applyLayout(mode: Int) {
        binding.gridContainer.removeAllViews()

        // Tháo các slotView khỏi parent cũ trước khi add lại
        slotViews.forEach {
            (it.parent as? FrameLayout)?.removeAllViews()
            (it.parent as? LinearLayout)?.removeView(it)
        }

        when (mode) {
            SessionManager.LAYOUT_1 -> {
                // 1 ngăn toàn màn hình (Dùng slot 0)
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                binding.gridContainer.addView(slotViews[0], params)
            }

            SessionManager.LAYOUT_2_V -> {
                // 2 ngăn xếp dọc (Slot 0 ở trên, Slot 1 ở dưới)
                val rootLinear = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }
                val weightParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f
                ).apply { setMargins(2, 2, 2, 2) }

                rootLinear.addView(slotViews[0], weightParams)
                rootLinear.addView(slotViews[1], LinearLayout.LayoutParams(weightParams))
                binding.gridContainer.addView(rootLinear)
            }

            SessionManager.LAYOUT_2_H -> {
                // 2 ngăn xếp ngang (Slot 0 bên trái, Slot 1 bên phải)
                val rootLinear = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }
                val weightParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f
                ).apply { setMargins(2, 2, 2, 2) }

                rootLinear.addView(slotViews[0], weightParams)
                rootLinear.addView(slotViews[1], LinearLayout.LayoutParams(weightParams))
                binding.gridContainer.addView(rootLinear)
            }

            SessionManager.LAYOUT_3 -> {
                // 3 ngăn: 1 ngăn lớn ở trên (Slot 0), 2 ngăn nhỏ ở dưới (Slot 1 & 2)
                val rootLinear = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }
                val topParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f
                ).apply { setMargins(2, 2, 2, 2) }

                rootLinear.addView(slotViews[0], topParams)

                val bottomLinear = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f
                    )
                }
                val bottomSubParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f
                ).apply { setMargins(2, 2, 2, 2) }

                bottomLinear.addView(slotViews[1], bottomSubParams)
                bottomLinear.addView(slotViews[2], LinearLayout.LayoutParams(bottomSubParams))

                rootLinear.addView(bottomLinear)
                binding.gridContainer.addView(rootLinear)
            }

            SessionManager.LAYOUT_4 -> {
                // 4 ngăn 2x2 Grid (Slot 0, 1 ở hàng 1; Slot 2, 3 ở hàng 2)
                val rootLinear = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }

                // Row 1
                val row1 = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f
                    )
                }
                val cellParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f
                ).apply { setMargins(2, 2, 2, 2) }

                row1.addView(slotViews[0], cellParams)
                row1.addView(slotViews[1], LinearLayout.LayoutParams(cellParams))

                // Row 2
                val row2 = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f
                    )
                }
                row2.addView(slotViews[2], cellParams)
                row2.addView(slotViews[3], LinearLayout.LayoutParams(cellParams))

                rootLinear.addView(row1)
                rootLinear.addView(row2)
                binding.gridContainer.addView(rootLinear)
            }
        }

        updateBottomButtonsHighlight(mode)
    }

    private fun toggleMaximizeSlot(slotView: GridSlotView) {
        if (maximizedSlot == slotView) {
            // Đang phóng to -> Thu nhỏ về layout cũ
            maximizedSlot = null
            slotView.isMaximized = false
            applyLayout(currentLayoutMode)
        } else {
            // Phóng to ô này
            maximizedSlot = slotView
            slotView.isMaximized = true
            binding.gridContainer.removeAllViews()
            (slotView.parent as? LinearLayout)?.removeView(slotView)
            (slotView.parent as? FrameLayout)?.removeView(slotView)

            binding.gridContainer.addView(
                slotView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }
    }

    private fun getActiveSlotViews(): List<GridSlotView> {
        return when (currentLayoutMode) {
            SessionManager.LAYOUT_1 -> listOf(slotViews[0])
            SessionManager.LAYOUT_2_V, SessionManager.LAYOUT_2_H -> listOf(slotViews[0], slotViews[1])
            SessionManager.LAYOUT_3 -> listOf(slotViews[0], slotViews[1], slotViews[2])
            else -> slotViews
        }
    }

    private fun updateBottomButtonsHighlight(mode: Int) {
        val buttons = listOf(
            binding.btnLayout1,
            binding.btnLayout2V,
            binding.btnLayout2H,
            binding.btnLayout3,
            binding.btnLayout4
        )
        val modes = listOf(
            SessionManager.LAYOUT_1,
            SessionManager.LAYOUT_2_V,
            SessionManager.LAYOUT_2_H,
            SessionManager.LAYOUT_3,
            SessionManager.LAYOUT_4
        )
        buttons.forEachIndexed { i, btn ->
            btn.alpha = if (modes[i] == mode) 1.0f else 0.5f
        }
    }

    private fun notifySaved() {
        binding.tvSaveStatus.visibility = View.VISIBLE
        binding.tvSaveStatus.alpha = 1.0f
        binding.tvSaveStatus.text = "✓ Đã ghi nhớ vị trí"
    }

    // Tự động lưu toàn bộ session khi app bị ẩn hoặc tắt
    override fun onPause() {
        super.onPause()
        sessionManager.saveAllSlots(slotViews.map { it.slotData })
        sessionManager.saveLayoutMode(currentLayoutMode)
    }

    override fun onDestroy() {
        super.onDestroy()
        slotViews.forEach { it.destroySlot() }
    }
}
