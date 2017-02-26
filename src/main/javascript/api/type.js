import {ExtendableException} from "./extend"

class TypeException extends ExtendableException {
    constructor() {
        super("Invalid type.")
    }
}

// String -> Type
export function Type(typeObj) {
    Object.defineProperty(typeObj, "apply", {
        get() {
            try {
                return value => Object.keys(this)
                    .filter(k => typeof this[k] == "symbol")
                    .map(k => ({symbolName: this[k].value, symbolRef: this[k]}))
                    .filter(({symbolName, symbolRef}) => value == symbolName)
                    .first.symbolRef;
            }
            catch (e) {
                throw new TypeException;
            }
        }
    });
}

export const ApplicationStateType = {
    AUTH: Symbol("auth"),
    DEFAULT: Symbol("default"),
    REPORT: Symbol("report"),
    STARTED: Symbol("started")
};
Type(ApplicationStateType);

