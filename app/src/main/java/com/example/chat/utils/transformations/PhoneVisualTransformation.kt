package com.example.chat.utils.transformations

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneVisualTransformation: VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Make the string XX-XXX-XX-XX
        val trimmed = if (text.text.length >= 9) text.text.substring(0..8) else text.text
        var output = ""
        for (i in trimmed.indices) {
            output += trimmed[i]
            if(i == 1 || i == 4 || i == 6) output +="-"
        }


        /**
         * The offset works such that the translator ignores hyphens. Conversions
         * from original to transformed text works like this
        - 3rd character in the original text is the 4th in the transformed text
        - The 6th character in the original becomes the 8th
        In reverse, the conversion is such that
        - 10th Character in transformed becomes the 8th in original
        - 4th in transformed becomes 3rd in original
         */

        val phoneNumberTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                // [offset [0 - 2] remain the same]
                if (offset <= 1) return offset
                // [3 - 5] transformed to [4 - 6] respectively
                if (offset <= 4) return offset + 1
                // [6 - 8] transformed to [8 - 10] respectively
                if (offset <= 6) return offset + 2
                if (offset <= 8) return offset + 3
                return 12
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 5) return offset - 1
                if (offset <= 8) return offset - 2
                if (offset <= 11) return offset - 3
                return 9
            }
        }

        return TransformedText(
            AnnotatedString(output),
            phoneNumberTranslator)
    }
}