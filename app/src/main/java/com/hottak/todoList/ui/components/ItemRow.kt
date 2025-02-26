package com.hottak.todoList.ui.components

import com.hottak.todoList.model.ItemData

//프로토 타입 ui
//@Composable
//fun ItemRow(
//    item: ItemData,
//    isInProgress: Boolean,
//    isEditing: MutableState<Boolean>,
//    editingItem: MutableState<ItemData?>,
//    onCheckedChange: (Boolean, ItemData) -> Unit,
//    onDelete: (ItemData) -> Unit // Add this parameter to handle deletion
//) {
//    // 진행 중이 아닌 경우 배경 색상 설정
//    val rowBackgroundColor = if (isInProgress) {
//        Color.LightGray.copy(alpha = 0.3f) // 진행 중일 때는 연한 회색 배경
//    } else {
//        Color.White.copy(alpha = 0.5f) // 완료된 항목은 투명도 적용
//    }
//    val dateParts = item.date.split(" ")
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
//            .background(rowBackgroundColor, shape = RoundedCornerShape(8.dp))
//            .padding(start = 20.dp, top = 16.dp, bottom = 16.dp, end = 20.dp)
//            .then(Modifier.alpha(if (isInProgress) 1f else 0.5f)),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        if (!isEditing.value || editingItem.value != item) {
//            Checkbox(
//                checked = item.isCompleted,
//                onCheckedChange = { isChecked ->
//                    onCheckedChange(isChecked, item) // Handle checked change
//                },
//                modifier = Modifier
//                    .size(50.dp)
//                    .weight(0.5f),  // 1등분 차지
//                colors = CheckboxDefaults.colors(
//                    checkedColor = Color.Blue,
//                    uncheckedColor = Color.Gray
//                )
//            )
//        }
//
//        Column(
//            modifier = Modifier
//                .weight(3f)  // 제목과 내용이 차지할 비율
//                .padding(start = 16.dp)
//        ) {
//            Text(
//                text = item.title,
//                fontWeight = FontWeight.Bold,
//                fontSize = 16.sp,
//                color = Color(0xFF2A4174),
//                maxLines = 1,  // 한 줄로 제한 (길면 생략부호 처리 가능)
//                overflow = TextOverflow.Ellipsis  // 넘치면 ... 처리
//            )
//            Text(
//                text = item.content,
//                fontSize = 14.sp,
//                color = Color.Black,
//                modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
//                style = TextStyle(
//                    lineBreak = LineBreak.Paragraph,
//                )
//            )
//        }
//
//        Column(
//            modifier = Modifier
//                .padding(start = 10.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // 년-월-일 표시
//            Text(
//                text = dateParts.getOrNull(0) ?: "",
//                fontSize = 14.sp,
//                color = Color.Black,
//                fontWeight = FontWeight.Bold
//            )
//
//            Spacer(modifier = Modifier.height(4.dp))  // 두 줄 사이에 여백 추가
//            // 시간:분:초 표시
//            Text(
//                text = dateParts.getOrNull(1) ?: "",
//                fontSize = 14.sp,
//                color = Color.Black,
//            )
//            Row (
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            )
//            {
//                Button(
//                    colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF2A4174),
//                    contentColor = Color.White
//                ),
//                    onClick = {isEditing.value = true; editingItem.value = item }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Edit,
//                        contentDescription = "Edit",
//                        tint = Color.White,
//                        modifier = Modifier.size(18.dp) // 아이콘 크기 고정
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(6.dp))  // 두 줄 사이에 여백 추가
//
//                Button(
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.Black,
//                        contentColor = Color.White
//                    ),
//                    onClick = {
//                        onDelete(item)
//                    }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = "Delete",
//                        tint = Color.White,
//                        modifier = Modifier.size(18.dp) // 아이콘 크기 고정
//                    )
//                }
//            }
//        }
//    }
//}


