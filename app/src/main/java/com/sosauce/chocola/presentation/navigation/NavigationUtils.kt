package com.sosauce.chocola.presentation.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun NavBackStack<NavKey>.navigateBack() {
    if (size == 1) return
    removeLastOrNull()
}

fun NavBackStack<NavKey>.navigate(element: NavKey) {
    remove(element)
    add(element)
}