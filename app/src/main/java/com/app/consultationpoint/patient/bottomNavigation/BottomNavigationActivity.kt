package com.app.consultationpoint.patient.bottomNavigation

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.app.consultationpoint.BaseFragment
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityBottomNavigationBinding
import com.app.consultationpoint.general.LoginActivity
import com.app.consultationpoint.notification.RemainderBroadcast
import com.app.consultationpoint.patient.appointment.myAppointments.MyAppointmentsFragment
import com.app.consultationpoint.patient.chat.room.ChatListFragment
import com.app.consultationpoint.patient.dashboard.DashboardFragment
import com.app.consultationpoint.patient.dashboard.DashboardViewModel
import com.app.consultationpoint.patient.doctor.DoctorListFragment
import com.app.consultationpoint.patient.userProfile.UserProfileActivity
import com.app.consultationpoint.utils.Utils
import com.app.consultationpoint.utils.Utils.formatTo
import com.google.android.material.navigation.NavigationView
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavLogger
import com.ncapdevi.fragnav.FragNavSwitchController
import com.ncapdevi.fragnav.FragNavTransactionOptions
import com.ncapdevi.fragnav.tabhistory.UniqueTabHistoryStrategy
import dagger.hilt.android.AndroidEntryPoint
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.header_layout.view.*
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class BottomNavigationActivity(override val numberOfRootFragments: Int = 4) : AppCompatActivity(),
    BaseFragment.FragmentNavigation,
    FragNavController.RootFragmentListener,
    NavigationView.OnNavigationItemSelectedListener, FragNavController.TransactionListener {

    private lateinit var binding: ActivityBottomNavigationBinding
    private val fragNavController: FragNavController =
        FragNavController(supportFragmentManager, R.id.container)
    private val viewModel by viewModels<DashboardViewModel>()
    private lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getAptDateList().observe(this, { calList ->
            if (calList != null && calList.isNotEmpty()) {
                createAptNotifications(calList)
            }
        })

        if (intent.getBooleanExtra("newUser", false)) {
            val config = RealmConfiguration.Builder()
                .name(Utils.getUserId() + "db.realm")
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build()

            Realm.setDefaultConfiguration(config)
            Timber.d("db created and default deleted")
        }

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)

        binding.navigationView.getHeaderView(0).tvUserName.text = "Hello, ${Utils.getFirstName()}"
