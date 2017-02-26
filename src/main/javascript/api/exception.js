import {ExtendableException} from "./extend"
import {HttpStatusType} from "./http"
import {Toastr} from "./lib"

export class ApplicationException extends ExtendableException {
    constructor({message = "Неизвестная ошибка приложения.", cause, show = true}) {
        super(message);
        this.cause = cause;
        this.show = true;
    }
}

export class ConfigurationException extends ApplicationException {
    constructor({message = "Invalid configuration."}) {
        super({message});
    }
}

export class ConfigurationParameterException extends ApplicationException {
    constructor(parameter) {
        const message = `Configuration parameter "${parameter}" invalid or not set.`;
        super({message});
    }
}

export class ControllerException extends ApplicationException {
    constructor({message = "Unknown controller exception." , cause}) {
        super({message, cause});
    }
}

export class HttpServiceException extends ApplicationException {
    constructor({httpStatusType, message = "Unknown http service exception.", cause}) {
        super({message, cause});
        this.httpStatusType = httpStatusType;
    }
}

export class HttpException extends ExtendableException {
    constructor({message = "Unknown http exception."}) {
        super(message);
    }
}

export class HttpServiceBadRequestException extends HttpServiceException {
    constructor(cause) {
        const httpStatusType = HttpStatusType.BAD_REQUEST;
        const message = "Неверный запрос к серверу.";
        super({httpStatusType, message, cause})
    }
}

export class HttpServiceUnavailableException extends HttpServiceException {
    constructor(cause) {
        const httpStatusType = HttpStatusType.SERVICE_UNAVAILABLE;
        const message = "Не удается установить соединение с сервером.";
        super({httpStatusType, message, cause})
    }
}

export class HttpServiceUnauthorized extends HttpServiceException {
    constructor() {
        const httpStatusType = HttpStatusType.UNAUTHORIZED;
        const message = "Cессия истекла или возникла ошибка на сервере.";
        super({httpStatusType, message})
    }
}

const ExceptionHandler = base => class extends base {
    handleException(log) {
        return exception => {
            const {cause, show} = exception;
            log.error(exception, cause);
            switch (exception.constructor) {
                case ApplicationException:
                    break;
                case ControllerException:
                    break;
                case HttpServiceException:
                    break;
                case HttpServiceUnavailableException:
                    break;
                case HttpServiceUnauthorized:
                    break;
                default:
                    throw new ApplicationException({cause})
            }
            if (show) {
                Toastr.error(exception.message, "Ошибка в приложении.");
                Toastr.options = {
                    closeButton: true,
                    debug: false,
                    progressBar: true,
                    preventDuplicates: true,
                    positionClass: "toast-top-right",
                    showDuration: 400,
                    hideDuration: 1000,
                    timeOut: 7000,
                    extendedTimeOut: 1000,
                    showEasing: "swing",
                    hideEasing: "linear",
                    showMethod: "fadeIn",
                    hideMethod: "fadeOut"
                }
            }
        }
    }
};

export {ExceptionHandler}
