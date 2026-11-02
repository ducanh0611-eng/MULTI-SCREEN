package com.antigravity.multigrid.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antigravity.multigrid.R
import com.antigravity.multigrid.model.AppPreset

class AppPickerDialog(
    context: Context,
    private val slotTitle: String,
    private val onAppSelected: (url: String, name: String) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_app_picker)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val tvTitle = findViewById<TextView>(R.id.tvPickerTitle)
        tvTitle.text = "Chọn ứng dụng cho $slotTitle"

        val etCustomUrl = findViewById<EditText>(R.id.etCustomUrl)
        val btnLoad = findViewById<Button>(R.id.btnLoadCustomUrl)
        val rvPresets = findViewById<RecyclerView>(R.id.rvPresetApps)

        btnLoad.setOnClickListener {
            val text = etCustomUrl.text.toString().trim()
            if (text.isNotEmpty()) {
                val finalUrl = if (text.startsWith("http://") || text.startsWith("https://")) {
                    text
                } else if (text.contains(".") && !text.contains(" ")) {
                    "https://$text"
                } else {
                    "https://www.google.com/search?q=${android.net.Uri.encode(text)}"
                }
                onAppSelected(finalUrl, "Web")
                dismiss()
            }
        }

        // Setup preset RecyclerView with 3 columns
        rvPresets.layoutManager = GridLayoutManager(context, 3)
        rvPresets.adapter = PresetAdapter(AppPreset.getDefaultPresets()) { preset ->
            onAppSelected(preset.url, preset.name)
            dismiss()
        }
    }

    private class PresetAdapter(
        private val items: List<AppPreset>,
        private val onItemClick: (AppPreset) -> Unit
    ) : RecyclerView.Adapter<PresetAdapter.ViewHolder>() {

        class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
            val tvIcon: TextView = itemView.findViewById(R.id.tvPresetIcon)
            val tvName: TextView = itemView.findViewById(R.id.tvPresetName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_preset_app, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvName.text = item.name
            holder.tvIcon.text = item.iconText

            val bg = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                try {
                    setColor(Color.parseColor(item.colorHex))
                } catch (e: Exception) {
                    setColor(Color.DKGRAY)
                }
            }
            holder.tvIcon.background = bg

            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        override fun getItemCount(): Int = items.size
    }
}
