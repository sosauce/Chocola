package com.sosauce.chocola.presentation.components

//@Composable
//fun SearchHistory(
//    modifier: Modifier = Modifier,
//    onInsertToSearch: (String) -> Unit
//) {
//
//    val list = List(5) { it }
//
//    Card(
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
//        ),
//        shape = RoundedCornerShape(24.dp)
//    ) {
//        LazyColumn(
//            modifier = modifier
//        ) {
//            items(
//                items = list
//            ) { item ->
//                CuteDropdownMenuItem(
//                    onClick = {},
//                    text = { Text(item.toString()) },
//                    leadingIcon = {
////                        Icon(
////                            imageVector = Icons.Rounded.Search,
////                            contentDescription = null
////                        )
//                    },
//                    trailingIcon = {
//                        IconButton(
//                            onClick = { onInsertToSearch(item.toString()) }
//                        ) {
////                            Icon(
////                                imageVector = Icons.Rounded.ArrowOutward,
////                                contentDescription = null,
////                                modifier = Modifier.rotate(270f)
////                            )
//                        }
//                    }
//                )
//            }
//        }
//    }
//
//}