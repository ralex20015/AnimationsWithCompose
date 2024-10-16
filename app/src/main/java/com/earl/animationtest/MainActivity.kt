package com.earl.animationtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntOffset
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.earl.animationtest.ui.theme.AnimationTestTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimationTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

enum class BoxState {
    Inactive,
    GoingUp,
}

//val path = remember {
//    Path().apply {
//        moveTo(0f, 0f)
//        cubicTo(0.05f, 0f, 0.133333f, 0.06f, 0.166666f, 0.12f)
//        cubicTo(0.208333f, 0.82f, 0.25f, 1f, 1f, 1f)
//    }
//}
//val easing = PathEasing(path)
//Al contenedor de le aplican dos cambios
@Composable
fun MainContent(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val animationTime = 1000
    val fadedAnimationTime = 500
    val totalTime = animationTime + fadedAnimationTime
    var fadeOutAppBar by remember { mutableStateOf(true) }
    var fadeInAppBar by remember { mutableStateOf(false) }
    //Puedo tener mil estados
    var currentState by remember { mutableStateOf(BoxState.Inactive) }
    //Tu Cuenta Transition
    val transition = updateTransition(targetState = currentState, label = "box state")
    val amountTransition = updateTransition(targetState = currentState, label = "amount transition")
    val totalHeight = 200.dp //100%

//    val alphaAnimation = animate
    //Tengo que dejar 5% de espacio
    LaunchedEffect(currentState) {
        if (currentState == BoxState.GoingUp) {
            coroutineScope.launch {
                delay(600)
                fadeOutAppBar = false
                delay(400)
                fadeInAppBar = true
            }
        } else {
            coroutineScope.launch {
                delay(600)
                fadeInAppBar = false
                delay(400)
                fadeOutAppBar = true
            }
        }
    }

    val amountPosition by transition.animateIntOffset(label = "translateAmount", transitionSpec = {
        tween(durationMillis = totalTime)
    }) { state ->
        when (state) {
            BoxState.Inactive -> IntOffset(0, 0)
            //Hasta aqui son 90
            BoxState.GoingUp -> IntOffset(0, -200)//45%

//            BoxState.GoingUp -> IntOffset(0, -90)//45%
        }
    }
    val positionYourAccount by transition.animateIntOffset(
        label = "translate",
        transitionSpec = {
            tween(
                durationMillis = totalTime,
//                easing = easing
            )
        }) { state ->
        when (state) {
            BoxState.Inactive -> IntOffset(0, 0)
            //Hasta aqui son 90
            BoxState.GoingUp -> IntOffset(0, -115)//45%

//            BoxState.GoingUp -> IntOffset(0, -90)//45%
        }
    }

    val color by transition.animateColor(
        transitionSpec = { tween(durationMillis = animationTime) },
        label = "color"
    ) { state ->
        when (state) {
            BoxState.Inactive -> Color.Gray
            BoxState.GoingUp -> Color.White
        }
    }

    //Animatable with  animateTo() called in with different timings
    // (using suspend functions)

    ConstraintLayout(modifier = modifier) {
        val (appbarRef, yourAccountRef, textAmountRef, btnRef) = createRefs()
        //El contenedor es el que cambiaria el background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .constrainAs(appbarRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
        ) {
            AnimatedVisibility(
                visible = fadeOutAppBar,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(animationSpec = tween(400, easing = LinearEasing)),
                exit = fadeOut(animationSpec = tween(400, easing = LinearEasing))
            ) {
//                .background(if(!fadeOutAppBar) Color(0xFF0066DD) else Color.White)
                AppBar1()
            }
            AnimatedVisibility(
                visible = fadeInAppBar,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(animationSpec = tween(400, easing = LinearEasing)),
                exit = fadeOut(animationSpec = tween(400, easing = LinearEasing))
            ) {
//                .background(if(!fadeOutAppBar) Color(0xFF0066DD) else Color.White)
                AppBar2()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight)
                .constrainAs(yourAccountRef) {
                    top.linkTo(appbarRef.bottom)
                    start.linkTo(parent.start)
                }
        ) {
            //Cuando llegue ahi el otro texto va a empezar a desaparecer
            Text(
                text = "Tu cuenta",
                color = color,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(positionYourAccount.x.dp, positionYourAccount.y.dp)
            )
        }

        //Amount
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeight)
                .constrainAs(textAmountRef) {
                    top.linkTo(yourAccountRef.bottom)
                    start.linkTo(parent.start)
                }
        ) {

            Text(
                text = "Another text 3",
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(amountPosition.x.dp, amountPosition.y.dp)
            )
        }

        Button(onClick = {
            currentState = when (currentState) {
                BoxState.Inactive -> BoxState.GoingUp
                BoxState.GoingUp -> BoxState.Inactive
            }
        },
            modifier
                .fillMaxWidth()
                .constrainAs(btnRef) {
                    start.linkTo(parent.start)
                    top.linkTo(textAmountRef.bottom)
                }) {
            Text("CLICK ME")
        }
    }
}

@Composable
private fun AppBar1() {
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center) {
        Text(
            text = "SomeText",
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun AppBar2() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0066DD)),
        horizontalArrangement = Arrangement.Start
    ) {
//        Text(
//            text = "SomeText",
//            textAlign = TextAlign.Center,
//            color = Color.Black,
//            modifier = Modifier
//                .align(Alignment.CenterVertically)
//        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Back",
            tint = Color.White
        )
    }
}

@Composable
fun Int.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainContentPreview() {
    MainContent()
}
