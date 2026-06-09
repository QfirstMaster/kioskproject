package com.example.kioskproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizDetailScreen(
    navController: NavController,
    category: String
) {
    when (category) {
        "movie" -> {
            MovieQuizScreen(navController)
            return
        }

        "cafe" -> {
            CafeQuizScreen(navController)
            return
        }

        "restaurant" -> {
            RestaurantQuizScreen(navController)
            return
        }
    }
    // 기존 객관식 퀴즈 코드
    val (emoji, title) = when (category) {
        "fastfood" -> "🍔" to "패스트푸드 문제"
        else -> "🖥️" to "키오스크 문제"
    }

    // 현재 문제 번호 (0부터 시작)
    var currentQuestion by remember { mutableStateOf(0) }

    // 선택한 답 인덱스
    var selectedAnswer by remember { mutableStateOf(-1) }

    // 정답 확인 여부
    var isAnswered by remember { mutableStateOf(false) }

    // 임시 퀴즈 데이터 (나중에 카테고리별로 분리 예정)
    val questions = listOf(
        QuizQuestion(
            question = "키오스크에서 음식을 주문할 때 가장 먼저 해야 할 것은?",
            answers = listOf("결제하기", "메뉴 선택", "수량 선택", "영수증 받기"),
            correctIndex = 1
        ),
        QuizQuestion(
            question = "키오스크에서 주문을 취소하려면 어떤 버튼을 눌러야 할까요?",
            answers = listOf("확인", "다음", "취소/처음으로", "결제"),
            correctIndex = 2
        ),
        QuizQuestion(
            question = "포장 주문을 하려면 어떤 옵션을 선택해야 할까요?",
            answers = listOf("매장식사", "포장", "배달", "예약"),
            correctIndex = 1
        ),
        QuizQuestion(
            question = "카드로 결제할 때 키오스크에서 카드를 넣는 곳은?",
            answers = listOf("화면 위쪽", "화면 아래쪽", "옆면 카드 단말기", "뒷면"),
            correctIndex = 2
        ),
        QuizQuestion(
            question = "주문 완료 후 영수증이 필요하면 어떻게 해야 할까요?",
            answers = listOf("그냥 기다린다", "영수증 출력 버튼을 누른다", "직원에게 요청한다", "앱을 켠다"),
            correctIndex = 1
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$emoji $title") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 진행 상황 표시 (예: 1 / 5)
            Text(
                text = "${currentQuestion + 1} / ${questions.size}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            // 진행 바
            LinearProgressIndicator(
                progress = { (currentQuestion + 1).toFloat() / questions.size },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 문제 텍스트
            Text(
                text = questions[currentQuestion].question,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 답안 버튼 4개
            questions[currentQuestion].answers.forEachIndexed { index, answer ->
                val backgroundColor = when {
                    // 아직 답 안 선택
                    !isAnswered -> MaterialTheme.colorScheme.surfaceVariant
                    // 정답
                    index == questions[currentQuestion].correctIndex -> Color(0xFF4CAF50)
                    // 내가 선택한 오답
                    index == selectedAnswer -> Color(0xFFE53935)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                Card(
                    onClick = {
                        if (!isAnswered) {
                            selectedAnswer = index
                            isAnswered = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor)
                ) {
                    Text(
                        text = "${index + 1}. $answer",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 다음 문제 or 완료 버튼
            if (isAnswered) {
                Button(
                    onClick = {
                        if (currentQuestion < questions.size - 1) {
                            // 다음 문제로 이동
                            currentQuestion++
                            selectedAnswer = -1
                            isAnswered = false
                        } else {
                            // 마지막 문제면 뒤로가기
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (currentQuestion < questions.size - 1) "다음 문제 →" else "완료!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// 퀴즈 문제 데이터 클래스
data class QuizQuestion(
    val question: String,       // 문제
    val answers: List<String>,  // 답안 4개
    val correctIndex: Int       // 정답 인덱스
)