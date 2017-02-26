import {$} from "./lib"
import {Loggable} from "./logging"
import {ScriptElement, LinkElement} from "./html"
import {ControllerException} from "./exception"
import {ResourceType} from "./resource"
import React from "react"
import ReactDOM from "react-dom"

let templateRendered = false;

export class Controller extends Loggable {
    constructor(application, viewClass, model = {}, resources = new Set, renderCallbacks = []) {
        super({classPathPrefix : "api/controller"});
        this.application = application;
        this.model = model;
        this.resources = resources;
        this.renderCallbacks = renderCallbacks;
        this.loadedResources = new Set;
        this.view = React.createElement(viewClass, {controller: this, model});
    }

    run() {
        const {application, resources, loadedResources, newResourceRequest, view, log} = this;
        const newBunchResourcesRequests = this.newBunchResourcesRequests(application, resources, loadedResources, newResourceRequest);
        const chain = {
            renderReact: () => new Promise((resolve, reject) => ReactDOM.render(view, application.container, resolve)),
            renderTemplate: () => {
                this.renderTemplate();
                // todo: make async logging
                if (loadedResources.sizes) {
                    this.log.debug(() => {
                        let str = "";
                        let i = 1;
                        loadedResources.forEach(r => {
                            str += `\n ${i}: ${r}`;
                            i++;
                        });
                        return `Resources loaded: [${str}]`;
                    });
                }
                log.info(`View rendering chain completed.`);
            },
            handleException(cause) {
                throw new ControllerException({cause});
            }
        };
        return newBunchResourcesRequests
            .then(chain.renderReact)
            .then(chain.renderTemplate)
            .catch(chain.handleException);
    };

    newBunchResourcesRequests(application, resources, loadedResources, newResourceRequest) {
        const bunch = [...resources]
            .filter(application.loadedResources.hasNot)
            .map(r => {
                application.loadedResources.add(r);
                loadedResources.add(r);
                return newResourceRequest(r)
            });
        return Promise.all(bunch);
    }

    newResourceRequest(resource) {
        return new Promise((resolve, reject) => {
            const resourceType = ResourceType.get(resource);
            let element;
            switch (resourceType) {
                case ResourceType.JS:
                    element = new ScriptElement(resource, {resolve, reject});
                    break;
                case ResourceType.CSS:
                    element = new LinkElement(resource, {resolve, reject});
                    break;
            }
        });
    }

    combineResources(resources, ...objects) {
        objects.forEach(object =>
            resources = resources.concat(Object.keys(object).map((k) => object[k]))
        );
        return resources;
    };

    renderTemplate() {
        const render = () => {
            let topOffset = 50;
            const width = (window.innerWidth > 0) ? window.innerWidth : screen.width;
            if (width < 768) {
                topOffset = 100; // 2-row-menu
            }

            var height = ((this.window.innerHeight > 0) ? this.window.innerHeight : this.screen.height) - 1;
            height = height - topOffset;
            if (height < 1) height = 1;
            if (height > topOffset) {
                $("#page-wrapper").css("min-height", (height) + "px");
            }

            var url = window.location;
            var element = $('ul.nav a').filter(function () {
                return this.href == url;
            }).addClass('active').parent();

            while (true) {
                if (element.is('li')) {
                    element = element.parent().addClass('in').parent();
                } else {
                    break;
                }
            }
        };

       // window.onresize = render;
    }
}