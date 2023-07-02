package com.fcascan.proyectofinal.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.fcascan.proyectofinal.R

class ItemsAdapter(
    private val itemsList: MutableList<ItemObject>,
    private var onClick: (Int) -> Unit,
    private var onLongClick: (Int) -> Unit,
    private var onPlayClicked: (Int) -> Unit,
    private var onPauseClicked: (Int) -> Unit,
    private var onStopClicked: (Int) -> Unit,
    private var onShareClicked: (Int) -> Unit,
) : RecyclerView.Adapter<ItemsAdapter.ItemsHolder>() {
    private lateinit var context: Context

    class ItemsHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View

        init {
            this.view = v
        }

        fun setTitle(title: String) {
            val txtTitle: TextView = view.findViewById(R.id.txtItemCardTitle)
            txtTitle.text = title
        }

        fun setDescription(description: String) {
            val txtDescription: TextView = view.findViewById(R.id.txtItemCardDescription)
            txtDescription.text = description
        }

        fun getCard(): CardView {
            return view.findViewById(R.id.card_item)
        }

        fun getPlayButton(): View {
            return view.findViewById<Button>(R.id.btnItemCardPlay)
        }

        fun getPauseButton(): View {
            return view.findViewById(R.id.btnItemCardPause)
        }

        fun getStopButton(): View {
            return view.findViewById(R.id.btnItemCardStop)
        }

        fun getShareButton(): View {
            return view.findViewById(R.id.btnItemCardShare)
        }

        fun resetPlayButton() {
            getPlayButton().visibility = View.VISIBLE
            getPauseButton().visibility = View.INVISIBLE
            getStopButton().visibility = View.INVISIBLE
        }
    }

    class ItemObject(title: String, description: String) {
        var title: String = title
        var description: String = description

        override fun toString(): String {
            return "${title}"
        }
    }

    private fun resetAllPlayButtons(recyclerView: RecyclerView) {
        for (i in 0 until itemCount) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as ItemsHolder
            viewHolder.resetPlayButton()
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        context = parent.context
        return (ItemsHolder(view))
    }

    override fun onBindViewHolder(holder: ItemsHolder, index: Int) {
        holder.getPauseButton().visibility = View.INVISIBLE
        holder.getStopButton().visibility = View.INVISIBLE
        holder.setTitle(itemsList[index].title)
        holder.setDescription(itemsList[index].description)
        holder.getCard().setOnClickListener {
            onClick(index)
        }
        holder.getCard().setOnLongClickListener {
            onLongClick(index)
            true
        }
        holder.getPlayButton().setOnClickListener {
            resetAllPlayButtons(holder.itemView.parent as RecyclerView)
            holder.getPauseButton().visibility = View.VISIBLE
            holder.getPlayButton().visibility = View.INVISIBLE
            holder.getStopButton().visibility = View.VISIBLE
            onPlayClicked(index)
        }
        holder.getPauseButton().setOnClickListener {
            resetAllPlayButtons(holder.itemView.parent as RecyclerView)
            holder.getPlayButton().visibility = View.VISIBLE
            holder.getPauseButton().visibility = View.INVISIBLE
            holder.getStopButton().visibility = View.VISIBLE
            onPauseClicked(index)
        }
        holder.getStopButton().setOnClickListener {
            resetAllPlayButtons(holder.itemView.parent as RecyclerView)
            holder.getStopButton().visibility = View.INVISIBLE
            onStopClicked(index)
        }
        holder.getShareButton().setOnClickListener {
            onShareClicked(index)
        }
    }
}