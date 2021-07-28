package com.app.consultationpoint.calender

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.FragmentCalenderBinding
import com.app.consultationpoint.patient.dashboard.DashboardViewModel
import com.app.consultationpoint.utils.Utils
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.calender_item.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class CalenderFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentCalenderBinding
    private val calender = Calendar.getInstance()
    private var currentMonth = 0
    private lateinit var today: Date
    private lateinit var tomorrow: Date
    private var currentDtPos: Int = 0
    private val viewModel by viewModel<DashboardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calender.time = Date()
        currentMonth = calender[Calendar.MONTH]
        currentDtPos = calender[Calendar.DATE] - 1

        setCalender()
    }

    @SuppressLint("SetTextI18n")
    private fun setCalender() {

        binding.tvCurrentMonthYear.text = "${DateUtils.getMonthName(Date())}, ${
            DateUtils.getYear(Date())
        }"

        binding.singleRowCalendar.calendarViewManager = object : CalendarViewManager {

            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                return if (position < currentDtPos)
                    R.layout.disabled_calender_item
                else {
                    if (isSelected) {
                        R.layout.selected_calender_item
                    } else
                        R.layout.calender_item
                }
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                holder.itemView.tvDate.text = DateUtils.getDayNumber(date)
                holder.itemView.tvDay.text = DateUtils.getDay3LettersName(date)
            }
        }

        binding.singleRowCalendar.calendarChangesObserver = object : CalendarChangesObserver {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                super.whenSelectionChanged(isSelected, position, date)

                today = Utils.todayDate(date)
                tomorrow = Utils.tomorrowDate(date)

                viewModel.getTodayApt(today, tomorrow)

                Timber.e("old date %s", today.toString())
                Timber.e("new date %s", tomorrow.toString())
            }
        }

        binding.singleRowCalendar.calendarSelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                return if (position < currentDtPos)
                    false
                else
                    true
            }
        }

        binding.singleRowCalendar.setDates(getFutureDatesOfCurrentMonth())

        binding.singleRowCalendar.initialPositionIndex = Date().date - 1

        binding.singleRowCalendar.init()

        binding.singleRowCalendar.select(Date().date - 1)
    }

    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        currentMonth = calender[Calendar.MONTH]
        return getDates(mutableListOf())
    }


    private fun getDates(list: MutableList<Date>): List<Date> {
        // load dates of whole month
        calender.set(Calendar.MONTH, currentMonth)
        calender.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calender.time)
        while (currentMonth == calender[Calendar.MONTH]) {
            calender.add(Calendar.DATE, +1)
            if (calender[Calendar.MONTH] == currentMonth)
                list.add(calender.time)
        }
        calender.add(Calendar.DATE, -1)
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalenderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}