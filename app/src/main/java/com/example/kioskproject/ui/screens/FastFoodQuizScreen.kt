package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import kotlinx.coroutines.delay
import kotlin.random.Random

data class FastFoodQuizOrder(
    val burger: String,
    val isSet: Boolean,
    val side: String,
    val drink: String,
    val quantity: Int
)

data class FastFoodQuiz(
    val orders: List<FastFoodQuizOrder>,
    val pointSave: Boolean,
    val paymentMethod: String
)

fun generateRandomFastFoodQuiz(): FastFoodQuiz {
    val burgers = listOf("치즈버거", "불고기버거", "새우버거")
    val sides = listOf("감자튀김", "치즈스틱")
    val drinks = listOf("콜라", "사이다")

    val orders = burgers
        .shuffled()
        .take(Random.nextInt(1, 4))
        .map { burger ->
            val isSet = Random.nextBoolean()

            FastFoodQuizOrder(
                burger = burger,
                isSet = isSet,
                side = if (isSet) sides.random() else "",
                drink = if (isSet) drinks.random() else "",
                quantity = Random.nextInt(1, 4)
            )
        }

    return FastFoodQuiz(
        orders = orders,
        pointSave = Random.nextBoolean(),
        paymentMethod = if (Random.nextBoolean()) "카드결제" else "현금결제"
    )
}

