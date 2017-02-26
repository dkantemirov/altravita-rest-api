import {$, Bootstrap} from "./lib"
import {ApplicationStateType} from "./type"
import {SessionStorage} from "./session"
import {Resource} from "./resource"
import {Loggable} from "./logging"
import {ExceptionHandler, ConfigurationParameterException} from "./exception"

export class Configuration {
    constructor(apiHostname,
                apiPort,
                apiPath,
                apiAuthHeader,
                logLevelType,
                logOutputType,
                initialStateType = ApplicationStateType.DEFAULT) {
        const exception = p => {throw new ConfigurationParameterException(p)};
        // required
        if (apiPath.substr(0, 2) == "..") {
            this.apiPath = apiPath ? apiPath : exception("apiPath");
        } else {
            this.apiHostname = apiHostname ? apiHostname : exception("apiHostname");
            this.apiPort = apiPort ? apiPort : exception("apiPort");
            this.apiPath = apiPath ? apiPath : exception("apiPath");
        }
        this.apiAuthHeader = apiAuthHeader ? apiAuthHeader : exception("apiAuthHeader");
        this.logLevelType = logLevelType ? logLevelType : exception("logLevel");
        this.logOutputType = logOutputType ? logOutputType : exception("logOutput");
        // optional
        this.initialStateType = initialStateType;
    }
}

class Stateful extends Loggable {
    constructor(states, initialStateType, logOutputType, logLevelType) {
        super({classPathPrefix : "api/core", logOutputType, logLevelType});
        this.states = states;
        this.currentStateType = ApplicationStateType.STARTED;
        this.initialStateType = initialStateType;
    }

    changeState(application, log) {
        return newStateType => new Promise((resolve, reject) => {
            const {currentStateType, states} = application;
            if (currentStateType != newStateType) {
                const Controller = states[newStateType];
                const chain = {
                    runController: new Controller(application).run(),
                    update() {
                        application.currentStateType = newStateType;
                        return application.currentStateType;
                    },
                    log: currentStateType => log.info(`Change state: "${currentStateType.value}".`)
                };
                chain.runController
                    .then(chain.update)
                    .then(chain.log)
                    .then(resolve, reject)
            } else resolve()
        });
    }
}

export class Application extends ExceptionHandler(Stateful) {
    constructor({
        name = "Name",
        version = "Version",
        configuration = new Configuration,
        states = {}
    }) {
        const {
            initialStateType,
            logOutputType,
            logLevelType
        } = configuration;
        super(states, initialStateType, logOutputType, logLevelType);
        this.name = name;
        this.version = version;
        this.configuration = configuration;
        this.resources = new Resource().predefinedResources;
        this.loadedResources = new Set;
        this.container = document.createElement("div");
    }

    run() {
        const {name, version, configuration: {initialStateType}, currentStateType, states, container, log} = this;
        const runApplication = this.runApplication(name, version, initialStateType,  container, log);
        const changeState = this.changeState(this, log);
        const handleException = this.handleException(log);
        return runApplication
            .then(changeState)
            .catch(handleException)
    };

    runApplication(name, version, initialStateType, container, log) {
        return new Promise(resolve => {
            const self = this;
            container.id = "app";
            container.className = "skin-1";
            document.body.appendChild(container);
            log.info(`Run [${name}-${version}].`);
            const session = SessionStorage.session;
            const authenticated = () => {
                log.info("Authenticated.");
                return initialStateType;
            };
            const authenticationRequired = () => {
                log.warn("Authentication required.");
                return ApplicationStateType.AUTH;
            };
            resolve(session ? authenticated() : authenticationRequired());
        });
    }
}

