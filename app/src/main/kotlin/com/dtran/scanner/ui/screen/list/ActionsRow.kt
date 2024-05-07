package com.dtran.scanner.ui.screen.list

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp


@Composable
fun ActionsRow(
    onDelete: () -> Unit, onEdit: () -> Unit, onFavorite: () -> Unit, modifier: Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp)
//            .background(Color.Red)
            .fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = onDelete,
            content = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = Color.Red,
                    contentDescription = "delete action",
                    modifier = Modifier.size(dimensionResource(id = com.dtran.scanner.R.dimen.size_action_icon))
                )
            },
        )
//        IconButton(
//            modifier = Modifier.size(actionIconSize),
//            onClick = onEdit,
//            content = {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_search),
//                    tint = Color.Gray,
//                    contentDescription = "edit action",
//                )
//            },
//        )
//        IconButton(modifier = Modifier.size(actionIconSize), onClick = onFavorite, content = {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_back),
//                tint = Color.Red,
//                contentDescription = "Expandable Arrow",
//            )
//        })
    }
}