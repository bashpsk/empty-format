package io.bashpsk.emptyformatdemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.bashpsk.emptyformat.EmptyFormat
import kotlinx.collections.immutable.toImmutableList

@Composable
fun FormatDemoScreen() {

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding->

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            items(
                items = EmptyFormat.DateTimePattern.entries.toImmutableList()
            ) { pattern ->

                FormatView(pattern = pattern)
            }
        }
    }
}