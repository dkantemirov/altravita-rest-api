import {HttpService} from "../api"

export class AuthService extends HttpService {
    constructor(hostname, port, path, authHeader) {
        super(hostname, port, path, authHeader, "auth");
    }

    signIn(login, password, remember = true) {
        return this.httpPost({method: "sign-in", json: {login, password}})
    }

    signOut() {
        return this.httpPost({method: "sign-out", hasResponseJson: false})
    }
}