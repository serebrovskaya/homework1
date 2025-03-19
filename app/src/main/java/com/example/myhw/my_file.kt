package com.example.myhw

fun readIntOrNull(): Int? {
    return readlnOrNull()?.toIntOrNull()
}

fun showMenuLib() {
    println("Меню библиотеки:\n"+
            "1. Показать книги\n"+
            "2. Показать газеты\n"+
            "3. Показать диски\n"+
            "4. Кабинет отцифровки\n"+
            "0. Выйти на улицу")
}

fun showMenuStore() {
    println("Меню магазина:\n"+
            "1. Купить книгу\n"+
            "2. Купить газету\n"+
            "3. Купить диск\n"+
            "0. Выйти на улицу")
}

fun gotoLibrary(library: Library){
    showMenuLib()
    lateinit var temp: List<LibraryItem>
    library.run {
        when (readIntOrNull()) {
            1 -> temp = getBooks()
            2 -> temp = getNewspapers()
            3 -> temp = getDiscs()
            4 -> {
                digitize(this)
                gotoLibrary(library)
            }
            0 -> return
        }
        try {
            printShortInfo(temp)
            println("")
            selectItem(temp)
        } catch (e: UninitializedPropertyAccessException) {
            println("Неверный выбор. Попробуйте снова.\n")
        }
        gotoLibrary(library)
    }
}

fun selectItem(items: List<LibraryItem>) {
    print("Выберите номер объекта: ")
    var selectedIndex = readIntOrNull()
    while (true) {
        if (selectedIndex != null && selectedIndex-1 in items.indices) {
            val selectedItem = items[selectedIndex-1]

            println("Вы выбрали: ${selectedItem.name}")
            print("Частное меню:\n" +
                    "1. Взять домой\n" +
                    "2. Читать в читальном зале\n" +
                    "3. Показать подробную информацию\n" +
                    "4. Вернуть\n" +
                    "Выберите действие: ")
            when (readIntOrNull()) {
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
                4 -> selectedItem.backItem()
                else -> println("Неверный выбор.")
            }
            println("\nВернуться:\n" +
                    "0 - Меню библиотеки, 1 - Частное меню для ${selectedItem.name}")
            if (readIntOrNull() == 0)
                break
        } else {
            print("Неверный номер объекта. Попробуйте выбрать номер ещё раз: ")
            selectedIndex = readIntOrNull()
        }
    }
}

fun gotoStore(library: Library){
    showMenuStore()
    lateinit var boughtItem: LibraryItem
    val manager = Manager()
    when (readIntOrNull()) {
        1 -> boughtItem = manager.buy(BookStore())
        2 -> boughtItem = manager.buy(NewspaperStore())
        3 -> boughtItem = manager.buy(DiscStore())
        0 -> return
    }
    try {
        print("Покупка! ")
        printFullInfo(boughtItem)
        println("")
        library.addItem(boughtItem)
    } catch (e: UninitializedPropertyAccessException) {
        println("Неверный выбор. Попробуйте снова.\n")
        gotoStore(library)
    }
}

inline fun <reified T> List<*>.filterByType(): List<T> {
    return filterIsInstance<T>()
}

fun main() {

    val library = Library().apply {
        addItems(
            Book(1867, "Война и мир", true, 5202, "Лев Толстой"),
            Book(1866, "Преступление и наказание", false, 672, "Федор Достоевский"),
            Book(90743, "Маугли", true, 202, "Джозеф Киплинг"),

            Newspaper(303, "Комсомольская правда", true, 123, Month.APRIL),
            Newspaper(923, "Московский комсомолец", false, 456, Month.entries[0]),
            Newspaper(17245, "Сельская жизнь", true, 794, Month.valueOf("DECEMBER")),

            Disc(1975, "Богемская рапсодия", true, "CD"),
            Disc(307, "Дэдпул и Росомаха", true, "DVD"),
            Disc(2001, "Voyage", false, "CD")
        )
    }

    println("Перечень книг: ")
    val allItems: List<LibraryItem> = library.getAll()
    val bookItems = allItems.filterByType<Book>()
    printShortInfo(bookItems)
    println()

    while (true) {
        println("1. Зайти в библиотеку\n"+
                "2. Зайти в магазин\n"+
                "0. Уйти домой (выход)")
        when (readIntOrNull()) {
            1 -> gotoLibrary(library)
            2 -> gotoStore(library)
            0 -> {
                println("Пока!")
                return
            }
            else -> println("Неверный выбор. Попробуйте снова.\n")
        }
    }
}

