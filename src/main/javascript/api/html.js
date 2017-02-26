export const ElementType = {
    SCRIPT: Symbol("script"),
    LINK: Symbol("link"),
    HEAD: Symbol("head"),
    BODY: Symbol("body")
};

class Element {
    constructor(elementType, attributes = new Set) {
        const element = document.createElement(htmlElementType.value);
        attributes.forEach(a => element[a.name] = a.value);
        this.element = element;
    }
}

export class ScriptElement extends Element {
    constructor(url, {resolve, reject}) {
        super(ElementType.SCRIPT, new Set([
            new SrcAttribute(url),
            new AsyncAttribute
        ]));
        const {element, callbacks} = this;
        callbacks(resolve, reject);
        document.body.appendChild(element);
    }

    callbacks(resolve, reject) {
        let {element: {onload, onerror}} = this;
        onload = resolve;
        onerror = reject;
    }
}

export class LinkElement extends Element {
    constructor(url, {resolve, reject}) {
        super(ElementType.LINK, new Set([
            new HrefAttribute(url),
            new TypeAttribute,
            new RelAttribute
        ]));
        const {element} = this;
        callbacks(resolve, reject);
        document.head.appendChild(element);
    }

    callbacks(resolve, reject) {
        let {element: {onload, onerror}} = this;
        onload = resolve;
        onerror = reject;
    }
}

export const AttributeType = {
    SRC: Symbol("src"),
    HREF: Symbol("href"),
    ASYNC: Symbol("async"),
    TYPE: Symbol("type"),
    REL: Symbol("rel")
};

class Attribute {
    constructor(attributeType, value) {
        this.name = attributeType.value;
        this.value = value;
    }
}

export class SrcAttribute extends Attribute {
    constructor(value) {
        super(AttributeType.SRC, value)
    }
}

export class HrefAttribute extends Attribute {
    constructor(value) {
        super(AttributeType.HREF, value)
    }
}

export class AsyncAttribute extends Attribute {
    constructor(value = true) {
        super(AttributeType.ASYNC, value)
    }
}

export class TypeAttribute extends Attribute {
    constructor(value = "text/css") {
        super(AttributeType.TYPE, value)
    }
}

export class RelAttribute extends Attribute {
    constructor(value = "stylesheet") {
        super(AttributeType.REL, value)
    }
}