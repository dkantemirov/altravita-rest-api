export const $ = window.$ = window.jQuery = require("jquery")

export const Bootstrap = require("bootstrap");
require("bootstrap/dist/css/bootstrap.css");
Bootstrap.$ = $;

export const MetisMenu = require("metismenu");
require("metismenu/dist/metisMenu.css");
MetisMenu.$ = $;

require("../../resources/bundle/css/plugins/toastr/toastr.min.css");
export const Toastr = require("../../resources/bundle/js/plugins/toastr/toastr.min.js");