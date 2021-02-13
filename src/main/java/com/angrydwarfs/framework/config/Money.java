/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.angrydwarfs.framework.config;

import com.angrydwarfs.framework.exceptions.IllegalMoneyFormatException;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Класс для работы с денежными единицами.
 * @param "strValue" - денежная сумма - в строковом формате
 * @param "currency" - валюта денежной суммы - по умолчанию RUR
 *
 */
public class Money {
    private static final Currency CURRENCY = Currency.getInstance("RUB");
    private BigDecimal value;
    private Currency currency;
    private int precision;

    /**
     * Конструктор с произвольной валютой
     * @param strValue строковое значение денежной суммы
     * @param currency валюта денежной суммы
     * @throws IllegalMoneyFormatException
     */
    public Money(String strValue, Currency currency) throws IllegalMoneyFormatException {
        this.currency = currency;
        this.precision = currency.getDefaultFractionDigits();
        if (isNumeric(strValue)) {
            this.value = new BigDecimal(strValue);
            if(value.compareTo(new BigDecimal(0)) < 0) {
                throw new IllegalMoneyFormatException("Данные '" + strValue + "' содержат отрицательные значения.", strValue);
            }
        } else {
            throw new IllegalMoneyFormatException("Данные '" + strValue + "' содержат не числовые значения, \n либо разделитель чисел не точка!", strValue);
        }
    }

    /**
     * Конструктор с валютой по умолчанию - рубли RUR
     * @param strValue строковое значение денежной суммы
     * @throws IllegalMoneyFormatException исключение в случае невозможности привести строку денежной суммы в формат денег
     */
    public Money(String strValue) throws IllegalMoneyFormatException{
        this(strValue, CURRENCY);
    }

    /**
     * Установка округления денежной единицы, до двух знаков после запятой.
     * @return Округленное до двух знаков после запятой значение.
     */
    public BigDecimal getValue() {
        return value.setScale(this.precision, BigDecimal.ROUND_DOWN);
    }

    /**
     * @return Текущее значение валюты в которой производятся расчеты.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Проверка вводимого строкового значения денежной суммы на соотвествие формату денег.
     * @param strNum - строкове значение денежной суммы
     * @return true - если строка соотвествует формату, false если нет.
     */
    private boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * Реализация метода умножения денежной суммы на целое число.
     * @param intValue - значение целого числа на которое умножается денежная сумма
     * @return новое значение денежной суммы
     * @throws IllegalMoneyFormatException исключение в случае невозможности привести строку денежной суммы в формат денег
     */
    public Money multiplyByInt(int intValue) throws IllegalMoneyFormatException {
        Money newValue = new Money (String.valueOf(intValue));
        newValue.value = this.value.setScale(this.precision, BigDecimal.ROUND_DOWN).multiply(newValue.value.setScale(this.precision, BigDecimal.ROUND_DOWN));
        newValue.value = newValue.value.setScale(this.precision, BigDecimal.ROUND_DOWN);
        return newValue;
    }

    /**
     * Реализация метода деления денежной суммы на целое число.
     * @param intValue - значение целого числа на которое делится денежная сумма
     * @return новое значение денежной суммы
     * @throws IllegalMoneyFormatException исключение в случае невозможности привести строку денежной суммы в формат денег
     */
    public Money divideByInt(int intValue) throws IllegalMoneyFormatException {
        Money newValue = new Money (String.valueOf(intValue));
        newValue.value = this.value.setScale(this.precision, BigDecimal.ROUND_DOWN).divide(newValue.value.setScale(this.precision, BigDecimal.ROUND_DOWN));
        newValue.value = newValue.value.setScale(this.precision, BigDecimal.ROUND_DOWN);
        return newValue;
    }

    /**
     * Реализация метода суммирования двух денежных суммм.
     * @param moneyValue - денежной суммы к которой прибавляется денежная сумма
     * @return новое значение денежной суммы
     * @throws IllegalMoneyFormatException исключение в случае невозможности привести строку денежной суммы в формат денег
     */
    public Money addMoney(String moneyValue) throws IllegalMoneyFormatException {
        Money newValue = new Money (String.valueOf(moneyValue));
        newValue.value = value.setScale(precision, BigDecimal.ROUND_DOWN).add(newValue.value.setScale(newValue.precision, BigDecimal.ROUND_DOWN));
        return newValue;
    }

    /**
     * Реализация метода вычитания двух денежных суммм.
     * @param moneyValue - денежной суммы из которой вычитается денежная сумма
     * @return новое значение денежной суммы
     * @throws IllegalMoneyFormatException исключение в случае невозможности привести строку денежной суммы в формат денег
     */
    public Money subtractMoney(String moneyValue) throws IllegalMoneyFormatException {
        Money newValue = new Money (String.valueOf(moneyValue));
        newValue.value = value.setScale(precision, BigDecimal.ROUND_DOWN).subtract(newValue.value.setScale(newValue.precision, BigDecimal.ROUND_DOWN));
        return newValue;
    }

    @Override
    public String toString() {
        return this.value.toString() + " " + this.currency.toString();
    }

}
