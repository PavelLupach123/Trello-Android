package com.example.trello.screens

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.trello.R
import com.example.trello.components.OpenEditTodoDialog
import com.example.trello.components.SearchBarTop
import com.example.trello.components.TaskCompleteAnimations
import com.example.trello.components.TaskList
import com.example.trello.components.addTodoDialog
import com.example.trello.constants.Constants
import com.example.trello.datastore.SettingsStore
import com.example.trello.notification.Notification
import com.example.trello.notification.channelID
import com.example.trello.receiver.RepeatingTasksReceiver
import com.example.trello.room.Todo
import com.example.trello.room.TodoViewModel
import com.example.trello.room.deletedtodo.DeletedTodoViewModel
import com.example.trello.ui.theme.TrelloTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val activity = LocalContext.current as Activity
    val context = LocalContext.current
    val todoViewModel = TodoViewModel(activity.application)
    val deletedTodoViewModel = DeletedTodoViewModel(activity.application)
    val listState = rememberLazyListState()
    val selectedItem = rememberSaveable {
        mutableIntStateOf(-1)
    }
    val todoListFromFlow by todoViewModel.getAllTodosFlow.collectAsState(initial = emptyList())
    val topAppBarColors = TopAppBarDefaults
    val openDialog = rememberSaveable {
        mutableStateOf(false)
    }
    val openEditDialog = rememberSaveable {
        mutableStateOf(false)
    }
    var enteredText by remember {
        mutableStateOf("")
    }
    val descriptionText by remember{
        mutableStateOf("")
    }
    val dateText = remember {
        mutableStateOf("")
    }
    val isDateDialogShowing = remember {
        mutableStateOf(false)
    }
    val isRepeatingState = remember{
        mutableStateOf(false)
    }
    val timeText = remember {
        mutableStateOf("")
    }
    val isTimeDialogShowing = remember {
        mutableStateOf(false)
    }
    val isLottiePlaying = remember {
        mutableStateOf(true)
    }

    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val coroutineScope = rememberCoroutineScope()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    createNotificationChannel(context.applicationContext)
    // setup settings store
    val settingsStore = SettingsStore(context)
    val savedAnimationKey = settingsStore.getAnimationKey.collectAsState(initial = true)
    val savedThemeKey = settingsStore.getThemeModeKey.collectAsState(initial = "0")
    TrelloTheme(darkTheme = when (savedThemeKey.value.toString()) {
        "null" -> {isSystemInDarkTheme()}
        "0" -> {isSystemInDarkTheme()}
        "1" -> {false}
        else -> {true}
    }
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                ModalDrawerSheet {
                    Row(
                        modifier = modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Trello")
                        Text(text = "")
                    }
                    HorizontalDivider()
                    NavigationDrawerItem(
                        modifier = modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        icon = { Icon(imageVector = Icons.Default.Delete, contentDescription = null)},
                        label = { Text(text = "Видалені завдання") },
                        selected = false,
                        onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            navHostController.navigate(route = Screen.DeletedTodosScreen.route)
                        }
                    )
                    NavigationDrawerItem(
                        modifier = modifier.padding(start = 16.dp, end = 16.dp),
                        icon = { Icon(imageVector = Icons.Default.Info, contentDescription = null)},
                        label = { Text(text = "Про додаток") },
                        selected = false,
                        onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            navHostController.navigate(route = Screen.GuideScreen.route)
                        }
                    )
                    NavigationDrawerItem(
                        modifier = modifier.padding(bottom = 16.dp,start = 16.dp, end = 16.dp),
                        icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = null)},
                        label = { Text(text = "Налаштування") },
                        selected = false,
                        onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            navHostController.navigate(route = Screen.SettingsScreen.route)
                        }
                    )
                }
            }
        ) {
            Scaffold(
                snackbarHost = {SnackbarHost(hostState = snackBarHostState)},
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    drawerState.apply {
                                        if(isClosed) open() else close()
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        title = {
                            Row(
                                modifier = modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    fontSize = 25.sp
                                )
                                SearchBarTop(searchText) { searchText = it }
                            }
                        },
                        colors = topAppBarColors.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text(text = stringResource(R.string.add_task_button)) },
                        icon = { Icon(painter = painterResource(id = R.drawable.ic_add_task), contentDescription = null) },
                        onClick = {
                            openDialog.value = true
                        },
                        expanded = listState.isScrollingUp()
                    )
                }
            ) { paddingValues ->
                enteredText = addTodoDialog(
                    openDialog,
                    enteredText,
                    descriptionText,
                    dateText,
                    isDateDialogShowing,
                    context,
                    timeText,
                    isTimeDialogShowing,
                    todoViewModel,
                    isRepeatingState.value
                )
                Surface(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if(todoListFromFlow.isEmpty()){
                        Box(modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                            contentAlignment = Alignment.Center) {
                            Column(
                                modifier = modifier,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    modifier = modifier
                                        .size(50.dp)
                                        .alpha(0.8f),
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = stringResource(R.string.empty_list_no_tasks_text),
                                    fontSize = 30.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    }else {
                        TaskList(
                            state = listState,
                            list = todoListFromFlow,
                            todoViewModel = todoViewModel,
                            deletedTodoViewModel = deletedTodoViewModel,
                            onClick = {index->
                                selectedItem.intValue = index
                                openEditDialog.value = true
                            },
                            searchText = searchText,
                            coroutineScope = rememberCoroutineScope(),
                            snackbarHostState = snackBarHostState
                        )
                    }
                    if (openEditDialog.value){
                        OpenEditTodoDialog(
                            todoListFromFlow,
                            selectedItem,
                            openEditDialog,
                            todoViewModel,
                            enteredText,
                            todoListFromFlow[selectedItem.intValue].todoDescription!!,
                            context
                        )
                    }
                }

            }
            if(todoViewModel.isAnimationPlayingState.value && (savedAnimationKey.value == true || savedAnimationKey.value == null)){
                Column(modifier = modifier.fillMaxSize()) {
                    TaskCompleteAnimations(
                        isLottiePlaying = isLottiePlaying,
                        modifier = modifier.fillMaxSize()
                    )
                    LaunchedEffect(Unit){
                        delay(2200)
                        withContext(Dispatchers.Main){
                            todoViewModel.isAnimationPlayingState.value = false
                        }

                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationChannel(context: Context){
    val name = "Нагадування"
    val desc = "Надсилає сповіщення про завдання, додані до списку"
    val importance = NotificationManager.IMPORTANCE_HIGH
    val channel = NotificationChannel(channelID,name,importance)
    channel.description = desc
    channel.enableLights(true)
    channel.enableVibration(true)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if(notificationManager.getNotificationChannel("Нагадування") != null){
        notificationManager.deleteNotificationChannel("Скасувати")
    }
    if(notificationManager.notificationChannels.isNullOrEmpty()){
        notificationManager.createNotificationChannel(channel)
    }
}
fun scheduleNotification(
    context: Context,
    titleText:String?,
    messageText:String?,
    time:String?,
    todo: Todo
) {

    val intent = Intent(context, Notification::class.java)
    intent.putExtra("titleExtra", titleText)
    intent.putExtra("messageExtra", messageText)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        todo.notificationID,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val currTime = getTimeInMillis(time!!)
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        currTime,
        pendingIntent
    )
}
fun setRepeatingAlarm(context: Context){
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val tomorrowTimestamp = calendar.timeInMillis
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    val intent = Intent(context.applicationContext,RepeatingTasksReceiver::class.java).also {
        it.action = "repeating_tasks"
    }
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        tomorrowTimestamp,
        PendingIntent.getBroadcast(context.applicationContext,Constants.BROADCAST_ID,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    )
}

fun cancelNotification(
    context: Context,
    todo: Todo
){
    val intent = Intent(context, Notification::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        todo.notificationID,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(pendingIntent)
}
fun getTimeInMillis(date: String): Long {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
    val mDate = sdf.parse(date)
    return mDate!!.time
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}


@Composable
fun CurrentDateTimeComparator(
    inputDate:String,
    inputTime:String,
    onTruePerform: () -> Unit
) {
    val calendarInstance = Calendar.getInstance()
    val currentDate = calendarInstance.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val parsedDate = format.parse(inputDate)
    val calendar = calendarInstance.apply {
        time = parsedDate!!
        set(Calendar.HOUR_OF_DAY, inputTime.substringBefore(":").toInt())
        set(Calendar.MINUTE, inputTime.substringAfter(":").toInt())
        set(Calendar.SECOND, 0)
    }
    val currentTime = Calendar.getInstance().timeInMillis
    if(calendar >= currentDate && calendar.timeInMillis >= currentTime){
        onTruePerform()
    }
}


fun currentDateTimeComparator(
    inputDate:String,
    inputTime:String,
    onTruePerform: () -> Unit
) {
    val calendarInstance = Calendar.getInstance()
    val currentDate = calendarInstance.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val parsedDate = format.parse(inputDate)
    val calendar = calendarInstance.apply {
        time = parsedDate!!
        set(Calendar.HOUR_OF_DAY, inputTime.substringBefore(":").toInt())
        set(Calendar.MINUTE, inputTime.substringAfter(":").toInt())
        set(Calendar.SECOND, 0)
    }
    val currentTime = Calendar.getInstance().timeInMillis
    if(calendar >= currentDate && calendar.timeInMillis >= currentTime){
        onTruePerform()
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun DisplayAppScreen() {
    MyApp(navHostController = rememberNavController())
}