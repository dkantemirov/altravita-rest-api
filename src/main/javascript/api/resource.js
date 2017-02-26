import {Type} from "./type"

import "../../less/template.less"
import "font-awesome-webpack"

export const ResourceType = {
    CSS: Symbol("css"),
    JS: Symbol("js"),
    // Resource -> Type
    get(resource) {
        const fileExtension = resource.split(".").pop();
        this.apply(fileExtension);
    }
};
Type(ResourceType);

export class Resource {
    constructor(jsPath = "static/js/plugins", cssPath = "static/css/plugins") {
        this.predefinedResources = {
            datatable: {
                css: {
                    bootstrap: `${cssPath}/dataTables/dataTables.bootstrap.css`,
                    responsive: `${cssPath}/dataTables/dataTables.responsive.css`,
                    tabletools: `${cssPath}/dataTables/dataTables.tableTools.min.css`,
                },
                js: {
                    core: `${jsPath}/dataTables/jquery.dataTables.js`,
                    bootstrap: `${jsPath}/dataTables/dataTables.bootstrap.js`,
                    responsive: `${jsPath}/dataTables/dataTables.responsive.js`,
                    tabletools: `${jsPath}/dataTables/dataTables.tableTools.min.js`
                }
            },
            icheck: {
                css: `${cssPath}/iCheck/custom.css`,
                js: `${jsPath}/iCheck/icheck.min.js`
            },
            daterangepicker: {
                css: `${cssPath}/daterangepicker/daterangepicker-bs3.css`,
                js: {
                    bootstrapdatepicker: `${jsPath}/datapicker/bootstrap-datepicker.js`,
                    core: `${jsPath}/daterangepicker/daterangepicker.js`
                }
            },
            sweetalert: {
                css: `${cssPath}/sweetalert/sweetalert.css`,
                js: `${jsPath}/sweetalert/sweetalert.min.js`
            },
            jsbarcode: `${jsPath}/jsbarcode/jsbarcode.code128.min.js`
        };
    }
}

