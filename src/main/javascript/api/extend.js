Object.defineProperty(Array.prototype, "first", {
    get() {
        return this[0];
    }
});

Object.defineProperty(Array.prototype, "last", {
    get() {
        return this[this.length - 1];
    }
});

Object.defineProperty(Array.prototype, "notLast", {
    get() {
        return v => !this.last == v;
    }
});

Object.defineProperty(Set.prototype, "hasNot", {
    get() {
        return v => !this.has(v);
    }
});

Object.defineProperty(Symbol.prototype, "value", {
    get() {
        return this.toString().slice(7, -1);
    }
});

export class ExtendableException extends Error {
    constructor(message) {
        super(message);
        this.name = this.constructor.name;
        this.message = message;
        if (typeof Error.captureStackTrace === 'function') Error.captureStackTrace(this, this.constructor);
        else this.stack = (new Error(message)).stack;
    }
}