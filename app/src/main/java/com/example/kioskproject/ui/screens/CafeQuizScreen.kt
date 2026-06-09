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
import com.example.kioskproject.R
import com.example.kioskproject.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

data class CafeQuizOrder(
    val menuName: String,
    val category: String,
    val quantity: Int,
    val size: String,
    val ice: String
)

data class CafeQuiz(
    val orders: List<CafeQuizOrder>,
    val pointSave: Boolean,
    val paymentMethod: String
)

private val cafeQuizMenuList = listOf(
    CafeMenuItem("아메리카노", 2500, R.drawable.americano, "coffee"),
    CafeMenuItem("카페라떼", 3500, R.drawable.cafe_latte, "coffee"),
    CafeMenuItem("바닐라라떼", 4000, R.drawable.vanilla_latte, "coffee"),

    CafeMenuItem("레몬에이드", 4500, R.drawable.lemonade, "ade"),
    CafeMenuItem("청포도에이드", 4500, R.drawable.green_grape_ade, "ade"),

    CafeMenuItem("딸기스무디", 5000, R.drawable.strawberry_smoothie, "smoothie"),
    CafeMenuItem("망고스무디", 5000, R.drawable.mango_smoothie, "smoothie"),

    CafeMenuItem("치즈케이크", 4500, R.drawable.cheese_cake, "dessert"),
    CafeMenuItem("초코케이크", 4500, R.drawable.chocolate_cake, "dessert")
)

fun generateRandomCafeQuiz(): CafeQuiz {
    val selectedMenus = cafeQuizMenuList
        .shuffled()
        .take(Random.nextInt(1, 5))

    val orders = selectedMenus.map { menu ->

        val size = when (menu.category) {
            "coffee", "ade", "smoothie" -> listOf("S", "M", "L").random()
            else -> "-"
        }

        val ice = when (menu.category) {
            "coffee", "ade" -> listOf("적게", "보통", "많이").random()
            else -> "-"
        }

        CafeQuizOrder(
            menuName = menu.name,
            category = menu.category,
            quantity = Random.nextInt(1, 5),
            size = size,
            ice = ice
        )
    }

    return CafeQuiz(
        orders = orders,
        pointSave = Random.nextBoolean(),
        paymentMethod = if (Random.nextBoolean()) "카드결제" else "현금결제"
    )
}

