package com.meg.listshop.android.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meg.listshop.android.models.ListshopViewModel

@Composable
fun MainScreen(
    viewModel: ListshopViewModel,
) {
    val scope = rememberCoroutineScope()

    MainScreenContent(
        onRefresh = { },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreenContent(
    onRefresh: () -> Unit = {},
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Loading...")

            Button(onRefresh) { Text("Load Data") }
        }
    }
}



@Composable
fun Error(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = error)
    }
}


@Preview
@Composable
fun MainScreenContentPreview_Success() {
    MainScreenContent(
    )
}
