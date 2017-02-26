import "babel-polyfill";
import Boot from "./src/main/javascript/boot";

/**
 * Application configuration settings.
 *  initialState -> Optional, one of [auth, default, report, ...].
 *  apiHostname -> Optional, String value.
 *  apiPort -> Optional, String value.
 *  apiPath -> Required, String value.
 *  logOutput -> Required, one of [Boot, BrowserConsole, Server].
 *  logLevel -> Required, one of [ALL, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF].
 */
new Boot({name, version, configuration});