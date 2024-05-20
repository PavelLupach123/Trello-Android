package com.example.trello.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trello.room.Todo
import com.example.trello.room.TodoViewModel
import com.example.trello.room.deletedtodo.DeletedTodo
import com.example.trello.room.deletedtodo.DeletedTodoViewModel
import com.example.trello.screens.currentDateTimeComparator
import com.example.trello.screens.scheduleNotification


@Composable
fun DeletedTodoItem(
    deletedTodo: DeletedTodo,
    todoViewModel: TodoViewModel,
    deletedTodoViewModel: DeletedTodoViewModel,
    context:Context = LocalContext.current,
    modifier: Modifier = Modifier
) {
    val isShowing = remember {
        mutableStateOf(false)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
            .heightIn(60.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                isShowing.value = true
            }
            .background(MaterialTheme.colorScheme.inverseOnSurface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)){
            Text(
                text = deletedTodo.title!!,
                fontSize = 20.sp,
                textDecoration = if(deletedTodo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
        }
    }
    if(isShowing.value){
        ActionDialogBox(
            isDialogShowing = isShowing,
            title = "Виберіть дію",
            message = "Відновити чи видалити це завдання?",
            confirmButtonText = "Відновити" ,
            dismissButtonText = "Видалити",
            onConfirmClick = {
                val newTodo = Todo(
                    ID = deletedTodo.ID,
                    title = deletedTodo.title,
                    todoDescription = deletedTodo.todoDescription,
                    isCompleted = deletedTodo.isCompleted,
                    date = deletedTodo.date,
                    time = deletedTodo.time,
                    notificationID = deletedTodo.notificationID,
                    isRecurring = deletedTodo.isRecurring
                )
                todoViewModel.insertTodo(newTodo)
                if(newTodo.time!!.isNotEmpty()){
                    currentDateTimeComparator(inputDate = newTodo.date!!, inputTime = newTodo.time!!) {
                        scheduleNotification(
                            context = context,
                            titleText = newTodo.title!!,
                            messageText = newTodo.todoDescription!!,
                            time = "${newTodo.date} ${newTodo.time}",
                            todo = newTodo
                        )
                    }
                }
                deletedTodoViewModel.deleteDeletedTodo(deletedTodo)
            },
            onDismissClick = {
                deletedTodoViewModel.deleteDeletedTodo(deletedTodo)
            },

            )
    }
}