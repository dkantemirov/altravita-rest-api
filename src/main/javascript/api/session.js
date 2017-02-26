import Cookie from "browser-cookies"

export class Session {
    constructor(id) {
        this.id = id;
    }
}

const cookieName = "token";
const defaultExpiration = 1296000;
const sessionCookie = Cookie.get(cookieName);
let session = sessionCookie ? new Session(sessionCookie) : undefined;

export class SessionStorage {
    static store(newSession, remember = true) {
        const id = newSession.id;
        if (remember) Cookie.set(cookieName, id, {expires: defaultExpiration});
        else Cookie.set(cookieName, id);
        session = newSession;
        return session;
    };

    static get session() {
        return session;
    }

    static remove() {
        Cookie.erase(cookieName);
        session = undefined;
    }
}