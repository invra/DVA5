package jb.dvacommon.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jb.common.ExceptionReporter
import jb.common.sound.Player
import jb.dvacommon.DVA
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import java.awt.Desktop
import java.awt.Dimension
import java.net.URI
import java.util.Properties
import javax.swing.JDialog
import javax.swing.SwingUtilities

class LoadWindow {
    private var statusText by mutableStateOf("")
    private var isVisible by mutableStateOf(false)
    private var showCloseButtonState by mutableStateOf(false)
    private var isFadingOut by mutableStateOf(false)

    private var dialog: JDialog? = null

    private val buildNumber: String by lazy {
        try {
            val props = Properties()
            LoadWindow::class.java.getResourceAsStream("/version.txt")?.use { props.load(it) }
            val build = props.getProperty("build.number") ?: ""
            if (build.isNotEmpty()) (10000 + build.toInt()).toString() else "DEV"
        } catch (_: Exception) {
            "DEV"
        }
    }

    fun setText(s: String) {
        SwingUtilities.invokeLater {
            statusText = if (s.isEmpty()) " " else s
        }
    }

    fun show(showCloseButton: Boolean, introSound: Boolean) {
        if (introSound) {
            playIntroSound()
        }

        showCloseButtonState = showCloseButton
        if (!showCloseButton) {
            statusText = ""
        }

        isFadingOut = false
        isVisible = true

        SwingUtilities.invokeLater {
            val width = 650
            val height = if (showCloseButton) 610 else 434

            val dialog = JDialog().apply {
                isUndecorated = true
                size = Dimension(width, height)
                setLocationRelativeTo(null)
                isResizable = false

                val composePanel = ComposePanel()
                composePanel.setContent {
                    IntUiTheme(isDark = true) {
                        Content()
                    }
                }
                add(composePanel)
            }

            this.dialog = dialog
            dialog.isVisible = true
        }
    }

    fun dispose() {
        SwingUtilities.invokeLater {
            isFadingOut = true
            isVisible = false
            dialog?.dispose()
            dialog = null
        }
    }

    private fun playIntroSound() {
        try {
            val startSoundList = listOfNotNull(
                LoadWindow::class.java.getResource("/start4-CHIME.mp3"),
                LoadWindow::class.java.getResource("/start4-D.f.mp3"),
                LoadWindow::class.java.getResource("/start4-V.f.mp3"),
                LoadWindow::class.java.getResource("/start4-A.f.mp3"),
            )
            val p = Player(startSoundList, null, DVA.getTemp())
            p.start()
        } catch (e: Exception) {
            ExceptionReporter.reportException(e)
        }
    }

    @Composable
    private fun Content() {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(350)),
            exit = fadeOut(animationSpec = tween(350))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(JewelTheme.globalColors.panelBackground)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(434.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                dialog?.let { d ->
                                    val currentLoc = d.location
                                    d.setLocation(
                                        currentLoc.x + dragAmount.x.toInt(),
                                        currentLoc.y + dragAmount.y.toInt()
                                    )
                                }
                            }
                        }
                ) {
                    Image(
                        painter = painterResource("splash_train.jpg"),
                        contentDescription = "Splash Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Text(
                        text = "DVA",
                        color = Color.White,
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Text(
                        text = statusText,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 12.dp, bottom = 12.dp)
                    )

                    if (!showCloseButtonState) {
                        Text(
                            text = "${DVA.VersionString} ($buildNumber)",
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 12.dp, bottom = 12.dp)
                        )
                    }
                }

                if (showCloseButtonState) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(JewelTheme.globalColors.panelBackground)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        AboutTextContent(buildNumber)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val licenseWindow = LicenseWindow()
                                    licenseWindow.showSubsequentTimes()
                                }
                            ) {
                                Text("License")
                            }

                            DefaultButton(onClick = { dispose() }) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AboutTextContent(buildNum: String) {
        val textColor = Color(0xFFDDDDDD)
        val mutedColor = Color(0xFFBBBBBB)
        val linkColor = Color(0xFF3582E1)

        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                append("DVA Version ${DVA.VersionString} Build $buildNum ")
            }
            withStyle(style = SpanStyle(color = mutedColor, fontSize = 12.sp)) {
                append("(Java ${System.getProperty("java.version")} ${System.getProperty("os.arch")})\n")
            }

            withStyle(style = SpanStyle(color = textColor, fontSize = 12.sp)) {
                append("${DVA.CopyrightMessage}\n")
                append("Contact: ")
            }

            pushStringAnnotation(tag = "URL", annotation = "mailto:jaboles@fastmail.fm")
            withStyle(style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline, fontSize = 12.sp)) {
                append("jaboles@fastmail.fm")
            }
            pop()

            withStyle(style = SpanStyle(color = textColor, fontSize = 12.sp)) {
                append(". GitHub: ")
            }

            pushStringAnnotation(tag = "URL", annotation = "https://github.com/jaboles/DVA5")
            withStyle(style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline, fontSize = 12.sp)) {
                append("https://github.com/jaboles/DVA5")
            }
            pop()

            withStyle(style = SpanStyle(color = textColor, fontSize = 12.sp)) {
                append("\nSounds copyright ©: Glenn Jackson-Bethell, Paul McCabe, Winston Yang, Ben Cousins, PKBeam, Charlie Munns.\n")
                append("Used with permission. Original site: ")
            }

            pushStringAnnotation(tag = "URL", annotation = "http://railwavs.railmedia.com.au")
            withStyle(style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline, fontSize = 12.sp)) {
                append("http://railwavs.railmedia.com.au")
            }
            pop()

            withStyle(style = SpanStyle(color = textColor, fontSize = 12.sp)) {
                append("\nOriginal splash screen design: Winston Yang")
            }
        }

        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        try {
                            Desktop.getDesktop().browse(URI.create(annotation.item))
                        } catch (e: Exception) {
                            ExceptionReporter.reportException(e)
                        }
                    }
            }
        )
    }
}
