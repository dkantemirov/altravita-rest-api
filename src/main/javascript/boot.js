import {Application, ApplicationStateType, Configuration, Loggable, LogLevelType, LogOutputType} from "./api"
import * as Controllers from "./controllers"

const states = {
    [ApplicationStateType.AUTH]: Controllers.AuthController,
    [ApplicationStateType.DEFAULT]: Controllers.DefaultController
};

export default class Boot extends Loggable {
    constructor({name, version, configuration}) {
        const logOutputType = LogOutputType.apply(configuration.logOutput);
        const logLevelType = LogLevelType.apply(configuration.logLevel);
        super({logLevelType, logOutputType});
        const apiHostname = configuration.apiHostname;
        const apiPort = configuration.apiPort;
        const apiPath = configuration.apiPath;
        const apiAuthHeader = configuration.apiAuthHeader;
        const initialStateType = "initialState" in configuration ? ApplicationStateType.apply(configuration["initialState"]) : ApplicationStateType.DEFAULT;
        const mappedConfiguration = new Configuration(apiHostname, apiPort, apiPath, apiAuthHeader, logLevelType, logOutputType, initialStateType);
        const application = new Application({name, version, configuration: mappedConfiguration, states});
        const runApplication = application.run();
        const success = r => this.log.debug("Boot success.", r);
        const failure = e => this.log.error("Boot failure.", e);
        runApplication
            .then(success)
            .catch(failure)
    }
}