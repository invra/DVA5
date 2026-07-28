package jb.dvacommon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import java.awt.Dimension
import javax.swing.JDialog
import javax.swing.SwingUtilities

class LicenseWindow {
    var accepted: Boolean by mutableStateOf(false)
        private set

    private var isFirstTimeState by mutableStateOf(true)

    @JvmField
    var frame: JDialog? = null

    val dialog: JDialog? get() = frame

    fun showFirstTime() {
        accepted = false
        isFirstTimeState = true
        showDialog()
    }

    fun showSubsequentTimes() {
        accepted = false
        isFirstTimeState = false
        showDialog()
    }

    fun showLicenseIfNotRead() {
        showFirstTime()
    }

    fun accepted(): Boolean = accepted

    private fun showDialog() {
        val width = 640
        val height = 520

        val showAndDisplayDialog = Runnable {
            val dialogInstance = JDialog().apply {
                title = "License Agreement"
                isModal = true
                isResizable = true
                size = Dimension(width, height)
                preferredSize = Dimension(width, height)
                setLocationRelativeTo(null)

                val composePanel = ComposePanel()
                composePanel.setContent {
                    IntUiTheme(isDark = true) {
                        Content()
                    }
                }
                add(composePanel)
            }
            this.frame = dialogInstance
            dialogInstance.isVisible = true
        }

        if (SwingUtilities.isEventDispatchThread()) {
            showAndDisplayDialog.run()
        } else {
            SwingUtilities.invokeAndWait(showAndDisplayDialog)
        }
    }

    @Composable
    private fun Content() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(JewelTheme.globalColors.panelBackground)
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (isFirstTimeState) {
                    Text(
                        text = INSTRUCTIONS_TEXT,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                        .background(JewelTheme.globalColors.panelBackground)
                        .padding(10.dp)
                ) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        Text(
                            text = LICENSE_BODY_TEXT
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isFirstTimeState) {
                        DefaultButton(onClick = {
                            accepted = true
                            closeDialog()
                        }) {
                            Text("I Accept")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = {
                            accepted = false
                            closeDialog()
                        }) {
                            Text("Cancel")
                        }
                    } else {
                        DefaultButton(onClick = {
                            accepted = true
                            closeDialog()
                        }) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }

    private fun closeDialog() {
        frame?.isVisible = false
        frame?.dispose()
        frame = null
    }

    companion object {
        private const val INSTRUCTIONS_TEXT = "You must accept the following license terms in order to use DVA."

        private val LICENSE_BODY_TEXT = """
            License Agreement

            Copyright 1999 - 2025 © Jonathan Boles
            Sounds copyright © Glenn Jackson-Bethell, Paul McCabe, Winston Yang, Ben Cousins, PKBeam, and Charlie Munns.
            Sounds used with permission.

            You should carefully read the following terms and conditions before using this software. Unless you have a different license agreement signed by the author your use of this software indicates your acceptance of this license agreement and warranty. This license agreement covers any version or release of DVA.

            If you do not agree with and accept this license agreement, click Cancel now.

            DISCLAIMER OF WARRANTY
            This software and the accompanying files are supplied on an "as is" basis. There are no warranties as to performance or merchantability or any other warranties whether expressed or implied.

            The user assumes the entire risk of using the software, any liability of the author will be limited exclusively to product replacement.

            USE
            This is free software. Subject to the terms below, you are licensed to use this software without charge as you wish. The DVA software in distributed binary form, source code, and accompanying sound files are strictly not to be distributed. You may not reverse-engineer, disassemble, decompile, or alter DVA.

            DISTRIBUTION
            You are not licensed to make copies of the DVA software, source code and documentation.

            You are VERY STRICTLY prohibited from copying or otherwise breaching copyright on ANY sound files that are included with DVA. Copyright of these files remains with Glenn Jackson-Bethell, Paul McCabe, Winston Yang, Ben Cousins, PKBeam and Charlie Munns.

            One copy of the DVA software, including sound files is permitted, solely for backup purposes.

            GOVERNING LAW
            This agreement shall be governed by laws of Australia.
        """.trimIndent()
    }
}