fun cafeQuizOrderText(order: CafeQuizOrder): String {
    return when (order.category) {
        "coffee", "ade" ->
            "${order.menuName} ${order.size} / 얼음 ${order.ice} / ${order.quantity}잔"

        "smoothie" ->
            "${order.menuName} ${order.size} / ${order.quantity}잔"

        "dessert" ->
            "${order.menuName} ${order.quantity}개"

        else ->
            "${order.menuName} ${order.quantity}개"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeQuizScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val fontSizeMode by settingsViewModel.fontSize.collectAsState()
    val isSeniorMode = fontSizeMode == "senior"

    var quiz by remember {
        mutableStateOf(generateRandomCafeQuiz())
    }

    var startTime by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    var clearTime by remember {
        mutableLongStateOf(0L)
    }

    var elapsedSeconds by remember {
        mutableLongStateOf(0L)
    }

    var selectedCategory by remember {
        mutableStateOf("coffee")
    }

    var cart by remember {
        mutableStateOf(listOf<CafeCartItem>())
    }

    var selectedMenu by remember {
        mutableStateOf<CafeMenuItem?>(null)
    }

    var selectedSize by remember {
        mutableStateOf("M")
    }

    var selectedIce by remember {
        mutableStateOf("보통")
    }

    var showSizeDialog by remember {
        mutableStateOf(false)
    }

    var showIceDialog by remember {
        mutableStateOf(false)
    }

    var orderStep by remember {
        mutableStateOf(0)
    }

    var phoneNumber by remember {
        mutableStateOf("")
    }

    var pointSaved by remember {
        mutableStateOf(false)
    }

    var paymentMethod by remember {
        mutableStateOf("")
    }

    var insertedCash by remember {
        mutableStateOf(0)
    }

    var showMissionDialog by remember {
        mutableStateOf(false)
    }

    var showSuccessDialog by remember {
        mutableStateOf(false)
    }

    var showFailDialog by remember {
        mutableStateOf(false)
    }

    var failReason by remember {
        mutableStateOf("")
    }

    val filteredMenus = cafeQuizMenuList.filter {
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
        quiz = generateRandomCafeQuiz()
        startTime = System.currentTimeMillis()
        clearTime = 0L
        elapsedSeconds = 0L

        selectedCategory = "coffee"
        cart = emptyList()

        selectedMenu = null
        selectedSize = "M"
        selectedIce = "보통"

        orderStep = 0
        phoneNumber = ""
        pointSaved = false
        paymentMethod = ""
        insertedCash = 0

        showMissionDialog = false
        showSuccessDialog = false
        showFailDialog = false
        failReason = ""
    }

    fun checkAnswer() {
        val menuCorrect =
            quiz.orders.all { order ->

                val cartItem = cart.find {
                    it.menu.name == order.menuName &&
                            it.size == order.size &&
                            it.ice == order.ice
                }

                cartItem?.quantity == order.quantity
            } && cart.size == quiz.orders.size

        val pointCorrect = pointSaved == quiz.pointSave
        val paymentCorrect = paymentMethod == quiz.paymentMethod

        when {
            !menuCorrect -> {
                failReason = "메뉴, 사이즈, 얼음량 또는 수량이 미션과 다릅니다."
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
                        text = "카페 퀴즈",
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

            CafeCategoryTabs(
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredMenus) { item ->

                    CafeMenuCard(
                        item = item,
                        isSeniorMode = isSeniorMode,
                        onClick = {
                            selectedMenu = item

                            when (item.category) {
                                "coffee", "ade", "smoothie" -> {
                                    showSizeDialog = true
                                }

                                "dessert" -> {
                                    cart = addCafeItemToCart(
                                        cart,
                                        item,
                                        "-",
                                        "-"
                                    )
                                }
                            }
                        }
                    )
                }
            }

            HorizontalDivider()

            CafeCartSection(
                cart = cart,
                totalPrice = totalPrice,
                isSeniorMode = isSeniorMode,
                onIncrease = { item ->
                    cart = addCafeItemToCart(
                        cart,
                        item.menu,
                        item.size,
                        item.ice
                    )
                },
                onDecrease = { item ->
                    cart = removeCafeItemFromCart(
                        cart,
                        item
                    )
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
                        quiz.orders.forEach { order ->
                            Text(cafeQuizOrderText(order))
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

        if (showSizeDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSizeDialog = false
                },
                title = {
                    Text("사이즈 선택")
                },
                text = {
                    Column {
                        listOf("S", "M", "L").forEach { size ->

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                onClick = {
                                    selectedSize = size
                                    showSizeDialog = false

                                    when (selectedMenu?.category) {
                                        "coffee", "ade" -> {
                                            showIceDialog = true
                                        }

                                        "smoothie" -> {
                                            selectedMenu?.let { menu ->
                                                cart = addCafeItemToCart(
                                                    cart,
                                                    menu,
                                                    selectedSize,
                                                    "-"
                                                )
                                            }
                                        }
                                    }
                                }
                            ) {
                                Text(size)
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }

        if (showIceDialog) {
            AlertDialog(
                onDismissRequest = {
                    showIceDialog = false
                },
                title = {
                    Text("얼음 선택")
                },
                text = {
                    Column {
                        listOf("적게", "보통", "많이").forEach { ice ->

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                onClick = {
                                    selectedIce = ice

                                    selectedMenu?.let { menu ->
                                        cart = addCafeItemToCart(
                                            cart,
                                            menu,
                                            selectedSize,
                                            selectedIce
                                        )
                                    }

                                    showIceDialog = false
                                }
                            ) {
                                Text(ice)
                            }
                        }
                    }
                },
                confirmButton = {}
            )
        }

        if (orderStep == 1) {
            AlertDialog(
                onDismissRequest = {},
                title = {
                    Text("포인트 적립")
                },
                text = {
                    Text("포인트를 적립하시겠습니까?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            pointSaved = true
                            phoneNumber = ""
                            orderStep = 2
                        }
                    ) {
                        Text("적립하기")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            pointSaved = false
                            orderStep = 3
                        }
                    ) {
                        Text("건너뛰기")
                    }
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