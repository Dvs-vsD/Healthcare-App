package com.app.consultationpoint.general

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.app.consultationpoint.R
import com.app.consultationpoint.databinding.ActivityOnBoardingBinding
import com.app.consultationpoint.utils.Utils.hide
import com.app.consultationpoint.utils.Utils.show

class OnBoardingActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    private var layouts: IntArray? = null
    private var vpAdapter: MyViewPagerAdapter? = null
    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inThis()
    }

    private fun inThis() {
        layouts = intArrayOf(
            R.layout.welcome_screen_1,
            R.layout.welcome_screen_2,
            R.layout.welcome_screen_3
        )
        vpAdapter = MyViewPagerAdapter()
        binding.viewPager.adapter = vpAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.addOnPageChangeListener(this)

        binding.btnNext.setOnClickListener {
            binding.viewPager.setCurrentItem(binding.viewPager.currentItem + 1, true)
        }
        binding.btnSkip.setOnClickListener {
            goToLoginActivity()
        }
        binding.btnGetStarted.setOnClickListener {
            goToLoginActivity()
        }
    }

    //    ViewPager Adapter
    inner class MyViewPagerAdapter : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = layoutInflater.inflate(layouts!![position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts?.size ?: 0
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        if (position == vpAdapter?.count?.minus(1)) {
            binding.btnNext.hide()
            binding.btnGetStarted.show()
        } else {
            binding.btnNext.show()
            binding.btnGetStarted.hide()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}