package com.meg.listshop.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


import com.meg.listshop.android.models.ListshopViewModel
import com.meg.listshop.android.ui.theme.MainScreen
import com.meg.listshop.android.ui.theme.theme.ListshopTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class MainActivity : ComponentActivity(), KoinComponent {

    private val viewModel: ListshopViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListshopTheme {
                MainScreen(viewModel)
            }
        }
    }
}
