import {Type} from "./type"
import log4javascript from "log4javascript"

export const LogLevelType = {
    ALL: Symbol("ALL"),
    TRACE: Symbol("TRACE"),
    DEBUG: Symbol("DEBUG"),
    INFO: Symbol("INFO"),
    WARN: Symbol("WARN"),
    ERROR: Symbol("ERROR"),
    FATAL: Symbol("FATAL"),
    OFF: Symbol("OFF"),
};
Type(LogLevelType);

export const LogOutputType = {
    APPLICATION: Symbol("Application"),
    BROWSER_CONSOLE: Symbol("BrowserConsole"),
    SERVER: Symbol("Server"),
};
Type(LogOutputType);

let initialized = false;
let logger = log4javascript.getLogger("main");
let layout = new log4javascript.PatternLayout("[%d] [%p] [%m]");
let appender;

function logMessage(classPath, message) {
    return classPath ? `path: [${classPath}], message: [${message}]` : `[${message}]`;
}
function logException(classPath, exception, cause) {
    const classPathStr = classPath ? `path: [${classPath}], ` : "";
    const exceptionStr = `\n  exception: [${exception.toString()}]`;
    const causeStr = cause ? `,\n  cause: [${cause.toString()}]` : "";
    return classPathStr + exceptionStr + causeStr + ".";
}

class LoggingAdapter {
    constructor(classPath, logOutputType, logLevelType) {
        this.classPath = classPath;
        if (!initialized) {
            switch (logOutputType) {
                case LogOutputType.APPLICATION:
                    appender = new log4javascript.InPageAppender;
                    break;
                case LogOutputType.SERVER:
                    // todo: add logging to server
                    appender = new log4javascript.InPageAppender;
                    break;
                default:
                    appender = new log4javascript.BrowserConsoleAppender;
            }
            appender.setLayout(layout);
            logger.addAppender(appender);
            switch (logLevelType) {
                case LogLevelType.ALL:
                    logger.setLevel(log4javascript.Level.ALL);
                    break;
                case LogLevelType.TRACE:
                    logger.setLevel(log4javascript.Level.TRACE);
                    break;
                case LogLevelType.DEBUG:
                    logger.setLevel(log4javascript.Level.DEBUG);
                    break;
                case LogLevelType.INFO:
                    logger.setLevel(log4javascript.Level.INFO);
                    break;
                case LogLevelType.WARN:
                    logger.setLevel(log4javascript.Level.WARN);
                    break;
                case LogLevelType.ERROR:
                    logger.setLevel(log4javascript.Level.ERROR);
                    break;
                case LogLevelType.FATAL:
                    logger.setLevel(log4javascript.Level.FATAL);
                    break;
                case LogLevelType.OFF:
                    logger.setLevel(log4javascript.Level.OFF);
                    break;
                default:
                    logger.setLevel(log4javascript.Level.WARN);
            }
            initialized = true;
        }
    }

    trace(message, exception) {
        const m = logMessage(this.classPath, message);
        if (exception) logger.trace(m, exception);
        else logger.trace(m);
    }

    debug(message, exception) {
        const m = logMessage(this.classPath, message);
        if (exception) logger.debug(m, exception);
        else logger.debug(m);
    }

    debugWith(func) {
        const m = logMessage(this.classPath, func());
        logger.debug(m);
    }

    info(message, exception) {
        const m = logMessage(this.classPath, message);
        if (exception) logger.info(m, exception);
        else logger.info(m);
    }

    warn(message, exception) {
        const m = logMessage(this.classPath, message);
        if (exception) logger.warn(m, exception);
        else logger.warn(m);
    }

    error(exception, cause) {
       logger.error(logException(this.classPath, exception, cause));
    }

    fatal(message, exception) {
        logger.fatal(logException(this.classPath, exception, cause));
    }
}

export class Loggable {
    constructor({
        classPathPrefix = undefined,
        logOutputType = LogOutputType.BROWSER_CONSOLE,
        logLevelType = LogLevelType.ERROR
    }) {
        const className = this.constructor.name;
        this.log = new LoggingAdapter(
            classPathPrefix ? `${classPathPrefix}/${className}` : classPathPrefix,
            logOutputType,
            logLevelType
        );
    }
}