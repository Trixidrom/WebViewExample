package com.example.webviewexample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/*
В этом примере показано, как создать собственный диалог в Android Jetpack в android.

*  Button        : https://www.boltuix.com/2021/12/button_25.html
*  Clip Modifier : https://www.boltuix.com/2021/12/clip-modifier_24.html
*  Alert Dialog  : https://www.boltuix.com/2021/12/alert-dialog_25.html
*  Column        : https://www.boltuix.com/2021/12/column-layout_25.html
*  Box           : https://www.boltuix.com/2021/12/box-layout_25.html
*  Type.kt       : https://www.boltuix.com/2021/12/typography_27.html
*  Color.kt      : https://www.boltuix.com/2022/05/google-material-design-color.html
* */

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InfoDialog(
    title: String?="Message",
    desc: String?="Your Message",
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    color = Color.Transparent,
                )
        ) {


            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.onPrimary,
                        shape = RoundedCornerShape(25.dp, 5.dp, 25.dp, 5.dp)
                    )
                    .align(Alignment.BottomCenter),
            ) {

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth(),

                    )
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    //.........................Spacer
                    Spacer(modifier = Modifier.height(24.dp))

                    //.........................Text: title
                    Text(
                        text = title!!,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 130.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    //.........................Text : description
                    Text(
                        text = desc!!,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    //.........................Button : OK button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors= ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.primary),
                        //.clip(RoundedCornerShape(25.dp))
                    ) {
                        Text(
                            text = "OK",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onPrimary,
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                }


            }

        }
    }
}


/*
<img
align="center"
src="https://www.tekheist.com/assets/img/favicon30f4.png" alt="Tek Heist"

onclick="showAndroidToast("We are at the forefront of innovation. Discover with us the possibilities of your next project")" />
* */