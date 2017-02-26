import {Type} from "./type"

const StatusType = {
    ACCEPTED: Symbol(202),
    BAD_GATEWAY: Symbol(502),
    BAD_REQUEST: Symbol(400),
    CONFLICT: Symbol(409),
    CONTINUE: Symbol(100),
    CREATED: Symbol(201),
    EXPECTATION_FAILED: Symbol(417),
    FAILED_DEPENDENCY: Symbol(424),
    FORBIDDEN: Symbol(403),
    GATEWAY_TIMEOUT: Symbol(504),
    GONE: Symbol(410),
    HTTP_VERSION_NOT_SUPPORTED: Symbol(505),
    INSUFFICIENT_SPACE_ON_RESOURCE: Symbol(419),
    INSUFFICIENT_STORAGE: Symbol(507),
    INTERNAL_SERVER_ERROR: Symbol(500),
    LENGTH_REQUIRED: Symbol(411),
    LOCKED: Symbol(423),
    METHOD_FAILURE: Symbol(420),
    METHOD_NOT_ALLOWED: Symbol(405),
    MOVED_PERMANENTLY: Symbol(301),
    MOVED_TEMPORARILY: Symbol(302),
    MULTI_STATUS: Symbol(207),
    MULTIPLE_CHOICES: Symbol(300),
    NETWORK_AUTHENTICATION_REQUIRED: Symbol(511),
    NO_CONTENT: Symbol(204),
    NON_AUTHORITATIVE_INFORMATION: Symbol(203),
    NOT_ACCEPTABLE: Symbol(406),
    NOT_FOUND: Symbol(404),
    NOT_IMPLEMENTED: Symbol(501),
    NOT_MODIFIED: Symbol(304),
    OK: Symbol(200),
    PARTIAL_CONTENT: Symbol(206),
    PAYMENT_REQUIRED: Symbol(402),
    PRECONDITION_FAILED: Symbol(412),
    PRECONDITION_REQUIRED: Symbol(428),
    PROCESSING: Symbol(102),
    PROXY_AUTHENTICATION_REQUIRED: Symbol(407),
    REQUEST_HEADER_FIELDS_TOO_LARGE: Symbol(431),
    REQUEST_TIMEOUT: Symbol(408),
    REQUEST_TOO_LONG: Symbol(413),
    REQUEST_URI_TOO_LONG: Symbol(414),
    REQUESTED_RANGE_NOT_SATISFIABLE: Symbol(416),
    RESET_CONTENT: Symbol(205),
    SEE_OTHER: Symbol(303),
    SERVICE_UNAVAILABLE: Symbol(503),
    SWITCHING_PROTOCOLS: Symbol(101),
    TEMPORARY_REDIRECT: Symbol(307),
    TOO_MANY_REQUESTS: Symbol(429),
    UNAUTHORIZED: Symbol(401),
    UNPROCESSABLE_ENTITY: Symbol(422),
    UNSUPPORTED_MEDIA_TYPE: Symbol(415),
    USE_PROXY: Symbol(305)
};
Type(StatusType);

const MethodType = {
    POST: Symbol("POST"),
    GET: Symbol("GET"),
    HEAD: Symbol("HEAD"),
    PUT: Symbol("PUT"),
    DELETE: Symbol("DELETE"),
    OPTIONS: Symbol("OPTIONS"),
    CONNECT: Symbol("CONNECT"),
};
Type(MethodType);

const ModeType = {
    CORS: Symbol("cors"),
    NO_CORS: Symbol("no-cors"),
    SAME_ORIGIN: Symbol("same-origin"),
    NAVIGATE: Symbol("navigate")
};
Type(ModeType);

const HeaderType = {
    Content_Type: Symbol("Content-Type"),
    Cache_Control: Symbol("Cache-Control"),
    Accept_Charset: Symbol("Accept-Charset"),
    Accept: Symbol("Accept"),
    Allow: Symbol("Allow"),
    Authorization: Symbol("Authorization"),
    User_Agent: Symbol("User-Agent")
};
Type(HeaderType);

export const MIMEType = {
    PDF: Symbol("application/pdf"),
    JSON: Symbol("application/json"),
    JAVASCRIPT: Symbol("application/javascript"),
    JPEG: Symbol("image/jpeg"),
    PNG: Symbol("image/png"),
    HTML: Symbol("text/html")
};
Type(MIMEType);

export default {
    StatusType,
    MethodType,
    ModeType,
    HeaderType,
    MIMEType
}