fun fastFoodQuizOrderText(order: FastFoodQuizOrder): String {
    return if (order.isSet) {
        "${order.burger} 세트 ${order.quantity}개 (${order.side} / ${order.drink})"
    } else {
        "${order.burger} 단품 ${order.quantity}개"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastFoodQuizScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val fontSizeMode by settingsViewModel.fontSize.collectAsState()
    val isSeniorMode = fontSizeMode == "senior"

    var quiz by remember { mutableStateOf(generateRandomFastFoodQuiz()) }

    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    var clearTime by remember { mutableLongStateOf(0L) }

    var selectedCategory by remember { mutableStateOf("burger") }
    var selectedBurger by remember { mutableStateOf<FastFoodMenuItem?>(null) }
    var selectedSide by remember { mutableStateOf("") }
    var selectedDrink by remember { mutableStateOf("") }

    var cart by remember { mutableStateOf(listOf<FastFoodCartItem>()) }

    var showMissionDialog by remember { mutableStateOf(false) }
    var showSetDialog by remember { mutableStateOf(false) }
    var showSideDialog by remember { mutableStateOf(false) }
    var showDrinkDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showFailDialog by remember { mutableStateOf(false) }

    var failReason by remember { mutableStateOf("") }

    var orderStep by remember { mutableStateOf(0) }
    var phoneNumber by remember { mutableStateOf("") }
    var pointSaved by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("") }
    var insertedCash by remember { mutableStateOf(0) }

    val filteredMenus = fastFoodMenuList.filter {
        it.category == selectedCategory
    }

    val totalPrice = cart.sumOf {
        it.price * it.quantity
    }

    LaunchedEffect(startTime) {
        while (true) {
            elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
            delay(1000)
        }
    }

    fun resetForNextQuiz() {
        quiz = generateRandomFastFoodQuiz()

        startTime = System.currentTimeMillis()
        elapsedSeconds = 0L
        clearTime = 0L

        selectedCategory = "burger"
        selectedBurger = null
        selectedSide = ""
        selectedDrink = ""

        cart = emptyList()

        orderStep = 0
        phoneNumber = ""
        pointSaved = false
        paymentMethod = ""
        insertedCash = 0

        showMissionDialog = false
        showSetDialog = false
        showSideDialog = false
        showDrinkDialog = false
        showSuccessDialog = false
        showFailDialog = false

        failReason = ""
    }

    fun checkAnswer() {
        val menuCorrect =
            quiz.orders.all { order ->
                val cartItem = cart.find {
                    it.burger == order.burger &&
                            it.isSet == order.isSet &&
                            it.side == order.side &&
                            it.drink == order.drink
                }

                cartItem?.quantity == order.quantity
            } && cart.size == quiz.orders.size

        val pointCorrect = pointSaved == quiz.pointSave
        val paymentCorrect = paymentMethod == quiz.paymentMethod

        when {
            !menuCorrect -> {
                failReason = "메뉴, 단품/세트, 사이드, 음료 또는 수량이 미션과 다릅니다."
                showFailDialog = true
            }

            !pointCorrect -> {
                failReason =
                    if (quiz.pointSave) {
                        "포인트 적립을 해야 하는 미션입니다."
                    } else {
                        "포인트 적립 안 함을 선택해야 하는 미션입니다."
                    }

                showFailDialog = true
            }

            !paymentCorrect -> {
                failReason = "결제수단이 다릅니다. 미션의 결제 방식을 확인하세요."
                showFailDialog = true
            }

            else -> {
                clearTime = (System.currentTimeMillis() - startTime) / 1000
                showSuccessDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "패스트푸드 퀴즈",
                        fontSize = if (isSeniorMode) 25.sp else 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
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
                .padding(if (isSeniorMode) 20.dp else 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        showMissionDialog = true
                    }
                ) {
                    Text(
                        text = "미션 보기",
                        fontSize = if (isSeniorMode) 18.sp else 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "경과 시간 : ${elapsedSeconds}초",
                fontWeight = FontWeight.Bold,
                fontSize = if (isSeniorMode) 18.sp else 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            FastFoodCategoryTabs(
                selectedCategory = selectedCategory,
                isSeniorMode = isSeniorMode,
                onCategorySelected = {
                    selectedCategory = it
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(
                    if (isSeniorMode) 2 else 3
                ),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredMenus) { item ->
                    FastFoodMenuCard(
                        item = item,
                        isSeniorMode = isSeniorMode,
                        onClick = {
                            if (item.category == "burger") {
                                selectedBurger = item
                                showSetDialog = true
                            } else {
                                cart = addFastFoodItemToCart(
                                    cart,
                                    item.name,
                                    false,
                                    "",
                                    "",
                                    item.price
                                )
                            }
                        }
                    )
                }
            }

            HorizontalDivider()

            FastFoodCartSection(
                cart = cart,
                totalPrice = totalPrice,
                isSeniorMode = isSeniorMode,
                onIncrease = {
                    cart = increaseFastFoodItem(cart, it)
                },
                onDecrease = {
                    cart = decreaseFastFoodItem(cart, it)
                },
                onClearCart = {
                    cart = emptyList()
                },
                onOrder = {
                    if (cart.isNotEmpty()) {
                        orderStep = 1
                    }
                }
            )
        }
    }
    if (showMissionDialog) {
        AlertDialog(
            onDismissRequest = {
                showMissionDialog = false
            },
            title = {
                Text("랜덤 미션")
            },
            text = {
                Column {
                    quiz.orders.forEach {
                        Text(fastFoodQuizOrderText(it))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "포인트 적립 : ${
                            if (quiz.pointSave) "예" else "아니오"
                        }"
                    )

                    Text("결제 방식 : ${quiz.paymentMethod}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMissionDialog = false
                    }
                ) {
                    Text("확인")
                }
            }
        )
    }

    // 버거 선택 후 세트/단품 선택
    if (showSetDialog) {
        AlertDialog(
            onDismissRequest = {
                showSetDialog = false
            },
            title = {
                Text("세트 선택")
            },
            text = {
                Text("세트로 변경하시겠습니까?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSetDialog = false
                        showSideDialog = true
                    }
                ) {
                    Text("세트")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        selectedBurger?.let {
                            cart = addFastFoodItemToCart(
                                cart,
                                it.name,
                                false,
                                "",
                                "",
                                it.price
                            )
                        }

                        showSetDialog = false
                    }
                ) {
                    Text("단품")
                }
            }
        )
    }

    // 세트 선택 후 사이드 선택
    if (showSideDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text("사이드 선택")
            },
            text = {
                Column {
                    listOf("감자튀김", "치즈스틱").forEach { side ->
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = {
                                selectedSide = side
                                showSideDialog = false
                                showDrinkDialog = true
                            }
                        ) {
                            Text(side)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // 사이드 선택 후 음료 선택, 이후 장바구니 추가
    if (showDrinkDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text("음료 선택")
            },
            text = {
                Column {
                    listOf("콜라", "사이다").forEach { drink ->
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = {
                                selectedDrink = drink

                                selectedBurger?.let {
                                    cart = addFastFoodItemToCart(
                                        cart,
                                        it.name,
                                        true,
                                        selectedSide,
                                        selectedDrink,
                                        it.price + 3000
                                    )
                                }

                                showDrinkDialog = false
                            }
                        ) {
                            Text(drink)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (orderStep == 1) {
        PointChoiceDialog(
            isSeniorMode = isSeniorMode,
            onSavePoint = {
                phoneNumber = ""
                orderStep = 2
            },
            onSkip = {
                pointSaved = false
                orderStep = 3
            }
        )
    }

    if (orderStep == 2) {
        PhoneNumberDialog(
            phoneNumber = phoneNumber,
            isSeniorMode = isSeniorMode,
            onNumberClick = {
                if (phoneNumber.length < 11) {
                    phoneNumber += it
                }
            },
            onBackspace = {
                if (phoneNumber.isNotEmpty()) {
                    phoneNumber = phoneNumber.dropLast(1)
                }
            },
            onConfirm = {
                if (phoneNumber.length == 11) {
                    pointSaved = true
                    orderStep = 3
                }
            },
            onCancel = {
                pointSaved = false
                orderStep = 3
            }
        )
    }

    if (orderStep == 3) {
        PaymentChoiceDialog(
            totalPrice = totalPrice,
            isSeniorMode = isSeniorMode,
            onCard = {
                paymentMethod = "카드결제"
                orderStep = 4
            },
            onCash = {
                paymentMethod = "현금결제"
                insertedCash = 0
                orderStep = 5
            }
        )
    }

    if (orderStep == 4) {
        CardPaymentDialog(
            isSeniorMode = isSeniorMode,
            onPaymentComplete = {
                orderStep = 6
                checkAnswer()
            }
        )
    }

    if (orderStep == 5) {
        CashPaymentDialog(
            totalPrice = totalPrice,
            insertedCash = insertedCash,
            isSeniorMode = isSeniorMode,
            onInsertCash = {
                insertedCash += it
            },
            onComplete = {
                if (insertedCash >= totalPrice) {
                    orderStep = 6
                    checkAnswer()
                }
            },
            onCancel = {
                insertedCash = 0
                orderStep = 3
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text("정답")
            },
            text = {
                Column {
                    Text("주문을 정확하게 완료했습니다!")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("걸린 시간 : ${clearTime}초")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        resetForNextQuiz()
                    }
                ) {
                    Text("다음 문제")
                }
            }
        )
    }

    if (showFailDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text("오답")
            },
            text = {
                Column {
                    Text(failReason)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("미션을 다시 확인하고 시도해보세요.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFailDialog = false
                        orderStep = 0
                    }
                ) {
                    Text("다시 시도")
                }
            }
        )
    }
}