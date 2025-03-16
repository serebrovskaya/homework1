package com.example.myhw

fun showMenu() {
    println("Меню библиотеки:")
    println("1. Показать книги")
    println("2. Показать газеты")
    println("3. Показать диски")
    println("0. Выйти")
}

fun selectItem(items: List<LibraryItem>) {
    var continuehere: Int? = 1
    print("Выберите номер объекта: ")
    var selectedIndex = readlnOrNull()?.toIntOrNull()?.minus(1)
    while (continuehere == 1) {
        if (selectedIndex != null && selectedIndex in items.indices) {
            val selectedItem = items[selectedIndex]

            println("Вы выбрали: ${selectedItem.name}")
            println("Частное меню:")
            println("1. Взять домой")
            println("2. Читать в читальном зале")
            println("3. Показать подробную информацию")
            println("4. Вернуть")

            print("Выберите действие: ")
            val choice = readlnOrNull()?.toIntOrNull()
            when (choice) {
                1 -> {
                    if (selectedItem is TakableHome)
                        selectedItem.takeItem()
                    else println("Этот объект нельзя взять домой.")
                }

                2 -> {
                    if (selectedItem is Readable) {
                        selectedItem.readItem()
                        println("Информация об объекте: ")
                        selectedItem.printShortInfo()
                    } else println("Этот объект нельзя читать.")
                }

                3 -> printFullInfo(selectedItem)
                4 -> selectedItem.returnItem()
                else -> println("Неверный выбор.")
            }
            println("\nВернуться:")
            println("0 - Меню библиотеки, 1 - Частное меню для ${selectedItem.name}")
            continuehere = readlnOrNull()?.toIntOrNull()
        } else {
            print("Неверный номер объекта. Попробуйте выбрать номер ещё раз: ")
            selectedIndex = readlnOrNull()?.toIntOrNull()?.minus(1)
        }
    }
}

fun main() {

    val library = Library().apply {
        addItems(
            Book(1867, "Война и мир", true, 5202, "Лев Толстой"),
            Book(1866, "Преступление и наказание", false, 672, "Федор Достоевский"),
            Book(90743, "Маугли", true, 202, "Джозеф Киплинг"),

            Newspaper(303, "Комсомольская правда", true, 123),
            Newspaper(923, "Московский комсомолец", false, 456),
            Newspaper(17245, "Сельская жизнь", true, 794),

            Disc(1975, "Богемская рапсодия", true, "CD"),
            Disc(307, "Дэдпул и Росомаха", true, "DVD"),
            Disc(2001, "Voyage", false, "CD")
        )
    }

    while (true) {
        showMenu()
        lateinit var temp: List<LibraryItem>
        library.run {
            when (readlnOrNull()?.toIntOrNull()) {
                1 -> temp = getBooks()
                2 -> temp = getNewspapers()
                3 -> temp = getDiscs()
                0 -> {
                    println("Пока!")
                    return
                }
            }
            try {
                printShortInfo(temp)
                println("")
                selectItem(temp)
            } catch (e: UninitializedPropertyAccessException) {
                println("Неверный выбор. Попробуйте снова.\n")
            }
        }
    }
}

interface TakableHome {
    fun takeItem()
}

interface Returnable {
    fun returnItem()
}

interface Readable {
    val name: String
    var isAvailable: Boolean

    fun readItem() {
        if (isAvailable) {
            when (this) {
                is Book -> println("Книга '${name}' с id '${id}' теперь в читальном зале.")
                is Newspaper -> println("Газета '$name' теперь в читальном зале.")
                //else -> println("Неизвестный тип элемента")
            }
            isAvailable = false
        } else {
            println("'${name}' сейчас не доступен для чтения.")
        }
    }
}

class Book(
    id: Int,
    name: String,
    isAvailable: Boolean,
    private val pageCount: Int,
    private val author: String
) : LibraryItem(id, name, isAvailable), TakableHome, Readable {
    override fun printFullInfo() {
        println("Книга: $name ($pageCount стр.) автора: $author с id: '$id' доступна: ${if (isAvailable) "Да" else "Нет"}")
    }

    // Реализация интерфейсов
    override fun takeItem() {
        if (isAvailable) {
            println("Книга '$name' взята домой.")
            isAvailable = false
        } else {
            println("Книга '$name' недоступна для взятия домой.")
        }
        println("Информация об объекте: ")
        printShortInfo()
    }
}

class Newspaper(
    id: Int,
    name: String,
    isAvailable: Boolean,
    private val issueNumber: Int
) : LibraryItem(id, name, isAvailable), Readable {
    override fun printFullInfo() {
        println("выпуск: $issueNumber газеты $name с id: '$id' доступен: ${if (isAvailable) "Да" else "Нет"}")
    }
}

class Disc(
    id: Int,
    name: String,
    isAvailable: Boolean,
    private val discType: String
) : LibraryItem(id, name, isAvailable), TakableHome {
    override fun printFullInfo() {
        println("$discType $name с id: '$id' доступен: ${if (isAvailable) "Да" else "Нет"}")
    }

    override fun takeItem() {
        if (isAvailable) {
            println("Диск '$name' взят домой.")
            isAvailable = false
        } else {
            println("Диск '$name' недоступна для взятия домой.")
        }
        println("Информация об объекте: ")
        printShortInfo()
    }
}

abstract class LibraryItem(
    val id: Int,
    val name: String,
    var isAvailable: Boolean
) : Returnable {
    fun printShortInfo() {
        println("$name, Доступность: ${if (isAvailable) "Да" else "Нет"}")
    }

    abstract fun printFullInfo()

    override fun returnItem() {
        if (!isAvailable) {
            println("'$name' возвращен в библиотеку.")
            isAvailable = true
        } else {
            println("'$name' уже доступен в библиотеке.")
        }
        println("Информация об объекте: ")
        printShortInfo()
    }
}

fun printShortInfo(items: List<LibraryItem>) {
    items.forEachIndexed { index, item ->
        print("${index + 1}.")
        item.printShortInfo()
    }
}

fun printFullInfo(item: LibraryItem) {
    item.printFullInfo()
}

class Library {
    private val items = mutableListOf<LibraryItem>()

    fun addItem(item: LibraryItem) {
        items.add(item)
    }

    fun addItems(vararg items: LibraryItem) {
        this.items.addAll(items)
    }

    fun getBooks(): List<Book> {
        return items.filterIsInstance<Book>()
    }

    fun getNewspapers(): List<Newspaper> {
        return items.filterIsInstance<Newspaper>()
    }

    fun getDiscs(): List<Disc> {
        return items.filterIsInstance<Disc>()
    }
}
