import {SessionStorage} from "./session"
import {Loggable} from "./logging"
import {
    HttpServiceException,
    HttpServiceUnavailableException,
    HttpServiceUnauthorized,
    HttpServiceBadRequestException
} from "./exception"
import http from "./http"
import URI from "urijs"

export class HttpServiceResponse {
    constructor(httpStatusType, json = {}) {
        this.httpStatusType = httpStatusType;
        this.status = httpStatusType.value;
        this.json = json;
    }
    toString() {
        return `[${this.constructor.name}: [${JSON.stringify(this, null, 4)}]]`;
    }
}

export class HttpService extends Loggable {
    constructor(hostname, port, path, authHeader, servicename) {
        super({classPathPrefix : "api/service"});
        const apiURI = path.substr(0, 2) == ".." ?
            new URI(path, location.href) : new URI({protocol: "http", hostname, port, path});
        this.uri = new URI(`${servicename}/`, apiURI);
        this.authHeader = authHeader;
    }

    httpPost({
        method,
        json,
        hasResponseJson = true
    }) {
        return new Promise((resolve, reject) => {
            const {
                uri,
                authHeader,
                newRequest,
                sendRequest,
                readResponseJson,
                processResponseJson,
                processResponse,
                log
            } = this;
            const isAuthenticated = !!SessionStorage.session;
            const chain = {
                newRequest: newRequest(
                    uri,
                    method,
                    json,
                    authHeader,
                    isAuthenticated,
                    log
                ),
                sendRequest
            };
            const chainWithResponseJson = {
                readResponseJson,
                processResponseJson: processResponseJson(isAuthenticated, log)
            };
            const chainWithoutResponseJson = {
                processResponse: processResponse(isAuthenticated, log)
            };
            const runChain = () => {
                return chain.newRequest
                    .then(chain.sendRequest)
            };
            hasResponseJson ?
                runChain()
                    .then(chainWithResponseJson.readResponseJson)
                    .then(chainWithResponseJson.processResponseJson)
                    .then(resolve, reject) :
                runChain()
                    .then(chainWithoutResponseJson.processResponse)
                    .then(resolve, reject)
        });
    }

    newRequest(uri, methodName, json, authHeader, isAuthenticated, log) {
        return new Promise(resolve => {
            const url = new URI(methodName, uri).toString();
            const method = http.MethodType.POST.value;
            const body = json ? JSON.stringify(json) : undefined;
            const mode = http.ModeType.CORS.value;
            const headers = new Headers({
                [http.HeaderType.Content_Type.value]: http.MIMEType.JSON.value
            });
            if (isAuthenticated) {
                const {id:token} = SessionStorage.session;
                headers.append(authHeader, token);
            }
            const settings = body ? {method, headers, mode, body} : {method, headers, mode};
            const request = new Request(url, settings);
            log.debugWith(() => {
                const h = headers => {
                    let str = "[";
                    for (let key of headers.keys()) {
                        const name = key;
                        const value = headers.get(key);
                        str += "\n    " + JSON.stringify({name, value});
                    }
                    return str + "\n  ]";
                };
                const httpMethodRequestStr = `HTTP ${method} request [`;
                const urlStr = `\n  url: "${url}",`;
                const headersStr = `\n  headers: ${h(headers)}`;
                return httpMethodRequestStr + urlStr + headersStr + "\n.";
            });
            resolve(request);
        });
    }

    sendRequest(request) {
        return new Promise((resolve, reject) => {
            const chain = {
                fetch: fetch(request),
                mapResponse: response => ({response, request}),
                mapException: exception => reject(new HttpServiceUnavailableException(exception))
            };
            chain.fetch
                .then(chain.mapResponse)
                .then(resolve)
                .catch(chain.mapException)
        });
    }

    readResponseJson({response, request}) {
        return new Promise((resolve, reject) => {
            const chain = {
                json: response.json(),
                mapResponse: json => ({json, response, request}),
                mapException: exception => reject(new HttpServiceBadRequestException(exception))
            };
            chain.json
                .then(chain.mapResponse)
                .then(resolve)
                .catch(chain.mapException)
        });
    }

    processResponseJson(isAuthenticated, log) {
        return ({json, response: {status: httpStatus}, request}) => {
            const httpStatusType = http.StatusType.apply(httpStatus);
            switch (httpStatusType) {
                case http.StatusType.UNAUTHORIZED:
                    if (isAuthenticated) throw new HttpServiceUnauthorized;
                    else throw new HttpServiceException({httpStatusType, message: json.message});
                case http.StatusType.GATEWAY_TIMEOUT:
                    throw new HttpServiceException({httpStatusType, message: json.message});
                default:
                    const {url, method} = request;
                    const response = new HttpServiceResponse(httpStatusType, json);
                    log.debugWith(() => {
                        const h = headers => {
                            let str = "[";
                            for (let key of headers.keys()) {
                                const name = key;
                                const value = headers.get(key);
                                str += "\n    " + JSON.stringify({name, value});
                            }
                            return str + "\n  ]";
                        };
                        const httpMethodResponseStr = `HTTP ${method} response [`;
                        const urlStr = `\n  url: "${url}",`;
                        const responseStr = `\n  response: ${response}`;
                        return httpMethodResponseStr + urlStr + responseStr + "\n.";
                    });
                    return response;
            }
        }
    }

    processResponse(isAuthenticated, log) {
        return ({response: {status: httpStatus}, request}) => {
            const httpStatusType = http.StatusType.apply(httpStatus);
            switch (httpStatusType) {
                case http.StatusType.UNAUTHORIZED:
                    if (isAuthenticated) throw new HttpServiceUnauthorized;
                    else throw new HttpServiceException({httpStatusType});
                case http.StatusType.GATEWAY_TIMEOUT:
                    throw new HttpServiceException({httpStatusType});
                default:
                    const {url, method} = request;
                    const response = new HttpServiceResponse(httpStatusType);
                    log.debugWith(() => {
                        const h = headers => {
                            let str = "[";
                            for (let key of headers.keys()) {
                                const name = key;
                                const value = headers.get(key);
                                str += "\n    " + JSON.stringify({name, value});
                            }
                            return str + "\n  ]";
                        };
                        const httpMethodResponseStr = `HTTP ${method} response [`;
                        const urlStr = `\n  url: "${url}",`;
                        const responseStr = `\n  response: ${response}`;
                        return httpMethodResponseStr + urlStr + responseStr + "\n.";
                    });
                    return response;
            }
        }
    }
}