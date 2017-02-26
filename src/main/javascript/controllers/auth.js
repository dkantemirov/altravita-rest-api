import {Controller, ExceptionHandler, ApplicationStateType, Session, SessionStorage} from "../api"
import {AuthService} from "../services"
import {AuthView} from "../views"

class AuthResponse {
    constructor(rejected, message) {
        this.rejected = rejected;
        this.message = message;
    }
}

export class AuthController extends ExceptionHandler(Controller) {
    constructor(application) {
        super(application, AuthView);
        const {apiHostname, apiPort, apiPath, apiAuthHeader} = application.configuration;
        this.authService = new AuthService(apiHostname, apiPort, apiPath, apiAuthHeader);
    }

    signIn(username, password, remember, stateTypeAfter = this.application.initialStateType) {
        return new Promise((resolve, reject) => {
            const {
                application,
                authService,
                interceptAuthException,
                handleException,
                log
            } = this;
            const {changeState, currentStateType} = application;
            const chain = {
                signIn: authService.signIn(username, password, remember),
                createSession: ({json: {token}}) => SessionStorage.store(new Session(token), remember),
                changeState: () => changeState(application, log)(stateTypeAfter),
                mapResponse: () => new AuthResponse(false),
                interceptAuthException: interceptAuthException(handleException, log)
            };
            return chain.signIn
                .then(chain.createSession)
                .then(chain.changeState)
                .then(chain.mapResponse)
                .catch(chain.interceptAuthException)
                .then(resolve, reject)
        });
    }

    interceptAuthException(handleException, log) {
        return exception => {
            exception.show = false;
            handleException(log)(exception);
            return new AuthResponse(true, exception.message);
        };
    };

    signOut() {
        return new Promise((resolve, reject) => {
            const {
                application,
                authService,
                handleException,
                log
            } = this;
            const {changeState, currentStateType} = application;
            const async = {
                signOut: authService.signOut(),
                changeState: changeState(application, log)(ApplicationStateType.AUTH),
                removeSession: new Promise(resolve => {
                    SessionStorage.remove();
                    resolve();
                }),
                handleException: handleException(log)
            };
            Promise.all(
                [
                    async.signOut,
                    async.changeState,
                    async.removeSession
                ])
                .catch(async.handleException)
                .then(resolve, reject)
        });
    }
}

const SignOut = base => class extends base {
    signOut() {
        new AuthController(this.application).signOut();
    }
};

export {SignOut}

