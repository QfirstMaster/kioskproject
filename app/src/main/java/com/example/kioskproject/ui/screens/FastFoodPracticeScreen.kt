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
import kotlin.random.Random
import androidx.compose.ui.Alignment

import com.example.kioskproject.R
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

// 메뉴 정보
data class FastFoodMenuItem(
    val name: String,
    val price: Int,
    val imageRes: Int,//수정
    val category: String
)

// 장바구니 정보
data class FastFoodCartItem(
    val burger: String,
    val isSet: Boolean,
    val side: String,
    val drink: String,
    val quantity: Int,
    val price: Int
)

val fastFoodMenuList = listOf(

    // 버거
    FastFoodMenuItem(
        "치즈버거",
        5000,
        R.drawable.cheese_burger,
        "burger"
    ),

    FastFoodMenuItem(
        "불고기버거",
        5500,
        R.drawable.beef_burger,
        "burger"
    ),

    FastFoodMenuItem(
        "새우버거",
        6000,
        R.drawable.shrimp,
        "burger"
    ),

    // 사이드
    FastFoodMenuItem(
        "감자튀김",
        2500,
        R.drawable.fries,
        "side"
    ),

    FastFoodMenuItem(
        "치즈스틱",
        3000,
        R.drawable.cheese_stick,
        "side"
    ),

    // 음료
    FastFoodMenuItem(
        "콜라",
        2000,
        R.drawable.cola,
        "drink"
    ),

    FastFoodMenuItem(
        "사이다",
        2000,
        R.drawable.soda,
        "drink"
    ),

    // 디저트
    FastFoodMenuItem(
        "아이스크림",
        1500,
        R.drawable.ice_cream,
        "dessert"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastFoodPracticeScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {

    // 설정 불러오기
    val fontSizeMode by settingsViewModel.fontSize.collectAsState()

    val isSeniorMode =
        fontSizeMode == "senior"

    // 현재 카테고리
    var selectedCategory by remember {
        mutableStateOf("burger")
    }

    // 현재 선택 버거
    var selectedBurger by remember {
        mutableStateOf<FastFoodMenuItem?>(null)
    }

    // 세트 여부
    var isSetMenu by remember {
        mutableStateOf(false)
    }

    // 선택 사이드
    var selectedSide by remember {
        mutableStateOf("")
    }

    // 선택 음료
    var selectedDrink by remember {
        mutableStateOf("")
    }

    // 장바구니
    var cart by remember {
        mutableStateOf(
            listOf<FastFoodCartItem>()
        )
    }

    // Dialog 상태
    var showSetDialog by remember {
        mutableStateOf(false)
    }

    var showSideDialog by remember {
        mutableStateOf(false)
    }

    var showDrinkDialog by remember {
        mutableStateOf(false)
    }

    // 카테고리별 메뉴
    val filteredMenus =
        fastFoodMenuList.filter {
            it.category == selectedCategory
        }

    // 총 금액
    val totalPrice =
        cart.sumOf {
            it.price * it.quantity
        }
    // 주문 단계
    var orderStep by remember {
        mutableStateOf(0)
    }

// 전화번호
    var phoneNumber by remember {
        mutableStateOf("")
    }

// 포인트 적립 여부
    var pointSaved by remember {
        mutableStateOf(false)
    }

// 결제수단
    var paymentMethod by remember {
        mutableStateOf("")
    }

// 주문번호
    var orderNumber by remember {
        mutableStateOf("")
    }

// 현금 투입 금액
    var insertedCash by remember {
        mutableStateOf(0)
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

                    phoneNumber =
                        phoneNumber.dropLast(1)
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

                orderNumber =
                    makeFastFoodOrderNumber()

                orderStep = 4
            },

            onCash = {

                paymentMethod = "현금결제"

                insertedCash = 0

                orderNumber =
                    makeFastFoodOrderNumber()

                orderStep = 5
            }
        )
    }
    if (orderStep == 4) {

        CardPaymentDialog(

            isSeniorMode = isSeniorMode,

            onPaymentComplete = {

                orderStep = 6
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
                }
            },

            onCancel = {

                insertedCash = 0

                orderStep = 3
            }
        )
    }
    if (orderStep == 6) {

        OrderCompleteDialog(

            orderNumber = orderNumber,

            paymentMethod = paymentMethod,

            pointSaved = pointSaved,

            phoneNumber = phoneNumber,

            isSeniorMode = isSeniorMode,

            onConfirm = {

                cart = emptyList()

                selectedCategory = "burger"

                orderStep = 0

                phoneNumber = ""

                pointSaved = false

                paymentMethod = ""

                insertedCash = 0

                orderNumber = ""
            }
        )
    }
    Scaffold(
        topBar = {

            TopAppBar(
                title = {

                    Text(
                        text = "한국 패스트푸드",

                        fontSize =
                            if (isSeniorMode)
                                25.sp
                            else
                                18.sp,

                        fontWeight =
                            FontWeight.Bold
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
                .padding(
                    if (isSeniorMode)
                        20.dp
                    else
                        12.dp
                )
        ) {

            Text(
                text = "원하는 메뉴를 선택하세요",

                fontSize =
                    if (isSeniorMode)
                        22.sp
                    else
                        16.sp,

                fontWeight = FontWeight.Bold
            )

            FastFoodCategoryTabs(
                selectedCategory = selectedCategory,
                isSeniorMode = isSeniorMode,
                onCategorySelected = {
                    selectedCategory = it
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(
                    if (isSeniorMode)
                        2
                    else
                        3
                ),

                modifier = Modifier.weight(1f),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(filteredMenus) { item ->

                    FastFoodMenuCard(
                        item = item,
                        isSeniorMode = isSeniorMode,

                        onClick = {

                            // 버거만 세트 선택
                            if (item.category == "burger") {

                                selectedBurger = item

                                showSetDialog = true

                            } else {

                                cart =
                                    addFastFoodItemToCart(
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

                    cart =
                        increaseFastFoodItem(
                            cart,
                            it
                        )
                },

                onDecrease = {

                    cart =
                        decreaseFastFoodItem(
                            cart,
                            it
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
    }
    if (showSetDialog) {

        AlertDialog(
            onDismissRequest = {

                showSetDialog = false
            },

            title = {

                Text("세트 선택")
            },

            text = {

                Text(
                    "세트로 변경하시겠습니까?"
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        isSetMenu = true

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

                            cart =
                                addFastFoodItemToCart(
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
    if (showSideDialog) {

        AlertDialog(
            onDismissRequest = {},

            title = {
                Text("사이드 선택")
            },

            text = {

                Column {

                    listOf(
                        "감자튀김",
                        "치즈스틱"
                    ).forEach { side ->

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
    if (showDrinkDialog) {

        AlertDialog(
            onDismissRequest = {},

            title = {
                Text("음료 선택")
            },

            text = {

                Column {

                    listOf(
                        "콜라",
                        "사이다"
                    ).forEach { drink ->

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),

                            onClick = {

                                selectedDrink = drink

                                selectedBurger?.let {

                                    cart =
                                        addFastFoodItemToCart(
                                            cart,
                                            it.name,
                                            true,
                                            selectedSide,
                                            selectedDrink,

                                            // 세트 가격
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
}
@Composable
fun FastFoodCategoryTabs(
    selectedCategory: String,
    isSeniorMode: Boolean,
    onCategorySelected: (String) -> Unit
) {

    val categories = listOf(
        "burger" to "버거",
        "side" to "사이드",
        "drink" to "음료",
        "dessert" to "디저트"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        categories.forEach { category ->

            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(
                        if (isSeniorMode)
                            60.dp
                        else
                            48.dp
                    ),

                onClick = {

                    onCategorySelected(
                        category.first
                    )
                }
            ) {

                Text(
                    text = category.second,

                    fontSize =
                        if (isSeniorMode)
                            18.sp
                        else
                            13.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun FastFoodMenuCard(
    item: FastFoodMenuItem,
    isSeniorMode: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                if (isSeniorMode)
                    220.dp
                else
                    180.dp
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.SpaceBetween
        ) {

            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier.size(80.dp)
            )

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = item.name,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "${item.price}원",

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )
            }

            Button(
                modifier =
                    Modifier.fillMaxWidth(),

                onClick = onClick
            ) {

                Text("담기")
            }
        }
    }
}
@Composable
fun FastFoodCartSection(
    cart: List<FastFoodCartItem>,
    totalPrice: Int,
    isSeniorMode: Boolean,

    onIncrease: (FastFoodCartItem) -> Unit,
    onDecrease: (FastFoodCartItem) -> Unit,

    onClearCart: () -> Unit,
    onOrder: () -> Unit
) {

    Column {

        Text(
            text = "장바구니",

            fontSize =
                if (isSeniorMode)
                    22.sp
                else
                    16.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (cart.isEmpty()) {

            Text(
                text = "선택한 메뉴가 없습니다."
            )
        }

        cart.forEach {

            FastFoodCartRow(
                item = it,

                onIncrease = {
                    onIncrease(it)
                },

                onDecrease = {
                    onDecrease(it)
                }
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "총 금액 : ${totalPrice}원",

            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            OutlinedButton(
                modifier =
                    Modifier.weight(1f),

                onClick = onClearCart
            ) {

                Text("비우기")
            }

            Button(
                modifier =
                    Modifier.weight(2f),

                onClick = onOrder
            ) {

                Text("주문하기")
            }
        }
    }
}
@Composable
fun FastFoodCartRow(
    item: FastFoodCartItem,

    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    "${item.burger} x${item.quantity}"
            )

            if (item.isSet) {

                Text(
                    text =
                        "세트 / ${item.side} / ${item.drink}",

                    fontSize = 11.sp
                )
            }
        }

        Row {

            OutlinedButton(
                onClick = onDecrease
            ) {

                Text("-")
            }

            Spacer(
                modifier =
                    Modifier.width(4.dp)
            )

            OutlinedButton(
                onClick = onIncrease
            ) {

                Text("+")
            }
        }
    }
}
fun addFastFoodItemToCart(
    cart: List<FastFoodCartItem>,
    burger: String,
    isSet: Boolean,
    side: String,
    drink: String,
    price: Int
): List<FastFoodCartItem> {

    val existingItem =
        cart.find {

            it.burger == burger &&
                    it.isSet == isSet &&
                    it.side == side &&
                    it.drink == drink
        }

    return if (existingItem == null) {

        cart + FastFoodCartItem(
            burger = burger,
            isSet = isSet,
            side = side,
            drink = drink,
            quantity = 1,
            price = price
        )

    } else {

        cart.map {

            if (
                it.burger == burger &&
                it.isSet == isSet &&
                it.side == side &&
                it.drink == drink
            ) {

                it.copy(
                    quantity =
                        it.quantity + 1
                )

            } else {

                it
            }
        }
    }
}
fun increaseFastFoodItem(
    cart: List<FastFoodCartItem>,
    item: FastFoodCartItem
): List<FastFoodCartItem> {

    return cart.map {

        if (it == item) {

            it.copy(
                quantity =
                    it.quantity + 1
            )

        } else {

            it
        }
    }
}
fun decreaseFastFoodItem(
    cart: List<FastFoodCartItem>,
    item: FastFoodCartItem
): List<FastFoodCartItem> {

    return cart.mapNotNull {

        if (it == item) {

            if (it.quantity <= 1) {

                null

            } else {

                it.copy(
                    quantity =
                        it.quantity - 1
                )
            }

        } else {

            it
        }
    }
}
fun makeFastFoodOrderNumber(): String {

    val number =
        Random.nextInt(
            100,
            999
        )

    return "F-$number"
}