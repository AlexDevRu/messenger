package com.example.chat.ui.base.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.chat.ui.validation.InputValidationError

@Composable
fun TextInputField(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    transformation: VisualTransformation = VisualTransformation.None,
    value: String = "",
    errors: List<InputValidationError>? = null,
    maxCount: Int = 20,
    enabled: Boolean = true,
    onValueChanged: (String) -> Unit = {}
) {

    val labelStr = stringResource(label)

    val errorStr: (InputValidationError) -> StringResWrapper = err@ {
        return@err when(it) {
            is InputValidationError.LessCharactersError -> StringResWrapper(R.string.small_length_validation, arrayOf(labelStr, 4))
            InputValidationError.NoNumbersError -> StringResWrapper(R.string.password_no_numbers_validation)
            InputValidationError.SameCaseError -> StringResWrapper(R.string.password_same_case_validation)
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
            end.linkTo(counterRef.start)
            width = Dimension.fillToConstraints
        }

        constrain(counterRef) {
            top.linkTo(fieldRef.bottom)
            end.linkTo(fieldRef.end)
        }
    }

    val errorIcon: @Composable () -> Unit = { Icon(
        painter = painterResource(id = R.drawable.ic_baseline_error_24),
        tint = Color.Red,
        contentDescription = ""
    ) }

    ConstraintLayout(constraintSet, modifier = modifier.padding( vertical = 8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if(it.length <= maxCount)
                    onValueChanged(it)
            },
            label = { Text(text = labelStr) },
            enabled = enabled,
            placeholder = { Text(text = stringResource(id = label)) },
            isError = !errors.isNullOrEmpty(),
            leadingIcon = leadingIcon,
            trailingIcon = if(errors.isNullOrEmpty()) trailingIcon else errorIcon,
            modifier = Modifier
                .fillMaxWidth()
                .layoutId("field"),
            visualTransformation = transformation,
        )
        Column(modifier = Modifier
            .padding(end = 10.dp)
            .layoutId("errors")
        ) {
            errors?.forEach {
                val stringRes = errorStr(it)
                Text(
                    text = stringResource(id = stringRes.stringRes, *(stringRes.formatArgs)),
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        Text(
            text = "${value.length} / $maxCount",
            modifier = Modifier.layoutId("counter")
        )
    }
}