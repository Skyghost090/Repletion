package com.repletion

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.icu.text.DecimalFormat
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.jaredrummler.ktsh.Shell


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tablayout = findViewById<TabLayout>(R.id.tabLayout)
        val seekBar_ = findViewById<SeekBar>(R.id.seekBar)
        val saturationNumber_ = findViewById<TextView>(R.id.saturationNumber)
        val aboutText_ = findViewById<TextView>(R.id.aboutText)
        val chechBtn = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val title_ = findViewById<TextView>(R.id.textView)
        val toolbarWidget_ = findViewById<MaterialToolbar>(R.id.materialToolbar)
        var progress = DecimalFormat("#,#")

        seekBar_.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {

                // Handle when the progress changes
                override fun onProgressChanged(seek: SeekBar,
                                               progress: Int, fromUser: Boolean) {
                }

                // Handle when the user starts tracking touch
                override fun onStartTrackingTouch(seek: SeekBar) {
                }

                // Handle when the user stops tracking touch
                override fun onStopTrackingTouch(seek: SeekBar) {
                    val sharedPrefs = getSharedPreferences("saturationLevel", MODE_PRIVATE)
                    val tasksPrefs = sharedPrefs.edit()
                    tasksPrefs.putString("saturation", "${progress.format(seekBar_.progress)}")
                    tasksPrefs.apply()
                    saturationNumber_.text = getSharedPreferences("saturationLevel", MODE_PRIVATE).getString("saturation", null)
                }
            })


        fun openCustomTab(url_: String){
            val builder = CustomTabsIntent.Builder()
            builder.setInstantAppsEnabled(true)
            builder.setDownloadButtonEnabled(false)
            val customBuilder = builder.build()
            customBuilder.intent.setPackage("com.android.chrome")
            customBuilder.launchUrl(this, Uri.parse(url_))
        }

        fun detectTab(){
            val imageView_ = findViewById<ImageView>(R.id.imageView)
            when(tablayout.selectedTabPosition){
                0 -> {
                    imageView_.background = resources.getDrawable(R.drawable.image)
                    chechBtn.setImageResource(R.drawable.ic_apply)
                    chechBtn.setOnClickListener {
                        Shell.SU.run("service call SurfaceFlinger 1022 f ${getSharedPreferences("saturationLevel", MODE_PRIVATE).getString("saturation", null)}")
                    }
                    aboutText_.setText(R.string.saturationHeader)
                    saturationNumber_.isVisible = true
                    saturationNumber_.text = "${progress.format(seekBar_.progress)}"
                    seekBar_.isVisible = true
                }

                1 -> {
                    openCustomTab("https://unsplash.com/pt-br/s/fotografias/4k-wallpaper")
                    tablayout.selectTab(tablayout.getTabAt(0))
                }

                2 -> {
                    imageView_.background = resources.getDrawable(R.drawable.githubuser)
                    chechBtn.setImageResource(R.drawable.ic_githubicon)
                    chechBtn.setOnClickListener {
                        openCustomTab("https://github.com/Skyghost090")
                    }
                    aboutText_.setText(R.string.about)
                    saturationNumber_.isVisible = false
                    seekBar_.isVisible = false
                }
            }
        }
        detectTab()

        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {detectTab()}
            override fun onTabUnselected(tab: TabLayout.Tab) {detectTab()}
            override fun onTabReselected(tab: TabLayout.Tab) {detectTab()}
        })

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
        window.statusBarColor = typedValue.data
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode){
            Configuration.UI_MODE_NIGHT_NO -> {
                title_.setTextColor(getColor(R.color.white))
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                title_.setTextColor(getColor(R.color.black))
            }
        }
    }
}