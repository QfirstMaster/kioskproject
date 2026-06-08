package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kioskproject.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random
import androidx.compose.material3.ButtonDefaults

// 영화관 매점 상품 정보
data class MovieMenuItem(
    val name: String,
    val price: Int,
    val emoji: String,
    val category: String
)

// 장바구니 항목
data class CartItem(
    val menu: MovieMenuItem,
    val quantity: Int
)

// 한국 영화관 매점 메뉴 데이터
private val movieMenuList = listOf(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviePracticeScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    // 설정 화면의 글씨 모드 가져오기
    val fontSizeMode by settingsViewModel.fontSize.collectAsState()

    // 어르신 모드 여부
    val isSeniorMode = fontSizeMode == "senior"

    // 선택된 상품 카테고리
    var selectedCategory by remember { mutableStateOf("combo") }

    // 장바구니 상태
    var cart by remember { mutableStateOf(listOf<CartItem>()) }

    // 주문 단계
    // 0 = 상품 선택, 1 = 포인트 적립 여부, 2 = 전화번호 입력,
    // 3 = 결제수단 선택, 4 = 카드 결제중, 5 = 현금 투입, 6 = 주문 완료
    var orderStep by remember { mutableStateOf(0) }

    // 전화번호 입력값
    var phoneNumber by remember { mutableStateOf("") }

    // 포인트 적립 여부
    var pointSaved by remember { mutableStateOf(false) }

    // 결제수단
    var paymentMethod by remember { mutableStateOf("") }

    // 현금 투입 금액
    var insertedCash by remember { mutableStateOf(0) }

    // 주문번호
    var orderNumber by remember { mutableStateOf("") }

    // 현재 카테고리 상품만 보여주기
    val filteredMenus = movieMenuList.filter {
        it.category == selectedCategory
    }

    // 총 금액 계산
    val totalPrice = cart.sumOf {
        it.menu.price * it.quantity
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "한국 영화관 매점",
                        fontSize = if (isSeniorMode) 25.sp else 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
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
            Text(
                text = "원하는 상품을 선택하세요",
                fontSize = if (isSeniorMode) 22.sp else 16.sp,
                fontWeight = FontWeight.Bold
            )

            MovieCategoryTabs(
                selectedCategory = selectedCategory,
                isSeniorMode = isSeniorMode,
                onCategorySelected = {
                    selectedCategory = it
                }
            )

            // 상품 목록: 일반 3열, 어르신 모드 2열
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

        // 포인트 적립 여부 선택
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

        // 전화번호 키패드 입력
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

        // 카드 / 현금 결제 선택
        if (orderStep == 3) {
            PaymentChoiceDialog(
                totalPrice = totalPrice,
                isSeniorMode = isSeniorMode,
                onCard = {
                    paymentMethod = "카드결제"
                    orderNumber = makeOrderNumber()
                    orderStep = 4
                },
                onCash = {
                    paymentMethod = "현금결제"
                    insertedCash = 0
                    orderNumber = makeOrderNumber()
                    orderStep = 5
                }
            )
        }

        // 카드 결제 진행 화면
        if (orderStep == 4) {
            CardPaymentDialog(
                isSeniorMode = isSeniorMode,
                onPaymentComplete = {
                    orderStep = 6
                }
            )
        }

        // 현금 투입 화면
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

        // 주문 완료 화면
        if (orderStep == 6) {
            OrderCompleteDialog(
                orderNumber = orderNumber,
                paymentMethod = paymentMethod,
                pointSaved = pointSaved,
                phoneNumber = phoneNumber,
                isSeniorMode = isSeniorMode,
                onConfirm = {
                    // 주문 완료 후 전체 초기화
                    cart = emptyList()
                    selectedCategory = "combo"
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
}

// 카테고리 탭
@Composable
fun MovieCategoryTabs(
    selectedCategory: String,
    isSeniorMode: Boolean,
    onCategorySelected: (String) -> Unit
) {

    val categories = listOf(
        "combo" to "콤보",
        "popcorn" to "팝콘",
        "drink" to "음료",
        "snack" to "스낵"
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
                        if (isSeniorMode) 60.dp
                        else 48.dp
                    ),

                onClick = {
                    onCategorySelected(category.first)
                },

                colors = ButtonDefaults.buttonColors(

                    // 선택된 탭 강조
                    containerColor =
                        if (selectedCategory == category.first)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                )
            ) {

                Text(
                    text = category.second,

                    fontSize =
                        if (isSeniorMode)
                            18.sp
                        else
                            13.sp,

                    fontWeight = FontWeight.Bold,

                    color =
                        if (selectedCategory == category.first)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// 상품 카드
@Composable
fun MovieMenuCard(
    item: MovieMenuItem,
    isSeniorMode: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                if (isSeniorMode) 220.dp
                else 180.dp
            ),

        shape = RoundedCornerShape(
            if (isSeniorMode) 20.dp
            else 16.dp
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    if (isSeniorMode) 14.dp
                    else 10.dp
                ),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // 상품 이미지 자리
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

// 장바구니 영역
@Composable
fun CartSection(
    cart: List<CartItem>,
    totalPrice: Int,
    isSeniorMode: Boolean,
    onIncrease: (MovieMenuItem) -> Unit,
    onDecrease: (MovieMenuItem) -> Unit,
    onClearCart: () -> Unit,
    onOrder: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            if (isSeniorMode) 10.dp else 6.dp
        )
    ) {
        Text(
            text = "장바구니",
            fontSize = if (isSeniorMode) 22.sp else 16.sp,
            fontWeight = FontWeight.Bold
        )

        if (cart.isEmpty()) {
            Text(
                text = "선택한 상품이 없습니다.",
                fontSize = if (isSeniorMode) 17.sp else 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            cart.forEach { cartItem ->
                CartItemRow(
                    cartItem = cartItem,
                    isSeniorMode = isSeniorMode,
                    onIncrease = {
                        onIncrease(cartItem.menu)
                    },
                    onDecrease = {
                        onDecrease(cartItem.menu)
                    }
                )
            }
        }

        Text(
            text = "총 금액 : ${totalPrice}원",
            fontSize = if (isSeniorMode) 21.sp else 15.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onClearCart,
                modifier = Modifier.weight(1f),
                enabled = cart.isNotEmpty()
            ) {
                Text(
                    text = "비우기",
                    fontSize = if (isSeniorMode) 17.sp else 12.sp
                )
            }

            Button(
                onClick = onOrder,
                modifier = Modifier.weight(2f),
                enabled = cart.isNotEmpty()
            ) {
                Text(
                    text = "주문하기",
                    fontSize = if (isSeniorMode) 17.sp else 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 장바구니 한 줄
@Composable
fun CartItemRow(
    cartItem: CartItem,
    isSeniorMode: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${cartItem.menu.name} x${cartItem.quantity}",
            modifier = Modifier.weight(1f),
            fontSize = if (isSeniorMode) 17.sp else 12.sp,
            fontWeight = FontWeight.Medium
        )

        OutlinedButton(
            onClick = onDecrease,
            modifier = Modifier.height(if (isSeniorMode) 42.dp else 34.dp)
        ) {
            Text("-")
        }

        OutlinedButton(
            onClick = onIncrease,
            modifier = Modifier.height(if (isSeniorMode) 42.dp else 34.dp)
        ) {
            Text("+")
        }
    }
}

// 포인트 적립 여부
@Composable
fun PointChoiceDialog(
    isSeniorMode: Boolean,
    onSavePoint: () -> Unit,
    onSkip: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = "포인트 적립",
                fontSize = if (isSeniorMode) 24.sp else 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "포인트를 적립하시겠습니까?",
                fontSize = if (isSeniorMode) 19.sp else 14.sp
            )
        },
        confirmButton = {
            Button(onClick = onSavePoint) {
                Text("적립하기")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onSkip) {
                Text("건너뛰기")
            }
        }
    )
}

// 전화번호 키패드
@Composable
fun PhoneNumberDialog(
    phoneNumber: String,
    isSeniorMode: Boolean,
    onNumberClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = "전화번호 입력",
                fontSize = if (isSeniorMode) 24.sp else 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = formatPhoneNumber(phoneNumber),
                    fontSize = if (isSeniorMode) 24.sp else 18.sp,
                    fontWeight = FontWeight.Bold
                )

                val keypadRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("←", "0", "확인")
                )

                keypadRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { key ->
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(if (isSeniorMode) 58.dp else 46.dp),
                                onClick = {
                                    when (key) {
                                        "←" -> onBackspace()
                                        "확인" -> onConfirm()
                                        else -> onNumberClick(key)
                                    }
                                }
                            ) {
                                Text(
                                    text = key,
                                    fontSize = if (isSeniorMode) 18.sp else 14.sp
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "11자리 입력 후 확인을 누르세요.",
                    fontSize = if (isSeniorMode) 15.sp else 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text("건너뛰기")
            }
        }
    )
}

// 결제수단 선택
@Composable
fun PaymentChoiceDialog(
    totalPrice: Int,
    isSeniorMode: Boolean,
    onCard: () -> Unit,
    onCash: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = "결제수단 선택",
                fontSize = if (isSeniorMode) 24.sp else 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "결제 금액 : ${totalPrice}원",
                    fontSize = if (isSeniorMode) 20.sp else 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onCard
                ) {
                    Text("💳 카드결제")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onCash
                ) {
                    Text("💵 현금결제")
                }
            }
        },
        confirmButton = {}
    )
}

