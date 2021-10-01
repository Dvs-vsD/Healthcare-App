package com.app.consultationpoint.patient.appointment.allAppointments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityAllAppointmentsBinding
import com.app.consultationpoint.patient.dashboard.DashboardViewModel
import com.app.consultationpoint.patient.dashboard.adapter.TodayAtpAdapter
import com.app.consultationpoint.utils.Utils.formatTo
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.show
import com.app.consultationpoint.utils.Utils.toDate
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class AllAppointmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllAppointmentsBinding
    private val viewModel by viewModels<AllAptViewModel>()
    private val dashboardViewModel by viewModels<DashboardViewModel>()
    private var dayAptAdapter: TodayAtpAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getALlEventList().observe(this, { list ->
            if (list.isNotEmpty()) {
                for (event in list) {
                    val date: Long = event.schedual_date.toDate("yyyy-MM-dd")?.time ?: 0
                    val eve = Event(ContextCompat.getColor(this, R.color.yellow), date)
                    binding.calenderView.addEvent(eve)
                }
                Timber.d("event set successfully")
            }
        })

        viewModel.getOneDayApt().observe(this, { dayApt ->
            if (dayApt.isNotEmpty() && dayAptAdapter != null) {
                binding.recyclerView.show()
                dayAptAdapter?.setList(dayApt, viewModel.getAptDoctorList().value)
            } else {
                binding.recyclerView.hide()
                binding.tvDate.text = getString(R.string.tv_no_appointments)
            }
        })

        inThis()
    }

    @SuppressLint("SetTextI18n")
    private fun inThis() {
        binding.ivBack.setOnClickListener { onBackPressed() }

        val today = Calendar.getInstance().time

        binding.calenderView.setFirstDayOfWeek(Calendar.SUNDAY)
        binding.tvCurrentMonthYear.text = today.formatTo("MMMM, yyyy ")
        binding.tvDate.text = "Appointments of " + today.formatTo("dd, MMMM yyyy")

        viewModel.fetchAllEventList()

        viewModel.getAptForThisDay(today.formatTo("yyyy-MM-dd"))

        dayAptAdapter = TodayAtpAdapter(viewModel.getOneDayApt().value, this, viewModel.getAptDoctorList().value, dashboardViewModel)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = dayAptAdapter

        binding.calenderView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                binding.tvDate.text = "Appointments of " + dateClicked?.formatTo("dd, MMMM yyyy")
                dateClicked?.formatTo("yyyy-MM-dd")?.let { viewModel.getAptForThisDay(it) }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                binding.tvCurrentMonthYear.text = firstDayOfNewMonth?.formatTo("MMMM, yyyy ")
            }
        })
    }
}