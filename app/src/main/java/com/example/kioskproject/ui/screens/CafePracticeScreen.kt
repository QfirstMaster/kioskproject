package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kioskproject.viewmodel.SettingsViewModel
import kotlin.random.Random

// 카페 메뉴 정보
data class CafeMenuItem(
    val name: String,
    val price: Int,
    val emoji: String,
    val category: String
)

// 카페 장바구니 정보
data class CafeCartItem(
    val menu: CafeMenuItem,
    val quantity: Int,
    val size: String,
    val ice: String
)

private val cafeMenuList = listOf(

    // 커피
    CafeMenuItem(
        "아메리카노",
        2500,
        "☕",
        "coffee"
    ),

    CafeMenuItem(
        "카페라떼",
        3500,
        "🥛",
        "coffee"
    ),

    CafeMenuItem(
        "바닐라라떼",
        4000,
        "🍦",
        "coffee"
    ),

    // 에이드
    CafeMenuItem(
        "레몬에이드",
        4500,
        "🍋",
        "ade"
    ),

    CafeMenuItem(
        "청포도에이드",
        4500,
        "🍇",
        "ade"
    ),

    // 스무디
    CafeMenuItem(
        "딸기스무디",
        5000,
        "🍓",
        "smoothie"
    ),

    CafeMenuItem(
        "망고스무디",
        5000,
        "🥭",
        "smoothie"
    ),

    // 디저트
    CafeMenuItem(
        "치즈케이크",
        4500,
        "🍰",
        "dessert"
    ),

    CafeMenuItem(
        "초코케이크",
        4500,
        "🍫",
        "dessert"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafePracticeScreen(

    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {

    // 설정에서 글씨 크기 가져오기
    val fontSizeMode by settingsViewModel.fontSize.collectAsState()

    // 어르신 모드 여부
    val isSeniorMode = fontSizeMode == "senior"

    // 현재 카테고리
    var selectedCategory by remember {
        mutableStateOf("coffee")
    }

    // 장바구니
    var cart by remember {
        mutableStateOf(listOf<CafeCartItem>())
    }

    // 사이즈 선택 Dialog
    var showSizeDialog by remember {
        mutableStateOf(false)
    }

    // 얼음 선택 Dialog
    var showIceDialog by remember {
        mutableStateOf(false)
    }

    // 현재 선택 상품
    var selectedMenu by remember {
        mutableStateOf<CafeMenuItem?>(null)
    }

    // 선택 사이즈
    var selectedSize by remember {
        mutableStateOf("M")
    }

    // 선택 얼음
    var selectedIce by remember {
        mutableStateOf("보통")
    }

    // 결제 단계
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

    // 현금 투입액
    var insertedCash by remember {
        mutableStateOf(0)
    }

    // 카테고리별 메뉴
    val filteredMenus =
        cafeMenuList.filter {
            it.category == selectedCategory
        }

    // 총 금액
    val totalPrice =
        cart.sumOf {
            it.menu.price * it.quantity
        }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {

                    Text(
                        text = "한국 카페",
                        fontSize =
                            if (isSeniorMode)
                                25.sp
                            else
                                18.sp,

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
                .padding(
                    if (isSeniorMode)
                        20.dp
                    else
                        12.dp
                ),

            verticalArrangement = Arrangement.spacedBy(
                if (isSeniorMode)
                    14.dp
                else
                    10.dp
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

            // 카테고리 탭
            CafeCategoryTabs(
                selectedCategory = selectedCategory,
                isSeniorMode = isSeniorMode,
                onCategorySelected = {
                    selectedCategory = it
                }
            )

            // 상품 목록
            LazyVerticalGrid(
                columns = GridCells.Fixed(
                    if (isSeniorMode) 2 else 3
                ),

                modifier = Modifier.weight(1f),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                items(filteredMenus) { item ->

                    CafeMenuCard(
                        item = item,
                        isSeniorMode = isSeniorMode,
                        onClick = {

                            selectedMenu = item

                            when (item.category) {

                                // 커피, 에이드
                                "coffee",
                                "ade" -> {

                                    showSizeDialog = true
                                }

                                // 스무디
                                "smoothie" -> {

                                    showSizeDialog = true
                                }

                                // 디저트
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

                    listOf(
                        "S",
                        "M",
                        "L"
                    ).forEach { size ->

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),

                            onClick = {

                                selectedSize = size

                                showSizeDialog = false

                                when (selectedMenu?.category) {

                                    // 커피, 에이드
                                    "coffee",
                                    "ade" -> {

                                        showIceDialog = true
                                    }

                                    // 스무디
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

                            Text(
                                text = size
                            )
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

                    listOf(
                        "적게",
                        "보통",
                        "많이"
                    ).forEach { ice ->

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),

                            onClick = {

                                selectedIce = ice

                                selectedMenu?.let { menu ->

                                    cart =
                                        addCafeItemToCart(
                                            cart,
                                            menu,
                                            selectedSize,
                                            selectedIce
                                        )
                                }

                                showIceDialog = false
                            }
                        ) {

                            Text(
                                text = ice
                            )
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
                Text(
                    "포인트를 적립하시겠습니까?"
                )
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

                orderNumber = makeCafeOrderNumber()

                orderStep = 4
            },

            onCash = {

                paymentMethod = "현금결제"

                insertedCash = 0

                orderNumber = makeCafeOrderNumber()

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

                selectedCategory = "coffee"

                orderStep = 0

                phoneNumber = ""

                pointSaved = false

                paymentMethod = ""

                insertedCash = 0

                orderNumber = ""
            }
        )
    }
}

@Composable
fun CafeCategoryTabs(
    selectedCategory: String,
    isSeniorMode: Boolean,
    onCategorySelected: (String) -> Unit
) {

    val categories = listOf(
        "coffee" to "커피",
        "ade" to "에이드",
        "smoothie" to "스무디",
        "dessert" to "디저트"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    onCategorySelected(category.first)
                }
            ) {

                Text(
                    text = category.second,
                    fontSize =
                        if (isSeniorMode)
                            18.sp
                        else
                            13.sp,

                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CafeMenuCard(
    item: CafeMenuItem,
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

        shape = RoundedCornerShape(
            if (isSeniorMode)
                20.dp
            else
                16.dp
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    if (isSeniorMode)
                        14.dp
                    else
                        10.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = item.emoji,

                fontSize =
                    if (isSeniorMode)
                        52.sp
                    else
                        40.sp
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = item.name,

                    fontSize =
                        if (isSeniorMode)
                            18.sp
                        else
                            13.sp,

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "${item.price}원",

                    fontSize =
                        if (isSeniorMode)
                            16.sp
                        else
                            12.sp,

                    color = MaterialTheme.colorScheme.primary,

                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),

                onClick = onClick
            ) {
                Text(
                    text = "담기",

                    fontSize =
                        if (isSeniorMode)
                            16.sp
                        else
                            12.sp
                )
            }
        }
    }
}
@Composable
fun CafeCartSection(
    cart: List<CafeCartItem>,
    totalPrice: Int,
    isSeniorMode: Boolean,

    onIncrease: (CafeCartItem) -> Unit,
    onDecrease: (CafeCartItem) -> Unit,

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

            CafeCartRow(
                item = it,
                isSeniorMode = isSeniorMode,

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
            text = "총 금액 : ${totalPrice}원",

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedButton(
                modifier = Modifier.weight(1f),

                onClick = onClearCart
            ) {

                Text("비우기")
            }

            Button(
                modifier = Modifier.weight(2f),

                onClick = onOrder
            ) {

                Text("주문하기")
            }
        }
    }
}
@Composable
fun CafeCartRow(
    item: CafeCartItem,
    isSeniorMode: Boolean,

    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text =
                    "${item.menu.name} x${item.quantity}"
            )

            when (item.menu.category) {

                "coffee",
                "ade" -> {

                    Text(
                        text =
                            "${item.size} / 얼음 ${item.ice}",

                        fontSize =
                            if (isSeniorMode)
                                14.sp
                            else
                                11.sp
                    )
                }

                "smoothie" -> {

                    Text(
                        text =
                            "${item.size}",

                        fontSize =
                            if (isSeniorMode)
                                14.sp
                            else
                                11.sp
                    )
                }
            }
        }

        Row {

            OutlinedButton(
                onClick = onDecrease
            ) {

                Text("-")
            }

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            OutlinedButton(
                onClick = onIncrease
            ) {

                Text("+")
            }
        }
    }
}
fun addCafeItemToCart(
    cart: List<CafeCartItem>,
    menu: CafeMenuItem,
    size: String,
    ice: String
): List<CafeCartItem> {

    val existingItem =
        cart.find {

            it.menu.name == menu.name &&
                    it.size == size &&
                    it.ice == ice
        }

    return if (existingItem == null) {

        cart + CafeCartItem(
            menu = menu,
            quantity = 1,
            size = size,
            ice = ice
        )

    } else {

        cart.map {

            if (
                it.menu.name == menu.name &&
                it.size == size &&
                it.ice == ice
            ) {

                it.copy(
                    quantity = it.quantity + 1
                )

            } else {

                it
            }
        }
    }
}
fun removeCafeItemFromCart(
    cart: List<CafeCartItem>,
    item: CafeCartItem
): List<CafeCartItem> {

    return cart.mapNotNull {

        if (
            it.menu.name == item.menu.name &&
            it.size == item.size &&
            it.ice == item.ice
        ) {

            if (it.quantity <= 1) {

                null

            } else {

                it.copy(
                    quantity = it.quantity - 1
                )
            }

        } else {

            it
        }
    }
}
fun makeCafeOrderNumber(): String {

    val number =
        Random.nextInt(
            100,
            999
        )

    return "C-$number"
}