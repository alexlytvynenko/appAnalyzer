package com.alexlytvynenko.appanalyzer.internal.ui

import android.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.CoordinatorLayout
import android.view.Menu
import com.alexlytvynenko.appanalyzer.R
import com.alexlytvynenko.appanalyzer.internal.*
import com.alexlytvynenko.appanalyzer.internal.behavior.BottomNavigationBehavior
import com.alexlytvynenko.appanalyzer.internal.entity.RequestEntity
import com.alexlytvynenko.appanalyzer.internal.ui.details.RequestDetailsFragment
import com.alexlytvynenko.appanalyzer.internal.ui.list.ExceptionListFragment
import com.alexlytvynenko.appanalyzer.internal.ui.list.LogListFragment
import com.alexlytvynenko.appanalyzer.internal.ui.list.RequestListFragment
import kotlinx.android.synthetic.main.activity_display_network.*
import android.support.design.widget.AppBarLayout

internal class DisplayAnalyzerActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener,
        LayoutController {

    private val MENU_ITEM_ID_LOG = 0
    private val MENU_ITEM_ID_EXCEPTION = 1
    private val MENU_ITEM_ID_REQUEST = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_network)
        setSupportActionBar(toolbar)
        initBottomNavigation()
        fragmentManager.addOnBackStackChangedListener(this)
        if (fragmentManager.backStackEntryCount == 0) {
            replaceFragment(LogListFragment())
        }
    }

    private fun initBottomNavigation() {
        val menu = bottomNavigation.menu
        menu.add(Menu.NONE, MENU_ITEM_ID_LOG, Menu.NONE, "Logs").setIcon(R.drawable.ic_log)
        menu.add(Menu.NONE, MENU_ITEM_ID_EXCEPTION, Menu.NONE, "Exceptions").setIcon(R.drawable.ic_error)
        menu.add(Menu.NONE, MENU_ITEM_ID_REQUEST, Menu.NONE, "Requests").setIcon(R.drawable.ic_network_info)
        (bottomNavigation.layoutParams as CoordinatorLayout.LayoutParams).behavior = BottomNavigationBehavior<BottomNavigationView>()
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                MENU_ITEM_ID_LOG -> consume {
                    replaceFragment(LogListFragment())
                }
                MENU_ITEM_ID_EXCEPTION -> consume {
                    replaceFragment(ExceptionListFragment())
                }
                MENU_ITEM_ID_REQUEST -> consume {
                    replaceFragment(RequestListFragment())
                }
                else -> false
            }
        }
    }

    override fun setTheme(resId: Int) {
        // We don't want this to be called with an incompatible theme.
        // This could happen if you implement runtime switching of themes
        // using ActivityLifecycleCallbacks.
        if (resId != R.style.app_analyzer_AppAnalyzer_Base_NoActionBar) {
            return
        }
        super.setTheme(resId)
    }

    internal fun openDetailsFragment(request: RequestEntity) {
        replaceFragment(instanceOf<RequestDetailsFragment>(RequestDetailsFragment.ARG_REQUEST to request), R.id.fragmentContainer, true)
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentManager.removeOnBackStackChangedListener(this)
    }

    override fun onBackStackChanged() {
        val currentFragment = getCurrentFragment()
        if (currentFragment is RequestListFragment) {
            currentFragment.loadData()
        }
        appBar.setExpanded(true)
        ((bottomNavigation.layoutParams as CoordinatorLayout.LayoutParams).behavior
                as BottomNavigationBehavior).reset(bottomNavigation)
    }

    override fun enableScroll() {
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        toolbar.layoutParams = params
    }

    override fun disableScroll() {
        val params = toolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        toolbar.layoutParams = params
    }

}
