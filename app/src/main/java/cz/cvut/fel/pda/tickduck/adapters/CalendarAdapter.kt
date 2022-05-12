package cz.cvut.fel.pda.tickduck.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.tickduck.R

class CalendarAdapter (
    private val daysOfMonth: ArrayList<String>,
    private val onItemListener: OnItemListener
    ) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder.create(parent, onItemListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.dayOfMonth.text = daysOfMonth[position]
    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, dayText: String?)
    }

    class CalendarViewHolder(
        itemView: View,
        private var onItemListener: OnItemListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init { itemView.setOnClickListener(this) }

        var dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)

        companion object {
            fun create(parent: ViewGroup, onItemListener: OnItemListener): CalendarViewHolder {
                val layoutView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.calendar_cell, parent, false)
                layoutView.layoutParams.height = (parent.height * 0.166666).toInt()
                return CalendarViewHolder(layoutView, onItemListener)
            }
        }

        override fun onClick(view: View?) {
            onItemListener.onItemClick(adapterPosition, dayOfMonth.text as String)
        }
    }
}
