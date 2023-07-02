package com.fcascan.proyectofinal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.fcascan.proyectofinal.R

class ActionsAdapter (
    private val actionsList: MutableList<ActionsObject>,
    private var onClick: (Int) -> Unit,
    private var onLongClick: (Int) -> Unit
) : RecyclerView.Adapter<ActionsAdapter.ActionsViewHolder>() {
    class ActionsViewHolder (v: View): RecyclerView.ViewHolder(v) {
        private var view: View
        init {
            this.view = v
        }

        fun setTitle(title: String) {
            val txtName: TextView = view.findViewById(R.id.txtActionsCardTitle)
            txtName.text = title
        }

        fun setDescription(description: String) {
            val txtDescription: TextView = view.findViewById(R.id.txtActionCardDescription)
            txtDescription.text = description
        }

        fun getCard(): CardView {
            return view.findViewById(R.id.card_action)
        }
    }

    class ActionsObject(title: String, description: String) {
        var title: String = title
        var description: String = description
    }

    override fun getItemCount(): Int {
        return actionsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_action, parent, false)
        return (ActionsViewHolder(view))
    }

    override fun onBindViewHolder(holder: ActionsViewHolder, index: Int) {
        holder.setTitle(actionsList[index].title)
        holder.setDescription(actionsList[index].description)
        holder.getCard().setOnClickListener {
            onClick(index)
        }
        holder.getCard().setOnLongClickListener {
            onLongClick(index)
            true
        }
    }
}