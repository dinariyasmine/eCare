package com.example.pop_ups_confirmations_template.ui.popup

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.core.theme.Error600
import com.example.core.theme.Primary500
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.bold.Check
import com.adamglin.phosphoricons.bold.X


sealed class PopupState {
    abstract val icon: ImageVector
    abstract val iconColor: Color
    abstract val title: String
    abstract val description: String
    abstract val buttonText: String
    abstract val onAction: () -> Unit
    abstract val buttonColor: Color?

    data class Success(
        override val title: String,
        override val description: String,
        override val buttonText: String,
        override val onAction: () -> Unit,
        override val icon: ImageVector = PhosphorIcons.Bold.Check,
        override val iconColor: Color = Primary500,
        override val buttonColor: Color = Primary500,
    ) : PopupState()

    data class Error(
        override val title: String,
        override val description: String,
        override val buttonText: String,
        override val onAction: () -> Unit,
        override val icon: ImageVector = PhosphorIcons.Bold.X,
        override val iconColor: Color = Error600,
        override val buttonColor: Color? = Color.White
    ) : PopupState()

    companion object {
        fun createSuccessState(
            title: String,
            description: String,
            buttonText: String,
            onAction: () -> Unit
        ): Success {
            return Success(
                title = title,
                description = description,
                buttonText = buttonText,
                onAction = onAction
            )
        }

        fun createErrorState(
            title: String,
            description: String,
            buttonText: String,
            onAction: () -> Unit
        ): Error {
            return Error(
                title = title,
                description = description,
                buttonText = buttonText,
                onAction = onAction
            )
        }
    }
}