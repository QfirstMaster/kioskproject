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

data class MovieQuiz(
    val orders: List<Pair<String, Int>>,
    val pointSave: Boolean,
    val paymentMethod: String
)

private val movieQuizMenuList = listOf(
    MovieMenuItem("커플콤보", 12000, "🎬", "combo"),
    MovieMenuItem("팝콘콤보", 10500, "🍿", "combo"),
    MovieMenuItem("나쵸콤보", 11000, "🧀", "combo"),
    MovieMenuItem("오리지널팝콘", 6000, "🍿", "popcorn"),
    MovieMenuItem("카라멜팝콘", 6500, "🍿", "popcorn"),
    MovieMenuItem("치즈팝콘", 6500, "🍿", "popcorn"),
    MovieMenuItem("콜라", 3000, "🥤", "drink"),
    MovieMenuItem("사이다", 3000, "🥤", "drink"),
    MovieMenuItem("제로콜라", 3000, "🥤", "drink"),
    MovieMenuItem("더블치즈나쵸", 4900, "🧀", "snack"),
    MovieMenuItem("핫도그", 4000, "🌭", "snack"),
    MovieMenuItem("즉석구이오징어", 5000, "🦑", "snack")
)

fun generateRandomMovieQuiz(): MovieQuiz {
    val selectedMenus = movieQuizMenuList
        .shuffled()
        .take(Random.nextInt(1, 5))

    val orders = selectedMenus.map {
        it.name to Random.nextInt(1, 5)
    }

    return MovieQuiz(
        orders = orders,
        pointSave = Random.nextBoolean(),
        paymentMethod = if (Random.nextBoolean()) "카드결제" else "현금결제"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieQuizScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val fontSizeMode by settingsViewModel.fontSize.collectAsState()
    val isSeniorMode = fontSizeMode == "senior"

    var quiz by remember { mutableStateOf(generateRandomMovieQuiz()) }

    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var clearTime by remember { mutableLongStateOf(0L) }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }

    var selectedCategory by remember { mutableStateOf("combo") }
    var cart by remember { mutableStateOf(listOf<CartItem>()) }

    var orderStep by remember { mutableStateOf(0) }

    var pointSaved by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("") }
    var insertedCash by remember { mutableStateOf(0) }

    var showMissionDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showFailDialog by remember { mutableStateOf(false) }
    var failReason by remember { mutableStateOf("") }

    val filteredMenus = movieQuizMenuList.filter {
        it.category == selectedCategory
    }

    val totalPrice = cart.sumOf {
        it.menu.price * it.quantity
    }

    LaunchedEffect(startTime) {
        while (true) {
            elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
            delay(1000)
        }
    }

    fun resetForNextQuiz() {
        quiz = generateRandomMovieQuiz()
        cart = emptyList()
        selectedCategory = "combo"
        orderStep = 0
        pointSaved = false
        phoneNumber = ""
        selectedPaymentMethod = ""
        insertedCash = 0
        failReason = ""
        startTime = System.currentTimeMillis()
        elapsedSeconds = 0
    }

    fun checkAnswer() {
        val menuCorrect =
            quiz.orders.all { order ->
                val cartItem = cart.find {
                    it.menu.name == order.first
                }
                cartItem?.quantity == order.second
            } && cart.size == quiz.orders.size

        val pointCorrect = pointSaved == quiz.pointSave
        val paymentCorrect = selectedPaymentMethod == quiz.paymentMethod

        when {
            !menuCorrect -> {
                failReason = "메뉴 또는 수량이 미션과 다릅니다."
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
                failReason = "결제수단이 다릅니다. 미션의 결제방식을 확인하세요."
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
                        text = "영화관 퀴즈",
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
                .padding(if (isSeniorMode) 20.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(
                if (isSeniorMode) 14.dp else 10.dp
            )
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

            MovieCategoryTabs(
                selectedCategory = selectedCategory,
                isSeniorMode = isSeniorMode,
                onCategorySelected = {
                    selectedCategory = it
                }
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(
                    if (isSeniorMode) 2 else 3
                ),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(
                    if (isSeniorMode) 12.dp else 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(
                    if (isSeniorMode) 12.dp else 8.dp
                )
            ) {
                items(filteredMenus) { item ->
                    MovieMenuCard(
                        item = item,
                        isSeniorMode = isSeniorMode,
                        onClick = {
                            cart = addItemToCart(cart, item)
                        }
                    )
                }
            }

            HorizontalDivider()

            CartSection(
                cart = cart,
                totalPrice = totalPrice,
                isSeniorMode = isSeniorMode,
                onIncrease = { item ->
                    cart = addItemToCart(cart, item)
                },
                onDecrease = { item ->
                    cart = removeOneItemFromCart(cart, item)
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

        if (showMissionDialog) {
            AlertDialog(
                onDismissRequest = {
                    showMissionDialog = false
                },
                title = {
                    Text("랜덤 미션")
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quiz.orders.forEach {
                            Text("${it.first} ${it.second}개")
                        }

                        HorizontalDivider()

                        Text("포인트 적립 : ${if (quiz.pointSave) "예" else "아니오"}")
                        Text("결제 방식 : ${quiz.paymentMethod}")
                        Text("소요 시간 : ${elapsedSeconds}초")
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

        if (orderStep == 1) {
            PointChoiceDialog(
                isSeniorMode = isSeniorMode,
                onSavePoint = {
                    pointSaved = true
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
                    selectedPaymentMethod = "카드결제"
                    orderStep = 4
                },
                onCash = {
                    selectedPaymentMethod = "현금결제"
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
                onDismissRequest = {
                    showSuccessDialog = false
                },
                title = {
                    Text("정답")
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("주문을 정확하게 완료했습니다!")
                        Text("걸린 시간 : ${clearTime}초")
                        Text("다음 문제로 넘어갑니다.")
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
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
                onDismissRequest = {
                    showFailDialog = false
                },
                title = {
                    Text("오답")
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(failReason)
                        Text("미션을 다시 확인하고 다시 시도해보세요.")
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
}