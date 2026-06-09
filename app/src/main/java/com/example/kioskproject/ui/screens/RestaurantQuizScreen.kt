package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kioskproject.viewmodel.SettingsViewModel

// 퀴즈 문제 데이터
// orders = "메뉴 이름" to "개수"
data class RestaurantQuiz(
    val orders: List<Pair<String, Int>>
)

// 랜덤 퀴즈 생성 함수
// 최대 4가지 메뉴, 각 메뉴당 최대 4개
fun generateRandomRestaurantQuiz(): RestaurantQuiz {
    val menuCount = (1..4).random()

    val selectedMenus = restaurantMenuList
        .shuffled()
        .take(menuCount)

    val orders = selectedMenus.map { menu ->
        menu.name to (1..4).random()
    }

    return RestaurantQuiz(
        orders = orders
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantQuizScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val fontSizeMode by settingsViewModel.fontSize.collectAsState()
    val isSeniorMode = fontSizeMode == "senior"

    // 현재 랜덤 미션
    var quiz by remember {
        mutableStateOf(
            generateRandomRestaurantQuiz()
        )
    }

    // 문제 시작 시간
    var startTime by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    // 정답 성공 시간
    var clearTime by remember {
        mutableLongStateOf(0L)
    }

    var selectedCategory by remember {
        mutableStateOf("pasta")
    }

    var cart by remember {
        mutableStateOf(listOf<RestaurantCartItem>())
    }

    var showSuccessDialog by remember {
        mutableStateOf(false)
    }

    var showFailDialog by remember {
        mutableStateOf(false)
    }

    val filteredMenus = restaurantMenuList.filter {
        it.category == selectedCategory
    }

    val totalPrice = cart.sumOf {
        it.menu.price * it.quantity
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("음식점 퀴즈")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 미션 표시 영역
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "랜덤 미션",
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isSeniorMode) 22.sp else 18.sp
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    quiz.orders.forEach { order ->
                        Text(
                            text = "${order.first} ${order.second}개",
                            fontSize = if (isSeniorMode) 18.sp else 14.sp
                        )
                    }
                }
            }

            // 메뉴 선택 영역
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                RestaurantCategoryPanel(
                    selectedCategory = selectedCategory,
                    isSeniorMode = isSeniorMode,
                    onCategorySelected = {
                        selectedCategory = it
                    },
                    onStaffCall = {
                        // 퀴즈에서는 직원 호출 사용 안 함
                    }
                )

                RestaurantMenuPanel(
                    menus = filteredMenus,
                    isSeniorMode = isSeniorMode,
                    onAddToCart = { menu ->
                        cart = addRestaurantItemToCart(
                            cart,
                            menu
                        )
                    }
                )
            }

            HorizontalDivider()

            // 장바구니 영역
            RestaurantCartSection(
                cart = cart,
                totalPrice = totalPrice,
                isSeniorMode = isSeniorMode,
                onIncrease = { item ->
                    cart = increaseRestaurantItem(
                        cart,
                        item
                    )
                },
                onDecrease = { item ->
                    cart = decreaseRestaurantItem(
                        cart,
                        item
                    )
                },
                onClearCart = {
                    cart = emptyList()
                },
                onOrder = {
                    // 정답 조건:
                    // 1. 미션에 있는 모든 메뉴의 수량이 정확해야 함
                    // 2. 장바구니에 미션 외 메뉴가 있으면 오답
                    val success =
                        quiz.orders.all { order ->
                            val item = cart.find {
                                it.menu.name == order.first
                            }

                            item?.quantity == order.second
                        } && cart.size == quiz.orders.size

                    if (success) {
                        clearTime =
                            (System.currentTimeMillis() - startTime) / 1000

                        showSuccessDialog = true
                    } else {
                        showFailDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        if (isSeniorMode) 260.dp else 220.dp
                    )
            )
        }

        // 정답 다이얼로그
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                },
                title = {
                    Text("정답")
                },
                text = {
                    Column {
                        Text("주문을 정확하게 완료했습니다!")

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text("걸린 시간 : ${clearTime}초")

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text("다음 문제로 넘어갑니다.")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            cart = emptyList()

                            quiz = generateRandomRestaurantQuiz()

                            startTime = System.currentTimeMillis()

                            showSuccessDialog = false
                        }
                    ) {
                        Text("다음 문제")
                    }
                }
            )
        }

        // 오답 다이얼로그
        if (showFailDialog) {
            AlertDialog(
                onDismissRequest = {
                    showFailDialog = false
                },
                title = {
                    Text("오답")
                },
                text = {
                    Column {
                        Text("주문 내용이 미션과 다릅니다.")

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text("미션을 다시 확인해보세요.")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showFailDialog = false
                        }
                    ) {
                        Text("다시 시도")
                    }
                }
            )
        }
    }
}