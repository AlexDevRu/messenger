package com.example.chat.ui.base.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.example.chat.R
import com.example.chat.ui.models.StringResWrapper
import com.example.chat.ui.validation.InputValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class TextFieldState(
    val value: String = "",
    val valueErrors: List<InputValidator> = emptyList(),
)

class TextFieldVM(
    private val validators: List<InputValidator> = emptyList(),
    val maxCount: Int? = null
) {

    private val _state = MutableStateFlow(TextFieldState())
    val state: StateFlow<TextFieldState> = _state

    val value: String get() = state.value.value

    private val _hasErrors = MutableStateFlow(false)
    val hasErrors: StateFlow<Boolean> = _hasErrors

    private val _hasErrorsOrEmpty = MutableStateFlow(true)
    val hasErrorsOrEmpty: StateFlow<Boolean> = _hasErrorsOrEmpty

    fun onValueChanged(newValue: String) {
        if(maxCount != null && newValue.length > maxCount)
            return

        _state.value = state.value.copy(value = newValue)

        val failedValidators = validators.filter {
            !it.validate(newValue)
        }

        _state.value = state.value.copy(
            valueErrors = failedValidators,
            value = newValue,
        )

        _hasErrors.value = failedValidators.isNotEmpty()
        _hasErrorsOrEmpty.value = hasErrors.value || newValue.isEmpty()
    }
}

@Composable
fun TextInputField(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    transformation: VisualTransformation = VisualTransformation.None,
    textFieldVM: TextFieldVM,
    enabled: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    maxLines: Int = 1
) {

    val labelStr = stringResource(label)

    val state by textFieldVM.state.collectAsState()

    val errorStr: (InputValidator) -> StringResWrapper = err@ {
        return@err when(it) {
            is InputValidator.LessCharactersValidator -> it.getMessage(labelStr)
            else -> it.getMessage()
        }
    }

    val constraintSet = ConstraintSet {
        val fieldRef = createRefFor("field")
        val errorsRef = createRefFor("errors")
        val counterRef = createRefFor("counter")

        constrain(fieldRef) {
            top.linkTo(parent.top)
        }

        constrain(errorsRef) {
            top.linkTo(fieldRef.bottom)
            start.linkTo(fieldRef.start)
            end.linkTo(if(textFieldVM.maxCount != null) counterRef.start else parent.end)
            width = Dimension.fillToConstraints
        }

        constrain(counterRef) {
            top.linkTo(fieldRef.bottom)
            end.linkTo(fieldRef.end)
        }
    }

    val errorIcon: @Composable () -> Unit = { Icon(
        painter = painterResource(id = R.drawable.ic_baseline_error_24),
        tint = MaterialTheme.colors.error,
        contentDescription = ""
    ) }

    ConstraintLayout(constraintSet, modifier = modifier.padding(vertical = 8.dp)) {
        OutlinedTextField(
            value = state.value,
            maxLines = maxLines,
            onValueChange = {
                textFieldVM.onValueChanged(it)
            },
            label = { Text(text = labelStr) },
            enabled = enabled,
            placeholder = { Text(text = stringResource(id = label)) },
            isError = !state.valueErrors.isNullOrEmpty(),
            leadingIcon = leadingIcon,
            trailingIcon = if(state.valueErrors.isNullOrEmpty()) trailingIcon else errorIcon,
            modifier = Modifier
                .fillMaxWidth()
                .layoutId("field"),
            visualTransformation = transformation,
            keyboardOptions = keyboardOptions,
        )
        Column(modifier = Modifier
            .padding(end = 10.dp)
            .layoutId("errors")
        ) {
            state.valueErrors.forEach {
                val stringRes = errorStr(it)
                Text(
                    text = stringResource(id = stringRes.stringRes, *(stringRes.formatArgs)),
                    color = MaterialTheme.colors.error,
                    fontSize = 12.sp
                )
            }
        }

        if(textFieldVM.maxCount != null) {
            Text(
                text = "${state.value.length} / ${textFieldVM.maxCount}",
                modifier = Modifier.layoutId("counter")
            )
        }
    }
}
