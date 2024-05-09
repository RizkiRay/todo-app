package com.example.todoapp.utils

import androidx.lifecycle.SavedStateHandle
import kotlin.reflect.KProperty

class StateDelegate<T>(
    private val state: SavedStateHandle,
    defaultValue: T
) {
    var value: T = defaultValue

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return state.get<T>(property.name) ?: value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        state.set(property.name, value)
        this.value = value
    }
}