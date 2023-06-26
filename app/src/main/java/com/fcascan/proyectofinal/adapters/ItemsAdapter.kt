package com.fcascan.proyectofinal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.fcascan.proyectofinal.R

class ItemsAdapter(
    private val itemsList: MutableList<ItemObject>,
    private var onClick: (Int) -> Unit,
    private var onLongClick: (Int) -> Unit,
    private var onPlayClicked: (Int) -> Unit,
    private var onStopClicked: (Int) -> Unit,
    private var onShareClicked: (Int) -> Unit
) : RecyclerView.Adapter<ItemsAdapter.ItemsHolder>() {
    private lateinit var context: Context

    class ItemsHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View

        init {
            this.view = v
        }

        fun setTitle(title: String) {
            val txtTitle: TextView = view.findViewById(R.id.txtCardTitle)
            txtTitle.text = title
        }

        fun setDescription(description: String) {
            val txtDescription: TextView = view.findViewById(R.id.txtCardDescription)
            txtDescription.text = description
        }

        fun getCard(): CardView {
            return view.findViewById(R.id.card_item)
        }

        fun getPlayButton(): View {
            return view.findViewById(R.id.btnCardPlay)
        }

        fun getStopButton(): View {
            return view.findViewById(R.id.btnCardStop)
        }

        fun getShareButton(): View {
            return view.findViewById(R.id.btnCardShare)
        }
    }

    class ItemObject(title: String, description: String) {
        var title: String = title
        var description: String = description

        override fun toString(): String {
            return "${title}"
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
//        holder.setImgItem(context, itemsList[index].imgItem)
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
            onPlayClicked(index)
        }
        holder.getStopButton().setOnClickListener {
            onStopClicked(index)
        }
        holder.getShareButton().setOnClickListener {
            onShareClicked(index)
        }
    }
}