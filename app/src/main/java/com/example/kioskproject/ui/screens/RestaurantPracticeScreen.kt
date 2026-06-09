package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kioskproject.viewmodel.SettingsViewModel
import androidx.compose.foundation.clickable

data class RestaurantMenuItem(
    val name: String,
    val price: Int,
    val emoji: String,
    val category: String
)

data class RestaurantCartItem(
    val menu: RestaurantMenuItem,
    val quantity: Int
)

val restaurantMenuList = listOf(

    RestaurantMenuItem("알리오올리오",18000,"🍝","pasta"),
    RestaurantMenuItem("까르보나라",20000,"🍝","pasta"),
    RestaurantMenuItem("뽀모도로",22000,"🍝","pasta"),

    RestaurantMenuItem("안심스테이크",32000,"🥩","steak"),
    RestaurantMenuItem("채끝스테이크",35000,"🥩","steak"),

    RestaurantMenuItem("마르게리타",22000,"🍕","pizza"),
    RestaurantMenuItem("고르곤졸라",24000,"🍕","pizza"),

    RestaurantMenuItem("콜라",3000,"🥤","drink"),
    RestaurantMenuItem("사이다",3000,"🥤","drink")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantPracticeScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = viewModel()
) {

    val fontSizeMode by settingsViewModel.fontSize.collectAsState()
    val isSeniorMode = fontSizeMode == "senior"

    var selectedCategory by remember {
        mutableStateOf("pasta")
    }

    var cart by remember {
        mutableStateOf(
            listOf<RestaurantCartItem>()
        )
    }

    var orderHistory by remember {
        mutableStateOf(
            listOf<RestaurantCartItem>()
        )
    }

    var showStaffDialog by remember {
        mutableStateOf(false)
    }

    val filteredMenus =
        restaurantMenuList.filter {
            it.category == selectedCategory
        }

    val totalPrice =
        cart.sumOf {
            it.menu.price * it.quantity
        }
    var showStaffCompleteDialog by remember {
        mutableStateOf(false)
    }

    var staffRequestText by remember {
        mutableStateOf("")
    }
    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "한국 음식점",
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
                            contentDescription = null
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
                        showStaffDialog = true
                    }
                )

                RestaurantMenuPanel(
                    menus = filteredMenus,
                    isSeniorMode = isSeniorMode,

                    onAddToCart = { menu ->

                        cart =
                            addRestaurantItemToCart(
                                cart,
                                menu
                            )
                    }
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        if (isSeniorMode)
                            260.dp
                        else
                            220.dp
                    )
            ) {

                OrderHistorySection(
                    history = orderHistory,
                    modifier = Modifier.weight(4f)
                )

                VerticalDivider()

                RestaurantCartSection(
                    cart = cart,
                    totalPrice = totalPrice,
                    isSeniorMode = isSeniorMode,

                    onIncrease = { item ->
                        cart =
                            increaseRestaurantItem(
                                cart,
                                item
                            )
                    },

                    onDecrease = { item ->
                        cart =
                            decreaseRestaurantItem(
                                cart,
                                item
                            )
                    },

                    onClearCart = {
                        cart = emptyList()
                    },

                    onOrder = {

                        orderHistory =
                            orderHistory + cart

                        cart = emptyList()
                    },

                    modifier = Modifier.weight(6f)
                )
            }
        }

        if (showStaffDialog) {

            var water by remember { mutableStateOf(false) }
            var spoon by remember { mutableStateOf(false) }
            var chopsticks by remember { mutableStateOf(false) }
            var napkin by remember { mutableStateOf(false) }
            var wetTissue by remember { mutableStateOf(false) }
            var plate by remember { mutableStateOf(false) }

            AlertDialog(

                onDismissRequest = {
                    showStaffDialog = false
                },

                title = {
                    Text("직원 호출")
                },

                text = {

                    Column {

                        Text(
                            "필요한 물품을 선택하세요"
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = water,
                                onCheckedChange = {
                                    water = it
                                }
                            )
                            Text("물")
                        }

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = spoon,
                                onCheckedChange = {
                                    spoon = it
                                }
                            )
                            Text("숟가락")
                        }

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = chopsticks,
                                onCheckedChange = {
                                    chopsticks = it
                                }
                            )
                            Text("젓가락")
                        }

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = napkin,
                                onCheckedChange = {
                                    napkin = it
                                }
                            )
                            Text("냅킨")
                        }

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = wetTissue,
                                onCheckedChange = {
                                    wetTissue = it
                                }
                            )
                            Text("물티슈")
                        }

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = plate,
                                onCheckedChange = {
                                    plate = it
                                }
                            )
                            Text("앞접시")
                        }
                    }
                },

                confirmButton = {

                    Button(
                        onClick = {

                            val requestList = mutableListOf<String>()

                            if (water) requestList.add("물")
                            if (spoon) requestList.add("숟가락")
                            if (chopsticks) requestList.add("젓가락")
                            if (napkin) requestList.add("냅킨")
                            if (wetTissue) requestList.add("물티슈")
                            if (plate) requestList.add("앞접시")

                            staffRequestText =
                                requestList.joinToString("\n")

                            showStaffDialog = false
                            showStaffCompleteDialog = true
                        }
                    ) {
                        Text("호출하기")
                    }
                },

                dismissButton = {

                    OutlinedButton(
                        onClick = {
                            showStaffDialog = false
                        }
                    ) {
                        Text("취소")
                    }
                }

            )

        }
        if (showStaffCompleteDialog) {

            AlertDialog(

                onDismissRequest = {
                    showStaffCompleteDialog = false
                },

                title = {
                    Text("직원 호출 완료")
                },

                text = {

                    Column {

                        Text("요청사항")

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(staffRequestText)

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            "직원이 곧 방문합니다."
                        )
                    }
                },

                confirmButton = {

                    Button(
                        onClick = {
                            showStaffCompleteDialog = false
                        }
                    ) {
                        Text("확인")
                    }
                }
            )
        }
    }
}
@Composable
fun RestaurantCategoryPanel(
    selectedCategory: String,
    isSeniorMode: Boolean,
    onCategorySelected: (String) -> Unit,
    onStaffCall: () -> Unit
) {

    val categories = listOf(
        "pasta" to "🍝 파스타",
        "steak" to "🥩 스테이크",
        "pizza" to "🍕 피자",
        "drink" to "🥤 음료"
    )


    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(
                if (isSeniorMode)
                    150.dp
                else
                    120.dp
            )
            .padding(8.dp)
    ) {

        categories.forEach { category ->

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .height(
                        if (isSeniorMode)
                            70.dp
                        else
                            55.dp
                    ),

                onClick = {
                    onCategorySelected(category.first)
                },

                colors = ButtonDefaults.buttonColors(
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
                            13.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    if (isSeniorMode)
                        80.dp
                    else
                        60.dp
                ),

            onClick = onStaffCall
        ) {

            Text(
                text = "직원호출",
                fontSize =
                    if (isSeniorMode)
                        18.sp
                    else
                        14.sp
            )
        }
    }
}
@Composable
fun RestaurantMenuPanel(
    menus: List<RestaurantMenuItem>,
    isSeniorMode: Boolean,
    onAddToCart: (RestaurantMenuItem) -> Unit
) {

    LazyVerticalGrid(

        columns = GridCells.Fixed(
            if (isSeniorMode)
                2
            else
                3
        ),

        modifier = Modifier
            .fillMaxHeight()
            .padding(8.dp),

        horizontalArrangement =
            Arrangement.spacedBy(8.dp),

        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        items(menus) { menu ->

            RestaurantMenuCard(
                menu = menu,
                isSeniorMode = isSeniorMode,

                onAddToCart = {
                    onAddToCart(menu)
                }
            )
        }
    }
}
@Composable
fun RestaurantMenuCard(
    menu: RestaurantMenuItem,
    isSeniorMode: Boolean,
    onAddToCart: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                if (isSeniorMode)
                    240.dp
                else
                    190.dp
            )
            .clickable {
                onAddToCart()
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = menu.emoji,
                fontSize =
                    if (isSeniorMode)
                        60.sp
                    else
                        42.sp
            )

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text = menu.name,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${menu.price}원",
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
@Composable
fun OrderHistorySection(
    history: List<RestaurantCartItem>,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {

        Text(
            text = "주문내역",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (history.isEmpty()) {

            Text(
                text = "주문 내역 없음"
            )

        } else {

            history.forEach {

                Text(
                    text =
                        "${it.menu.name} x${it.quantity}"
                )
            }
        }
    }
}
@Composable
fun RestaurantCartSection(
    cart: List<RestaurantCartItem>,
    totalPrice: Int,
    isSeniorMode: Boolean,
    onIncrease: (RestaurantCartItem) -> Unit,
    onDecrease: (RestaurantCartItem) -> Unit,
    onClearCart: () -> Unit,
    onOrder: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp)
    ) {

        Text(
            text = "장바구니",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            cart.forEach { item ->

                RestaurantCartRow(
                    item = item,
                    isSeniorMode = isSeniorMode,

                    onIncrease = {
                        onIncrease(item)
                    },

                    onDecrease = {
                        onDecrease(item)
                    }
                )
            }
        }

        Text(
            text = "총 금액 : ${totalPrice}원",
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
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
fun RestaurantCartRow(
    item: RestaurantCartItem,
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

        Text(
            text =
                "${item.menu.name} x${item.quantity}",

            modifier = Modifier.weight(1f),

            fontSize =
                if (isSeniorMode)
                    16.sp
                else
                    12.sp
        )

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
fun addRestaurantItemToCart(
    cart: List<RestaurantCartItem>,
    menu: RestaurantMenuItem
): List<RestaurantCartItem> {

    val existingItem =
        cart.find {
            it.menu.name == menu.name
        }

    return if (existingItem == null) {

        cart + RestaurantCartItem(
            menu = menu,
            quantity = 1
        )

    } else {

        cart.map {

            if (it.menu.name == menu.name) {

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

fun increaseRestaurantItem(
    cart: List<RestaurantCartItem>,
    item: RestaurantCartItem
): List<RestaurantCartItem> {

    return cart.map {

        if (it.menu.name == item.menu.name) {

            it.copy(
                quantity =
                    it.quantity + 1
            )

        } else {

            it
        }
    }
}

fun decreaseRestaurantItem(
    cart: List<RestaurantCartItem>,
    item: RestaurantCartItem
): List<RestaurantCartItem> {

    return cart.mapNotNull {

        if (it.menu.name == item.menu.name) {

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