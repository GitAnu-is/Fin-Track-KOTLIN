package com.example.fintrack.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrack.R
import com.example.fintrack.model.Transaction

class TransactionAdapter(
    private val transactionList: MutableList<Transaction>,
    private val onEdit: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amountText: TextView = view.findViewById(R.id.amountText)
        val typeText: TextView = view.findViewById(R.id.typeText)
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val titleText: TextView = view.findViewById(R.id.titleText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val editButton: ImageButton = view.findViewById(R.id.editButton)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = transactionList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactionList[position]

        holder.amountText.text = String.format("%.2f", transaction.amount)
        holder.typeText.text = transaction.type
        holder.categoryText.text = transaction.category
        holder.titleText.text = transaction.title
        holder.dateText.text = transaction.date

        val context = holder.itemView.context
        holder.amountText.setTextColor(
            ContextCompat.getColor(
                context,
                if (transaction.type == "EXPENSE") android.R.color.holo_red_dark
                else android.R.color.holo_green_dark
            )
        )

        holder.editButton.setOnClickListener { onEdit(position) }
        holder.deleteButton.setOnClickListener { onDelete(position) }
    }
}
