package io.github.mmauro94.common.navigation

interface NavigationDestination

@Suppress("TooManyFunctions")
data class StackData<T : NavigationDestination>(
    val stack: List<T> = emptyList(),
) {
    init {
        require(stack.isNotEmpty())
    }

    fun pop() = StackData(stack.dropLast(1))
    fun popOrNull() = if (canPop()) pop() else null
    fun pop(item: T): StackData<T> {
        return StackData(stack.minus(item))
    }

    fun tryPop(item: T): StackData<T> {
        return if (canPop(item)) {
            pop(item)
        } else {
            this
        }
    }

    fun replace(toPop: T, toPush: T): StackData<T> {
        return StackData(
            stack.map {
                when (it) {
                    toPop -> toPush
                    else -> it
                }
            },
        )
    }

    fun push(item: T): StackData<T> {
        return StackData(stack.minus(item).plus(item))
    }

    fun popUntil(main: T): StackData<T> {
        val iof = stack.indexOf(main)
        require(iof >= 0)
        return StackData(stack.subList(0, iof + 1))
    }

    fun canPop(): Boolean {
        return stack.size > 1
    }

    fun canPop(item: T): Boolean {
        return contains(item) && canPop()
    }

    fun contains(item: T) = stack.contains(item)
    fun topOrNull() = stack.lastOrNull()
    fun bottomOrNull() = stack.firstOrNull()

    companion object {
        fun <T : NavigationDestination> of(home: T) = StackData(listOf(home))
    }
}

fun <T : NavigationDestination> List<Pair<T, ItemAnimatableState>>.visible(
    canSeeBehind: (T) -> Boolean,
): List<Pair<T, ItemAnimatableState>> {
    return drop(
        withIndex()
            .indexOfLast { (index, element) ->
                // Element on top is never considered opaque, so animations are smooth the in disappears
                index < size - 1 && !canSeeBehind(element.first) && !element.second.isAnimating
            }.coerceAtLeast(0),
    )
}