// 카드 결제 진행
@Composable
fun CardPaymentDialog(
    isSeniorMode: Boolean,
    onPaymentComplete: () -> Unit
) {
    // 화면이 뜨면 2.5초 후 자동 결제 완료
    LaunchedEffect(Unit) {
        delay(2500)
        onPaymentComplete()
    }

    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = "카드 결제",
                fontSize = if (isSeniorMode) 24.sp else 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "💳",
                    fontSize = if (isSeniorMode) 60.sp else 42.sp
                )

                Text(
                    text = "카드를 삽입해주세요.\n결제 처리 중입니다...",
                    fontSize = if (isSeniorMode) 19.sp else 14.sp
                )

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {}
    )
}

// 현금 결제 화면
@Composable
fun CashPaymentDialog(
    totalPrice: Int,
    insertedCash: Int,
    isSeniorMode: Boolean,
    onInsertCash: (Int) -> Unit,
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val remain = totalPrice - insertedCash

    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = "현금 결제",
                fontSize = if (isSeniorMode) 24.sp else 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("결제 금액 : ${totalPrice}원")
                Text("투입 금액 : ${insertedCash}원")

                Text(
                    text = if (remain > 0) {
                        "남은 금액 : ${remain}원"
                    } else {
                        "거스름돈 : ${-remain}원"
                    },
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(1000, 5000, 10000).forEach { cash ->
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onInsertCash(cash)
                            }
                        ) {
                            Text("${cash}원")
                        }
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = insertedCash >= totalPrice,
                    onClick = onComplete
                ) {
                    Text("결제 완료")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text("취소")
            }
        }
    )
}

