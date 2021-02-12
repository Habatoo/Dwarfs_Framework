package com.angrydwarfs.framework.models;

/**
 * Перечень возможных статусов аккаунта пользователя.
 * @see Status (таблица статусов пользователя).
 * @version 0.001
 * @author habatoo
 */
public enum EStatus {
    COMMON,
    READ_ONLY,
    NO_ACTIVITY,
    BAN,
    CLEAR
}