//        binding.navigationView.getHeaderView(0).ivProfile.setImageBitmap(Utils.getImageBitMap(this, Utils.getUserProfile()))
//        binding.ivProfile.setImageBitmap(Utils.getImageBitMap(this, Utils.getUserProfile()))

        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        binding.ivBack.setOnClickListener { onBackPressed() }

        fragNavController.apply {
            transactionListener = this@BottomNavigationActivity
            rootFragmentListener = this@BottomNavigationActivity
            createEager = true
            fragNavLogger = object : FragNavLogger {
                override fun error(message: String, throwable: Throwable) {
                    //  Log.e(TAG, message, throwable)
                }
            }

            defaultTransactionOptions = FragNavTransactionOptions.newBuilder().customAnimations(
                R.anim.slide_in_from_right,
                R.anim.slide_out_to_left,
                R.anim.slide_in_from_left,
                R.anim.slide_out_to_right
            ).build()
            fragmentHideStrategy = FragNavController.DETACH_ON_NAVIGATE_HIDE_ON_SWITCH

            navigationStrategy = UniqueTabHistoryStrategy(object : FragNavSwitchController {
                override fun switchTab(index: Int, transactionOptions: FragNavTransactionOptions?) {
                    binding.bottomNav.selectTabAtPosition(index)
                }
            })
        }

        fragNavController.initialize(0, savedInstanceState)

        val initial = savedInstanceState == null
        if (initial) {
            binding.bottomNav.selectTabAtPosition(0)
        }
        fragNavController.rootFragmentListener = this

        binding.bottomNav.setOnTabSelectListener({ itemId -> bottomBarSelection(itemId) }, initial)

        //Alarm Notification for first 5 appointments
        viewModel.fetchALlAptFromRDB()
    }

    private fun createAptNotifications(calList: ArrayList<Calendar>) {
        for ((index, value) in calList.withIndex()) {
            if (index < 5) {
                Timber.d(value.time.formatTo("dd-MM-yyyy hh:mm a"))
                val intent = Intent(this, RemainderBroadcast::class.java)
                intent.putExtra("channel_id", index)
                intent.putExtra("time", value.time.formatTo("hh:mm a"))
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        this,
                        index,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                val alarmManager: AlarmManager =
                    this.getSystemService(ALARM_SERVICE) as AlarmManager

                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    value.timeInMillis - 1800000,
                    pendingIntent
                )
            } else
                break
        }
    }

    private fun bottomBarSelection(itemId: Int) {
        when (itemId) {
            R.id.botNavHome -> {
                fragNavController.switchTab(0)
                binding.ivBack.visibility = View.GONE
                binding.tvHeading.visibility = View.GONE
                binding.ivProfile.visibility = View.VISIBLE
                toggle.isDrawerIndicatorEnabled = true
            }
            R.id.botNavDoctor -> {
                fragNavController.switchTab(1)
                binding.ivBack.visibility = View.VISIBLE
                binding.tvHeading.visibility = View.VISIBLE
                binding.tvHeading.text = getString(R.string.doctor_list)
                binding.ivProfile.visibility = View.GONE
                toggle.isDrawerIndicatorEnabled = false
            }
            R.id.botNavChat -> {
                fragNavController.switchTab(2)
                binding.ivBack.visibility = View.VISIBLE
                binding.tvHeading.visibility = View.VISIBLE
                if (Utils.getUserType() == 0)
                    binding.tvHeading.text = getString(R.string.chat_with_doc)
                else
                    binding.tvHeading.text = getString(R.string.chat_with_patient)
                binding.ivProfile.visibility = View.GONE
                toggle.isDrawerIndicatorEnabled = false
            }
            R.id.botNavApt -> {
                fragNavController.switchTab(3)
                binding.ivBack.visibility = View.VISIBLE
                binding.tvHeading.visibility = View.VISIBLE
                binding.tvHeading.text = getString(R.string.tv_my_appointments)
                binding.ivProfile.visibility = View.GONE
                toggle.isDrawerIndicatorEnabled = false
            }
        }
    }


    override fun getRootFragment(index: Int): Fragment {
        when (index) {
            0 -> return DashboardFragment.newInstance(0)
            1 -> return DoctorListFragment.newInstance(0)
            2 -> return ChatListFragment.newInstance(0)
            3 -> return MyAppointmentsFragment.newInstance(0)
        }
        throw IllegalStateException("Need to send an index that we know")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragNavController.onSaveInstanceState(outState)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.botNavHome -> fragNavController.switchTab(0)
            R.id.botNavDoctor -> fragNavController.switchTab(1)
            R.id.botNavChat -> fragNavController.switchTab(2)
            R.id.botNavApt -> fragNavController.switchTab(3)
            R.id.menu_logout -> {
                viewModel.logout()
                goToLoginScreen()
            }
        }

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (fragNavController.popFragment().not()) {
                super.onBackPressed()
            }
        }
    }

    override fun pushFragment(
        fragment: Fragment,
        sharedElementList: List<Pair<View, String>>?
    ) {
        val options = FragNavTransactionOptions.newBuilder()
//        options.reordering = true
        sharedElementList?.let {
            it.forEach { pair ->
                options.addSharedElement(pair)
            }
        }
        fragNavController.pushFragment(fragment, options.build())
    }

    private fun goToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onFragmentTransaction(
        fragment: Fragment?,
        transactionType: FragNavController.TransactionType
    ) {
        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
    }

    override fun onTabTransaction(fragment: Fragment?, index: Int) {
        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
    }
}