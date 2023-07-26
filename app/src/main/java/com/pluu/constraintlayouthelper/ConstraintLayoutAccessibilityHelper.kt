package com.pluu.constraintlayouthelper

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

class ConstraintLayoutAccessibilityHelper @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintHelper(context, attrs, defStyleAttr) {
    init {
        ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
        ViewCompat.setAccessibilityDelegate(this, AccessibilityDelegate())
    }

    override fun updatePreLayout(container: ConstraintLayout) {
        super.updatePreLayout(container)
        if (this.mReferenceIds != null) {
            this.setIds(this.mReferenceIds)
        }

        mIds.forEach {
            container.getViewById(it)?.importantForAccessibility =
                View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }
    }

    private inner class AccessibilityDelegate : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)
            val parent = parent as? ViewGroup ?: return
            info.text = generateText(parent)
        }

        private fun generateText(parent: ViewGroup): CharSequence {
            return mIds.asSequence()
                .mapNotNull { id -> parent.findViewById(id) }
                .mapNotNull { view ->
                    when {
                        view.contentDescription?.isNotEmpty() == true -> view.contentDescription
                        view is TextView -> view.text.takeIf { it.isNotEmpty() }
                        else -> ""
                    }
                }.joinToString(", ")
        }
    }
}