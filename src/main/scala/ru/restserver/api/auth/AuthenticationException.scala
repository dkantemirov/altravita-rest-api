package ru.restserver.api.auth

import ru.restserver.api.ServiceException

class AuthenticationException(message: String = "Ошибка авторизации.") extends ServiceException(message)
class UnknownUserOrPasswordIncorrectException(message: String = "Неверное имя пользователя или пароль.") extends AuthenticationException(message)
class PasswordHasExpiredException(message: String = "Истек срок действия пароля.") extends AuthenticationException(message)