interface Store<T : LibraryItem> {
    fun sell(): T
}

class BookStore : Store<Book> {
    override fun sell(): Book {
        return Book(1813, "Гордость и предубеждение", true, 512, "Джейн Остин")
    }
}

class NewspaperStore : Store<Newspaper> {
    override fun sell(): Newspaper {
        return Newspaper(1830, "Вестник МГТУ", true, 3, Month.MARCH)
    }
}

class DiscStore : Store<Disc> {
    override fun sell(): Disc {
        return Disc(1996, "Свадьба родителей", true, "DVD")
    }
}

class Manager {
    fun <T : LibraryItem> buy(store: Store<T>): T {
        return store.sell()
    }
}

class Digitizer<in T : LibraryItem, out D> { //На выходе мы получаем диск или другой цифровой носитель.
    fun digitize(item: T): D {
        return Disc(item.id, item.name, true, "CD") as D
    }
}

fun digitize(library: Library){
    println("Выполнить отцифровку:\n" +
            "1. Книги\n" +
            "2. Газеты")
    lateinit var temp: List<LibraryItem>
    library.run {
        when (readIntOrNull()) {
            1 -> temp = getBooks()
            2 -> temp = getNewspapers()
        }
    }
    try {
        printShortInfo(temp)
        print("Выберите номер объекта для отцифровки: ")
        val selectedIndex = readIntOrNull()
        if (selectedIndex != null && selectedIndex-1 in temp.indices) {
            val selectedItem = temp[selectedIndex-1]
            if (selectedItem.isAvailable){
                val digitizer = Digitizer<LibraryItem, Disc>()
                library.addItem(digitizer.digitize(selectedItem))
                println("Создан диск: ${library.getDiscs().last().name} \n")
            }
            else
                println("'${selectedItem.name}' сейчас не доступен для отцифровки.")
        }
    } catch (e: UninitializedPropertyAccessException) {
        println("Неверный выбор. Попробуйте снова.\n")
        digitize(library)
    }

}

interface TakableHome {
    fun takeItem()
}

interface BackableLib {
    fun backItem()
}

interface Readable {
    val name: String
    var isAvailable: Boolean

    fun readItem() {
        if (isAvailable) {
            when (this) {
                is Book -> println("Книга '${name}' с id '${id}' теперь в читальном зале.")
                is Newspaper -> println("Газета '$name' теперь в читальном зале.")
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
    private val issueNumber: Int,
    private val releaseMonth: Month
) : LibraryItem(id, name, isAvailable), Readable {
    override fun printFullInfo() {
        println("выпуск: $issueNumber, ${releaseMonth.russianName} газеты $name с id: '$id' доступен: ${if (isAvailable) "Да" else "Нет"}")
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
) : BackableLib {
    fun printShortInfo() {
        println("$name, Доступность: ${if (isAvailable) "Да" else "Нет"}")
    }

    abstract fun printFullInfo()

    override fun backItem() {
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
        print("${index + 1}. ")
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

    fun getAll(): List<LibraryItem> {
        return items
    }
}

enum class Month(val russianName: String) {
    JANUARY("Январь"),
    FEBRUARY("Февраль"),
    MARCH("Март"),
    APRIL("Апрель"),
    MAY("Май"),
    JUNE("Июнь"),
    JULY("Июль"),
    AUGUST("Август"),
    SEPTEMBER("Сентябрь"),
    OCTOBER("Октябрь"),
    NOVEMBER("Ноябрь"),
    DECEMBER("Декабрь");
}
