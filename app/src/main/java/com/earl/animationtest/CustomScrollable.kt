package com.earl.animationtest

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MyComposable() {
    val scrollState = rememberScrollState()
    val initialTopSectionHeight = 300.dp
    val minTopSectionHeight = 80.dp
    val topSectionHeight = remember { mutableStateOf(initialTopSectionHeight) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (topSection, imageSection, bottomSection) = createRefs()

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp)
                .background(Color.Gray)
                .constrainAs(imageSection) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(bottomSection.top)
                }
                .verticalScroll(scrollState)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val (topContent, image) = createRefs()

                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Image",
                    modifier = Modifier
                        .size(1300.dp)
//                        .background(Color.Gray)
                        .constrainAs(image) {
                            top.linkTo(topSection.bottom)
                            centerHorizontallyTo(parent)
                        }
                )
            }
        }

        Text(
            text = "Top Section",
            modifier = Modifier
                .height(topSectionHeight.value)
                .heightIn(min = minTopSectionHeight)
                .fillMaxWidth()
                .background(Color.Green)
                .constrainAs(topSection) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .constrainAs(bottomSection) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text("Button")
        }
    }


    val resources = LocalContext.current.resources

    LaunchedEffect(scrollState.value) {
        val scrollRange = initialTopSectionHeight - minTopSectionHeight
        val scrollPercentage = scrollState.value.toFloat() / scrollRange.toPx(resources)
        val newHeight = initialTopSectionHeight - (scrollPercentage * scrollRange.value).dp // Convert to Dp

        topSectionHeight.value = newHeight.coerceAtLeast(minTopSectionHeight)
    }
}

@Composable
private fun ImageSection(

){

}

fun Dp.toPx(resources: Resources): Float {
    return this.value * resources.displayMetrics.density
}

@Preview
@Composable
fun MyComposablePreview() {
    MyComposable()
}