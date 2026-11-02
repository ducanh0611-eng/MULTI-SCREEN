package com.antigravity.multigrid.model

data class GridSlotData(
    val slotIndex: Int,
    var title: String = "Ngăn ${slotIndex + 1}",
    var url: String = "https://www.google.com",
    var isMuted: Boolean = false
)
