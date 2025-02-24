package reducedfractionapp;

/**
 * Класс ReducedFraction представляет собой несократимую дробь.
 * Дробь является неизменяемой (immutable).
 */
public final class ReducedFraction {

    /* =========================== Свойства =============================== */

    /** Числитель дроби */
    private final int numerator;

    /** Знаменатель дроби (никогда не равен нулю) */
    private final int denominator;

    /* =========================== Операции ============================== */

    /* ---------------------------- Порождение ---------------------------- */

    /**
     * Создание дроби с указанием числителя и знаменателя.
     * Если знаменатель отрицательный, числитель изменяется на противоположный, а знаменатель становится положительным.
     * Если числитель или знаменатель не кратны друг другу, дробь сокращается.
     *
     * @param numerator числитель
     * @param denominator знаменатель (должен быть не равен нулю)
     * @throws IllegalArgumentException если знаменатель равен нулю
     */
    public ReducedFraction(int numerator, int denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Знаменатель не может быть равен нулю.");
        }

        int gcd = gcd(numerator, denominator);

        // Установка правильного знака и сокращение дроби
        this.numerator = (denominator < 0) ? -numerator / gcd : numerator / gcd;
        this.denominator = Math.abs(denominator) / gcd;
    }

    /**
     * Создание дроби на основе целого числа.
     * @param whole целое число
     */
    public ReducedFraction(int whole) {
        this(whole, 1);
    }

    /* --------------------- Арифметические операции ---------------------- */

    /**
     * Сложение двух дробей.
     *
     * @param other другая дробь
     * @return новая дробь, представляющая результат сложения
     */
    public ReducedFraction add(ReducedFraction other) {
        int newNumerator = this.numerator * other.denominator + other.numerator * this.denominator;
        int newDenominator = this.denominator * other.denominator;
        return new ReducedFraction(newNumerator, newDenominator);

    }
    //!ОБНОВА НИЖЕ! сложение и вычитание дроби и числа
    /**
     * Сложение дроби с целым числом.
     *
     * @param other целое число
     * @return новая дробь, представляющая результат сложения
     */
    public ReducedFraction add(int other) {
        int newNumerator = this.numerator + (other * this.denominator);
        int newDenominator = this.denominator;
        return new ReducedFraction(newNumerator, newDenominator);
    }


    /**
     * Вычитание двух дробей.
     *
     * @param other другая дробь
     * @return новая дробь, представляющая результат вычитания
     */
    public ReducedFraction subtract(ReducedFraction other) {
        int newNumerator = this.numerator * other.denominator - other.numerator * this.denominator;
        int newDenominator = this.denominator * other.denominator;
        return new ReducedFraction(newNumerator, newDenominator);
    }
    public ReducedFraction subtract(int other) {
        int newNumerator = this.numerator - (other * this.denominator);
        int newDenominator = this.denominator;
        return new ReducedFraction(newNumerator, newDenominator);
    }

    /**
     * Умножение двух дробей.
     *
     * @param other другая дробь
     * @return новая дробь, представляющая результат умножения
     */
    public ReducedFraction multiply(ReducedFraction other) {
        return new ReducedFraction(this.numerator * other.numerator, this.denominator * other.denominator);
    }
    public ReducedFraction multiply(int other) {
        return new ReducedFraction(this.numerator * other, this.denominator);
    }

    /**
     * Деление двух дробей.
     *
     * @param other другая дробь
     * @return новая дробь, представляющая результат деления
     * @throws ArithmeticException если деление на ноль
     */
    public ReducedFraction divide(ReducedFraction other) {
        if (other.numerator == 0) {
            throw new ArithmeticException("Деление на ноль.");
        }
        return new ReducedFraction(this.numerator * other.denominator, this.denominator * other.numerator);
    }
    public ReducedFraction divide(int other) {
        if (other == 0) {
            throw new ArithmeticException("Деление на ноль.");
        }
        return new ReducedFraction(this.numerator, this.denominator * other);
    }

    /* --------------------- Операции сравнения ---------------------- */

    /**
     * Сравнение двух дробей.
     *
     * @param other другая дробь
     * @return 1 если текущая дробь больше, -1 если меньше, и 0 если они равны
     */
    public int compareTo(ReducedFraction other) {
        int leftSide = this.numerator * other.denominator;
        int rightSide = other.numerator * this.denominator;
        return Integer.compare(leftSide, rightSide);
    }
    public int compareTo(int other) {
* @param wdshjcbhdsbchjdsbhcjbdshbcdsbcaooooooooooooooooooooooooo
        // Преобразуем целое число в дробь: integer/1
        int leftSide = this.numerator;
        int rightSide = other * this.denominator;
        return Integer.compare(leftSide, rightSide);
    }

    /**
     * Проверка эквивалентности двух дробей.
     *
     * @param obj объект для сравнения
     * @return true если дроби эквивалентны, false в противном случае
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ReducedFraction) {
            ReducedFraction other = (ReducedFraction) obj;
            return this.numerator == other.numerator && this.denominator == other.denominator;
        }
        if (obj instanceof Integer) {
            int integer = (Integer) obj;
            // Преобразуем целое число в дробь: integer/1
            return this.numerator == integer && this.denominator == 1;
        }
        return false;
    }

    /**
     * Вычисление хэш-кода дроби.
     *
     * @return хэш-код дроби
     */
    @Override
    public int hashCode() {
        return 31 * numerator + denominator;
    }

    /* --------------------- Операции преобразования ---------------------- */

    /**
     * Представить дробь как строку.
     *@param b dshbjchdsbkjckdsjbcjdsbcjkdscbkjd
     * @return строковое представление дроби
     */
    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

    /**
     * Представить дробь как вещественное число.
     *
     * @return дробь в виде числа с плавающей запятой
     */
    public double toDouble() {
        return (double) numerator / denominator;
    }

    /* =========================== Вспомогательные методы ============================== */

    /**
     * Вычисление наибольшего общего делителя (НОД) двух чисел.
     *
     * @param a первое число
     * @param b второе число
     * @return НОД двух чисел
     */
    private static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }
}
