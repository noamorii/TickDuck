package cz.cvut.fel.pda.tickduck.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.tickduck.R
import cz.cvut.fel.pda.tickduck.utils.CalendarUtils
import java.time.LocalDate

class CalendarAdapter(
    private val days: ArrayList<LocalDate?>,
    private val onItemListener: OnItemListener
    ) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    companion object {
         lateinit var c_days: ArrayList<LocalDate?>
    }

    init {
        c_days = days
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder.create(parent, days, onItemListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        if (date != null) {
            holder.dayOfMonth.text = date.dayOfMonth.toString()
            if (date == CalendarUtils.selectedDay)
                holder.dayOfMonth.setBackgroundResource(R.drawable.calendar_cell_background2)

        } else {
            holder.dayOfMonth.text = ""
        }
    }

    override fun getItemCount(): Int {
        return days.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, date: LocalDate?)
    }

    class CalendarViewHolder(
        itemView: View,
        private var onItemListener: OnItemListener,
        private val days: ArrayList<LocalDate?>
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init { itemView.setOnClickListener(this) }
        val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)
        val parent: View = itemView.findViewById(R.id.parentView)

        companion object {
            fun create(parent: ViewGroup, days: ArrayList<LocalDate?>, onItemListener: OnItemListener): CalendarViewHolder {
                val layoutView = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.calendar_cell, parent, false).apply {
                        if (c_days.size > 15)
                            layoutParams.height = (parent.height * 0.166666).toInt()
                        else
                            layoutParams.height = parent.height
                    }
                return CalendarViewHolder(layoutView, onItemListener, days)
            }
        }

        override fun onClick(view: View?) {
            onItemListener.onItemClick(adapterPosition, days[adapterPosition])
        }
    }
}
