package com.example.chat.ui.contacts

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.chat.R
import com.example.chat.ui.base.composables.Toolbar
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun Tabs(tabs: List<TabItem>, pagerState: PagerState) {

    val scope = rememberCoroutineScope()

    TabRow(
        modifier = Modifier.fillMaxWidth(),
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }) {
        tabs.forEachIndexed { index, tab ->
            LeadingIconTab(
                icon = { Icon(painter = painterResource(tab.icon), contentDescription = "") },
                text = { Text(tab.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun ContactsScreen(
    onBackPressed: () -> Unit
) {

    val tabs = listOf(TabItem.Contacts, TabItem.Users)
    val pagerState = rememberPagerState(0)

    Scaffold(
        topBar = { Toolbar(R.string.contacts, onBackPressed) }
    ) {
        /*Column {
            Tabs(tabs = tabs, pagerState = pagerState)
            HorizontalPager(state = pagerState, count = tabs.size) { page ->
                tabs[page].screen()
            }
        }*/
        ContactsTabScreen()
    }
}

@Composable
fun ContactsTabScreen(
    viewModel: ContactsVM = getViewModel()
) {

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if(it) {
            viewModel.setEvent(ContactsContract.Event.OnReadContacts)
        }
    }

    val state by viewModel.uiState.collectAsState()

    if(state.loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp), color = Color.Blue)
        }
    }
    else {

        if(state.contacts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.contacts_not_found))
            }
        } else {
            LazyColumn() {
                state.contacts.forEach { contact ->
                    item {
                        ContactItem(
                            modifier = Modifier.fillMaxWidth(),
                            contact = contact
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit){
        permissionsLauncher.launch(Manifest.permission.READ_CONTACTS)
    }
}

@Composable
fun UsersTabScreen() {
    Text(text = "2")
}