// 주문 완료
@Composable
fun OrderCompleteDialog(
    orderNumber: String,
    paymentMethod: String,
    pointSaved: Boolean,
    phoneNumber: String,
    isSeniorMode: Boolean,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = "주문 완료",
                fontSize = if (isSeniorMode) 24.sp else 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("주문이 완료되었습니다 🎉")
                Text("주문번호 : $orderNumber")
                Text("결제수단 : $paymentMethod")

                if (pointSaved) {
                    Text("포인트 적립 완료")
                    Text("전화번호 : ${formatPhoneNumber(phoneNumber)}")
                } else {
                    Text("포인트 적립 안 함")
                }

                Text("예상 준비 시간 : 약 5분")
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("확인")
            }
        }
    )
}

// 장바구니에 상품 추가
fun addItemToCart(
    cart: List<CartItem>,
    item: MovieMenuItem
): List<CartItem> {
    val existingItem = cart.find {
        it.menu.name == item.name
    }

    return if (existingItem == null) {
        cart + CartItem(
            menu = item,
            quantity = 1
        )
    } else {
        cart.map {
            if (it.menu.name == item.name) {
                it.copy(quantity = it.quantity + 1)
            } else {
                it
            }
        }
    }
}

// 장바구니에서 상품 1개 감소
fun removeOneItemFromCart(
    cart: List<CartItem>,
    item: MovieMenuItem
): List<CartItem> {
    return cart.mapNotNull {
        if (it.menu.name == item.name) {
            if (it.quantity <= 1) {
                null
            } else {
                it.copy(quantity = it.quantity - 1)
            }
        } else {
            it
        }
    }
}

// 전화번호 보기 좋게 표시
fun formatPhoneNumber(phone: String): String {
    return when {
        phone.length <= 3 -> phone
        phone.length <= 7 -> phone.substring(0, 3) + "-" + phone.substring(3)
        else -> phone.substring(0, 3) + "-" + phone.substring(3, 7) + "-" + phone.substring(7)
    }
}

// 주문번호 생성
fun makeOrderNumber(): String {
    val number = Random.nextInt(100, 999)
    return "A-$number"
}